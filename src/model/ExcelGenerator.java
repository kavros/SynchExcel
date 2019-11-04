package model;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import static  model.Constants.*;


public class ExcelGenerator {
    public class RowData
    {
        int row;
        Double quantity;
    }

    private final int bCellNum = 4;     // barcode
    private final int qCellNum = 6;     // quantity
    private final int stCellNum = 1;    // update status
    private final int descCellNum= 5;   // product description
    private final int lastPrcPrNum = 2;
    private HashMap<String,RowData> excelData ;
    private int totalRows;
    private XSSFWorkbook workbook;

    public ExcelGenerator()
    {
        excelData = new HashMap<String, RowData>();
        totalRows = -1;
    }

    private boolean hasReachTheEnd(String str)
    {
        return ( (str != null) && (str.contains("ΣΥΝΟΛΟ TΕΛΙΚΟ")) );
    }

    private state Init()
    {
        try
        {
            FileInputStream file = new FileInputStream(new File(Constants.inputExcel));
            workbook = new XSSFWorkbook (file);
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

                String bVal = GetBarcode(bCell);
                Double qVal = GetQuantity(qCell);

                reachedHook = hasReachTheEnd(bVal);

                if (bVal == null || qVal == null) continue;

                RowData v =  new RowData();
                v.quantity=qVal;
                v.row= currRow;
                excelData.put(bVal,v);
            }
            totalRows=currRow;
            file.close();
        }
        catch (Exception e)
        {
            System.out.println("Error: "+e);
            return state.FAILURE;
        }

        return  state.SUCCESS;
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
        if(r == null)
        {
            System.err.println("Error: row cannot be null");
            System.exit(-1);
        }
        Cell c = r.getCell(index);
        if(c == null)
        {
            c = r.createCell(index);
        }
        return  c;
    }


    private void updateRow(XSSFSheet sheet, ExcelGenerator.RowData exlEntry, Double qValDb, String bDbVal, double lastPrcVal)
    {
        Cell c = getCell(sheet.getRow(exlEntry.row), qCellNum);
        c.setCellValue(qValDb);

        Cell c2 = getCell(sheet.getRow(exlEntry.row), stCellNum);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy");
        LocalDate localDate = LocalDate.now();
        c2.setCellValue("Updated on "+dtf.format(localDate));

        Cell c3 = getCell(sheet.getRow(exlEntry.row), lastPrcPrNum);
        c3.setCellValue(lastPrcVal);

        System.out.println("Updated quantity from " + exlEntry.quantity + " to " + qValDb+ " at "+ bDbVal );
    }

    private void insertRowLast(XSSFSheet sheet, int lastRow, String bDbVal, Double qValDb,
                               HashMap<String, DatabaseManager.HashValue>  dbData, String pName)
    {

        sheet.createRow(lastRow+1).createCell(bCellNum).setCellValue(bDbVal);
        sheet.getRow(lastRow+1).createCell(qCellNum).setCellValue(dbData.get(bDbVal).quantity);
        sheet.getRow(lastRow+1).createCell(descCellNum).setCellValue(pName);
        sheet.getRow(lastRow+1).createCell(lastPrcPrNum).setCellValue(dbData.get(bDbVal).lastPrcPr);
        System.out.println("Added entry"+"("+ bDbVal+ "," +qValDb+")"+"at row "+lastRow);
    }

    public state GenerateExcel() throws  Exception
    {
        if( Init() == state.FAILURE)
            return state.FAILURE;

        DatabaseManager conn=new DatabaseManager( credFilePath);
        HashMap<String, DatabaseManager.HashValue> dbData = conn.GetDataFromWarehouse();

        XSSFSheet sheet =workbook.getSheetAt(0);

        for(String bDbVal:dbData.keySet())
        {
            Double qValDb = dbData.get(bDbVal).quantity;
            ExcelGenerator.RowData exlEntry = excelData.get(bDbVal);
            Double lastPrcPr = dbData.get(bDbVal).lastPrcPr;
            boolean isBarcodeInExcel = (excelData.get(bDbVal) != null);

            if (isBarcodeInExcel)
            {
                boolean isQuantityChanged = Double.compare(qValDb,exlEntry.quantity) != 0 ;
                if ( isQuantityChanged )
                {
                    updateRow(sheet,exlEntry,qValDb,bDbVal,lastPrcPr);
                }
            }
            else
            {
                if(qValDb == 0) continue;
                String pName = conn.getProductName(bDbVal);
                insertRowLast(sheet, totalRows, bDbVal,qValDb,dbData,pName);
                totalRows++;
            }
        }


        FileOutputStream output_file =new FileOutputStream(new File(Constants.outExcel));
        workbook.write(output_file);

        return state.SUCCESS;
    }


}
