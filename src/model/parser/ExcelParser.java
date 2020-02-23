package model.parser;

import model.Columns;
import model.ExcelProductDetails;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.HashMap;
import java.util.Iterator;

public class ExcelParser
{

    private final XSSFWorkbook workbook;
    private final HashMap<String, ExcelProductDetails> excelData;

    private int totalRows;

    public ExcelParser(XSSFWorkbook _workbook)
    {
        excelData =  new HashMap<>();
        totalRows = -1;
        workbook = _workbook;
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
            String bVal = GetBarcode(row.getCell(Columns.BARCODE));
            Double qVal = GetNumericValue(row.getCell(Columns.QUANTITY));
            Double lastPrcPr = GetNumericValue(row.getCell(Columns.LAST_PRICE));

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

        ExcelProductDetails v =  new ExcelProductDetails(quantity,lastPrcPr,currentRow);
        excelData.put(barcode,v);
    }

    public HashMap<String, ExcelProductDetails> GetExcelData()
    {
        if(excelData.size() == 0)
            LoadDataFromExcel();

        return excelData;
    }
    public int GetTotalRows()
    {
        return totalRows;
    }

    public XSSFWorkbook GetWorkbook()
    {
        return workbook;
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
        }

        return qtVal;
    }
}
