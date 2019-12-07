package model;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

public class ExcelParser
{
    public class RowData
    {
        int row;
        Double quantity;
        Double lastPrcPr;
    }

    private XSSFWorkbook workbook;
    private HashMap<String, RowData> excelData;
    private final int bCellNum = 4;     // barcode
    private final int qCellNum = 6;     // quantity
    private final int lastPrcPrCellNum = 2;

    private int totalRows;

    public ExcelParser()
    {
        excelData =  new HashMap<>();
        totalRows = -1;
    }

    private State LoadDataFromExcel()
    {
        try
        {
            workbook = new XSSFWorkbook (Constants.inputExcel);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            int currRow = sheet.getFirstRowNum() - 1;
            Boolean reachedHook = false;
            while(rowIterator.hasNext() && !reachedHook )
            {
                currRow++;
                Row row = rowIterator.next();

                Cell bCell = row.getCell(bCellNum);
                Cell qCell = row.getCell(qCellNum);
                Cell lastPrcPrCell = row.getCell(lastPrcPrCellNum);

                String bVal = GetBarcode(bCell);
                Double qVal = GetNumericValue(qCell);
                Double lastPrcPr   = GetNumericValue(lastPrcPrCell);

                reachedHook = hasReachTheEnd(bVal);

                if (bVal == null || qVal == null) continue;

                if(lastPrcPr == null)
                    lastPrcPr = 0.0;

                RowData v =  new RowData();
                v.quantity  = qVal;
                v.row       = currRow;
                v.lastPrcPr = lastPrcPr;
                excelData.put(bVal,v);
            }
            totalRows=currRow;
        }
        catch (IOException e)
        {
            System.out.println("Error: "+e);
            return State.FAILURE;
        }

        return  State.SUCCESS;
    }


    public HashMap<String, RowData> GetExcelData()
    {
        if(excelData.size() != 0)
        {
            return excelData;
        }

        if(LoadDataFromExcel() == State.FAILURE)
        {
            return null;
        }
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
