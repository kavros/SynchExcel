package model.excel.parser;

import model.excel.constants.ExcelColumns;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.Iterator;

public class ExcelParser
{

    private final XSSFWorkbook workbook;
    private ExcelData data;
    private int totalRows;

    public ExcelParser(XSSFWorkbook _workbook)
    {
        data = new ExcelData();
        totalRows = -1;
        workbook = _workbook;
    }

    public ExcelData GetExcelData()
    {
        if(data.Size() == 0)
            LoadDataFromExcel();

        return data;
    }

    private void LoadDataFromExcel()
    {
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        int currRow = sheet.getFirstRowNum() - 1;
        Boolean reachedHook = false;

        while(rowIterator.hasNext() && !reachedHook )
        {
            currRow++;
            Row row = rowIterator.next();
            String bVal = GetBarcode(row.getCell(ExcelColumns.BARCODE));
            Double qVal = GetNumericValue(row.getCell(ExcelColumns.STORAGE_QUANTITY));
            Double lastPrcPr = GetNumericValue(row.getCell(ExcelColumns.LAST_PRICE));

            if (bVal != null && qVal != null)
                Insert(bVal,currRow,qVal,lastPrcPr);

            reachedHook = hasReachTheEnd(bVal);
        }
        totalRows=currRow;
    }

    private void Insert(String barcode, int currentRow,double quantity, Double lastPrcPr)
    {
        if(lastPrcPr == null)
            lastPrcPr = 0.0;

        ExcelRow row = new ExcelRow(currentRow,quantity,lastPrcPr);
        data.Add(row,barcode);
    }

    public int GetTotalRows()
    {
        return totalRows;
    }

    private boolean hasReachTheEnd(String str)
    {
        return ( (str != null) && (str.contains("ΣΥΝΟΛΟ TΕΛΙΚΟ")) );
    }

    private String GetBarcode(Cell c)
    {
        String btVal = null;

        if(c == null)
            return  null;

        if (c.getCellType() == CellType.NUMERIC )
        {
            btVal = String.format ("%.0f",c.getNumericCellValue());
        }
        else if (c.getCellType() == CellType.STRING)
        {
            btVal = c.getStringCellValue();
        }

        return btVal;
    }

    private Double GetNumericValue(Cell c)
    {
        Double qtVal =null;
        if(c==null)
            return null;

        if (c.getCellType() == CellType.NUMERIC )
        {
            qtVal = c.getNumericCellValue();
        }else if(c.getCellType() == CellType.STRING){
            try {
                qtVal = Double.parseDouble(c.getStringCellValue().replace(",", "."));
            }catch (NumberFormatException nfe) {
                return null;
            }
        }

        return qtVal;
    }
}
