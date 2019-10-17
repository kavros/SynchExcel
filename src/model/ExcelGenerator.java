package model;

import main.Main;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;



public class ExcelGenerator {
    public class RowData
    {
        int row;
        Double quantity;
    }
    private HashMap<String,RowData> excelData ;
    private int lastRow;
    XSSFWorkbook workbook;

    public ExcelGenerator()
    {
        excelData = new HashMap<String, RowData>();
        lastRow = 0;
        Init();
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

    private Cell getCell(Row r,int index)
    {
        Cell c = r.getCell(index);
        if(c == null)
        {
            c = r.createCell(index);
        }
        return  c;
    }


    private void updateRow(XSSFSheet sheet, ExcelGenerator.RowData exlEntry, Double qValDb, String bDbVal)
    {
        Cell c = getCell(sheet.getRow(exlEntry.row), 5);
        c.setCellValue(qValDb);

        Cell c2 = getCell(sheet.getRow(exlEntry.row), 1);
        c2.setCellValue("Updated");
        System.out.println("Updated quantity from " + exlEntry.quantity + " to " + qValDb+ " at "+ bDbVal );
    }

    private void insertRowLast(XSSFSheet sheet,int lastRow,String bDbVal,Double qValDb,HashMap<String,Double>  dbData,String pName)
    {

        sheet.createRow(lastRow+1).createCell(3).setCellValue(bDbVal);
        sheet.getRow(lastRow+1).createCell(5).setCellValue(dbData.get(bDbVal));
        sheet.getRow(lastRow+1).createCell(4).setCellValue(pName);

        System.out.println("Added entry"+"("+ bDbVal+ "," +qValDb+")");
    }

    public void GenerateExcel() throws  Exception
    {
        DatabaseManager conn=new DatabaseManager( "./data/credentials.txt");
        HashMap<String,Double> dbData = conn.GetWarehouseData();

        XSSFSheet sheet =workbook.getSheetAt(0);

        for(String bDbVal:dbData.keySet())
        {
            Double qValDb = dbData.get(bDbVal);
            ExcelGenerator.RowData exlEntry = excelData.get(bDbVal);
            boolean isBarcodeInExcel = (excelData.get(bDbVal) != null);

            if (isBarcodeInExcel)
            {
                boolean isQuantityChanged = (qValDb != exlEntry.quantity);
                if ( isQuantityChanged )
                {
                    updateRow(sheet,exlEntry,qValDb,bDbVal);
                }
            }
            else
            {
                if(qValDb == 0) continue;
                String pName = conn.getProductName(bDbVal);
                insertRowLast(sheet, lastRow, bDbVal,qValDb,dbData,pName);
                lastRow++;
            }
        }


        FileOutputStream output_file =new FileOutputStream(new File("./excel/b.xlsx"));
        workbook.write(output_file);
    }


}
