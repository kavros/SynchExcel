package main;
import model.ConnectionManager;


import org.apache.poi.hssf.util.HSSFColor;
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

    public static void main(String[] args)
    {
        try
        {
            ConnectionManager conn=new ConnectionManager( "./data/credentials.txt");
            HashMap<String,Double> dbData = conn.GetWarehouseData();
            //HashMap<String,Double> excelData = new HashMap<String, Double>();

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

            int cnt=0;
            while(rowIterator.hasNext())
            {
                cnt++;
                Row row = rowIterator.next();

                Cell bCell = row.getCell(3);
                Cell qCell = row.getCell(5);

                String bVal = getBarcode(bCell);
                Double qVal = getQuantity(qCell);
                if (bVal == null || qVal == null) continue;

                //excelData.put(bVal,qVal);

                if(dbData.get(bVal) != null )
                {
                    Double qValDb = dbData.get(bVal);

                    System.out.println("Database qt "+qValDb);
                    System.out.println("Excel qt "+ qVal);
                    if(qValDb != qVal)
                    {
                        System.out.println("Update "+bVal+" from "+qVal+" to "+qValDb);
                        Cell c = getCell(row,5);
                        c.setCellValue(qValDb);

                        Cell c2 = getCell(row,1);
                        c2.setCellValue("Updated");
                    }
                }

            }
            System.out.println(cnt);


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


     /*if (bt == CellType.STRING)
                {
                    //System.out.println(barcode.getStringCellValue() );
                }
                else if(bt == CellType.BLANK)
                {

                }
                else if(bt == CellType.ERROR)
                {

                }
                else if(bt == CellType.FORMULA)
                {

                }
                else if(bt == CellType._NONE)
                {

                }*/

     /*CellStyle style = workbook.createCellStyle();
                    Font font = workbook.createFont();
                    font.setColor(IndexedColors.RED.getIndex());
                    style.setFont(font);
                    sheet.getRow(rCnt).setRowStyle(style);*/