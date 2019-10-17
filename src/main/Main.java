package main;
import model.DatabaseManager;


import model.ExcelParser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;

public class Main
{

    static private Cell getCell(Row r,int index)
    {
        Cell c = r.getCell(index);
        if(c == null)
        {
            c = r.createCell(index);
        }
        return  c;
    }


    static private void updateRow(XSSFSheet sheet, ExcelParser.RowData exlEntry, Double qValDb, String bDbVal)
    {
        Cell c = getCell(sheet.getRow(exlEntry.row), 5);
        c.setCellValue(qValDb);

        Cell c2 = getCell(sheet.getRow(exlEntry.row), 1);
        c2.setCellValue("Updated");
        System.out.println("Updated quantity from " + exlEntry.quantity + " to " + qValDb+ " at "+ bDbVal );
    }

    static private void insertRowLast(XSSFSheet sheet,int lastRow,String bDbVal,Double qValDb,HashMap<String,Double>  dbData,String pName)
    {

        sheet.createRow(lastRow+1).createCell(3).setCellValue(bDbVal);
        sheet.getRow(lastRow+1).createCell(5).setCellValue(dbData.get(bDbVal));
        sheet.getRow(lastRow+1).createCell(4).setCellValue(pName);

        System.out.println("Added entry"+"("+ bDbVal+ "," +qValDb+")");
    }



    public static void main(String[] args)
    {
        try
        {
            DatabaseManager conn=new DatabaseManager( "./data/credentials.txt");
            HashMap<String,Double> dbData = conn.GetWarehouseData();

            ExcelParser p = new ExcelParser();
            HashMap<String,ExcelParser.RowData> excelData = p.GetExcelData();

            XSSFWorkbook workbook = p.GetWorkBook();
            XSSFSheet sheet =workbook.getSheetAt(0);

            int lastRow = p.GetLastRow();

            for(String bDbVal:dbData.keySet())
            {
                Double qValDb = dbData.get(bDbVal);
                ExcelParser.RowData exlEntry = excelData.get(bDbVal);
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
        catch (Exception e)
        {
            System.out.println("Error: "+e);
        }









    }
}
