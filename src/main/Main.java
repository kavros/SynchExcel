package main;
import model.DatabaseManager;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;

public class Main
{
    static private Row getRow(XSSFSheet sheet,int index)
    {
        Row r = sheet.getRow(index);
        if (r == null)
        {
            r = sheet.createRow(index);
        }
        return r;
    }


    static private Cell getCell(Row r,int index)
    {
        Cell c = r.getCell(index);
        if(c == null)
        {
            c = r.createCell(index);
        }
        return  c;
    }

    static private String getBarcode(Cell c)
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

    static private Double getQuantity(Cell c)
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
    static class Value
    {
        int row;
        Double quantity;
    }

    public static void main(String[] args)
    {
        try
        {
            DatabaseManager conn=new DatabaseManager( "./data/credentials.txt");
            HashMap<String,Double> dbData = conn.GetWarehouseData();
            HashMap<String,Value> excelData = new HashMap<String, Value>();

            FileInputStream file = new FileInputStream(new File("./excel/a.xlsx"));

            //Get the workbook instance for XLS file
            XSSFWorkbook workbook = new XSSFWorkbook (file);

            //Get first sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);

            //Get iterator to all the rows in current sheet
            Iterator<Row> rowIterator = sheet.iterator();
            /*Row r = getRow(sheet,0);
            Cell ce = getCell(r,0);
            ce.setCellValue("LALA");*/

            int rCnt=0;
            while(rowIterator.hasNext())
            {
                rCnt++;
                Row row = rowIterator.next();

                Cell bCell = row.getCell(3);
                Cell qCell = row.getCell(5);

                String bVal = getBarcode(bCell);
                Double qVal = getQuantity(qCell);
                if (bVal == null || qVal == null) continue;

                Value v =  new Value();
                v.quantity=qVal;
                v.row=rCnt;
                excelData.put(bVal,v);
            }
            int lastRow=rCnt;

            for(String bDbVal:dbData.keySet())
            {
                Double qValDb = dbData.get(bDbVal);
                Value exlEntry = excelData.get(bDbVal);

                if (excelData.get(bDbVal) != null)
                {

                    if (qValDb != exlEntry.quantity)
                    {

                        Cell c = getCell(sheet.getRow(exlEntry.row), 5);
                        c.setCellValue(qValDb);

                        Cell c2 = getCell(sheet.getRow(exlEntry.row), 1);
                        c2.setCellValue("Updated");
                        System.out.println("Updated quantity from " + exlEntry.quantity + " to " + qValDb+ " at "+ bDbVal );
                    }

                }
                else
                {
                    if(qValDb == 0) continue;

                    sheet.createRow(lastRow+1).createCell(3).setCellValue(bDbVal);
                    sheet.getRow(lastRow+1).createCell(5).setCellValue(dbData.get(bDbVal));
                    lastRow++;
                    System.out.println("Added entry"+"("+ bDbVal+ "," +qValDb+")");
                }
            }

            file.close();

            FileOutputStream output_file =new FileOutputStream(new File("./excel/b.xlsx"));
            //write changes
            workbook.write(output_file);

        }
        catch (Exception e)
        {
            System.out.println("Error: "+e);
        }









    }
}
