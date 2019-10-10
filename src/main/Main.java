package main;
import model.ConnectionManager;


import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;

public class Main
{



    public static void main(String[] args)
    {
        try
        {
            ConnectionManager conn=new ConnectionManager( "./data/credentials.txt");
            HashMap<String,Double> databaseData = conn.GetWarehouseData();
            HashMap<String,Double> excelData = new HashMap<String, Double>();

            FileInputStream file = new FileInputStream(new File("./excel/a.xlsx"));

            //Get the workbook instance for XLS file
            XSSFWorkbook workbook = new XSSFWorkbook (file);

            //Get first sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);

            //Get iterator to all the rows in current sheet
            Iterator<Row> rowIterator = sheet.iterator();
            System.out.println( );

            while(rowIterator.hasNext())
            {
                Row row = rowIterator.next();

                //For each row, iterate through each columns
                //Iterator<Cell> cellIterator = row.cellIterator();
                //Cell cell = cellIterator.next();
                Cell barcode = row.getCell(2);
                Cell quantity = row.getCell(4);

                // if a cell is blank then it's null
                if (barcode == null || quantity == null) {
                    continue;
                }

                CellType bt = barcode.getCellType();
                CellType qt = quantity.getCellType();
                String btVal = null;
                double qtVal = -1;
                if (bt == CellType.NUMERIC && qt == CellType.NUMERIC)
                {
                    btVal = String.format ("%.0f",barcode.getNumericCellValue());
                    qtVal = quantity.getNumericCellValue();
                }
                else if (bt == CellType.STRING)
                {
                    //System.out.println(barcode.getStringCellValue() );
                    btVal = barcode.getStringCellValue();
                    //System.out.println("Error: Cell type"+bt.name() +" is not valid.--"+cnt);
                }

                if (    btVal ==  "ΒΟΗΘΗΤΙΚΟΣ ΚΩΔΙΚΟΣ"  ||
                        btVal == "ΣΥΝΟΛΟ" ||
                        qtVal ==-1)
                {
                    continue;
                }

                excelData.put(btVal,qtVal);

                if(databaseData.get(btVal) != null)
                {
                    System.out.println("Found");
                }

            }

            for (String i : excelData.keySet())
            {
                //System.out.printf( "%s, %.0f \n",i, excelData.get(i));
                System.out.println( i +","+excelData.get(i));
            }

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
