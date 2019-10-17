package model;

import main.Main;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;



public class ExcelParser {
    public class RowData
    {
        public int row;
        public Double quantity;
    }
    private HashMap<String,RowData> excelData ;
    private int lastRow;
    XSSFWorkbook workbook;

    public ExcelParser()
    {
        excelData = new HashMap<String, RowData>();
        lastRow = 0;
        Init();
    }

    public HashMap<String,RowData> GetExcelData()
    {
        return excelData;
    }

    public int GetLastRow()
    {
        return lastRow;
    }

    public XSSFWorkbook GetWorkBook()
    {
        return  workbook;
    }

    private void Init()
    {
        try
        {
            FileInputStream file = new FileInputStream(new File("./excel/a.xlsx"));

            //Get the workbook instance for XLS file
            workbook = new XSSFWorkbook (file);

            //Get first sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);

            //Get iterator to all the rows in current sheet
            Iterator<Row> rowIterator = sheet.iterator();
            int rCnt = 0;
            while(rowIterator.hasNext())
            {
                rCnt++;
                Row row = rowIterator.next();

                Cell bCell = row.getCell(3);
                Cell qCell = row.getCell(5);

                String bVal = GetBarcode(bCell);
                Double qVal = GetQuantity(qCell);
                if (bVal == null || qVal == null) continue;

                RowData v =  new RowData();
                v.quantity=qVal;
                v.row=rCnt;
                excelData.put(bVal,v);
            }
            lastRow=rCnt;
            file.close();
        }
        catch (Exception e)
        {
            System.out.println("Error: "+e);
        }

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

    private Double GetQuantity(Cell c)
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
