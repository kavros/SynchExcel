package model;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;


public class ExcelGenerator
{
    private final int bCellNum = 4;     // barcode
    private final int qCellNum = 6;     // quantity
    private final int stCellNum = 1;    // update status
    private final int descCellNum= 5;   // product description
    private final int lastPrcPrCellNum = 2;

    DatabaseService databaseService;
    ExcelParser exlParser;

    public ExcelGenerator( DatabaseService _databaseService,
                          ExcelParser _exlParser)
    {
        databaseService = _databaseService;
        exlParser= _exlParser;
    }

    public State GenerateExcel() throws  Exception
    {
        HashMap<String, ExcelParser.RowData> excelData = exlParser.GetExcelData();
        if( excelData == null)
        {
            return State.FAILURE;
        }

        HashMap<String, DatabaseService.HashValue> dbData = databaseService.GetDataFromWarehouse();

        XSSFSheet sheet = exlParser.GetWorkbook().getSheetAt(0);
        int totalRows = exlParser.GetTotalRows();

        for(String bDbVal:dbData.keySet())
        {
            Double qValDb = dbData.get(bDbVal).quantity;
            ExcelParser.RowData exlEntry = excelData.get(bDbVal);
            Double lastPrcPrDb = dbData.get(bDbVal).lastPrcPr;
            boolean isBarcodeInExcel = (excelData.get(bDbVal) != null);

            if (isBarcodeInExcel)
            {
                boolean isQuantityChanged = Double.compare(qValDb,exlEntry.quantity) != 0 ;

                if ( isQuantityChanged )
                {
                    UpdateQuantity(sheet,exlEntry,qValDb);
                }

                Double lastPrcPrExl = excelData.get(bDbVal).lastPrcPr;
                boolean isLastPrcPrChanged = Double.compare(lastPrcPrExl,lastPrcPrDb) != 0;

                if(isLastPrcPrChanged)
                {
                    UpdateLastPrcPr(sheet,exlEntry,lastPrcPrDb);
                }
            }
            else
            {
                if(qValDb == 0) continue;
                String pName = dbData.get(bDbVal).productName;
                InsertRowLast(sheet, totalRows, bDbVal,qValDb,dbData,pName);
                totalRows++;
            }
        }

        FileOutputStream output_file =new FileOutputStream(new File(Constants.outExcel));
        exlParser.GetWorkbook().write(output_file);

        return State.SUCCESS;
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


    private void UpdateQuantity(XSSFSheet sheet, ExcelParser.RowData exlEntry, Double qValDb)
    {
        Cell c = getCell(sheet.getRow(exlEntry.row), qCellNum);
        c.setCellValue(qValDb);

        UpdatedStatusCol(sheet,exlEntry,"quantity");
        System.out.println("Updated quantity from " + exlEntry.quantity + " to " + qValDb+ " at line "+ (exlEntry.row+1) );
    }

    private void InsertRowLast(XSSFSheet sheet, int lastRow, String bDbVal, Double qValDb,
                               HashMap<String, DatabaseService.HashValue>  dbData, String pName)
    {

        sheet.createRow(lastRow+1).createCell(bCellNum).setCellValue(bDbVal);
        sheet.getRow(lastRow+1).createCell(qCellNum).setCellValue(dbData.get(bDbVal).quantity);
        sheet.getRow(lastRow+1).createCell(descCellNum).setCellValue(pName);
        sheet.getRow(lastRow+1).createCell(lastPrcPrCellNum).setCellValue(dbData.get(bDbVal).lastPrcPr);
        System.out.println("Added entry"+"("+ bDbVal+ "," +qValDb+")"+"at row "+lastRow);
    }

    private void UpdateLastPrcPr(XSSFSheet sheet, ExcelParser.RowData exlEntry, double lastPrcVal)
    {
        Cell c = getCell(sheet.getRow(exlEntry.row), lastPrcPrCellNum);
        c.setCellValue(lastPrcVal);

        System.out.println("Updated purchase price from " + exlEntry.lastPrcPr + " to " + lastPrcVal + " at line " + (exlEntry.row+1) );
    }

    private void UpdatedStatusCol(XSSFSheet sheet, ExcelParser.RowData exlEntry, String message)
    {
        Cell c2 = getCell(sheet.getRow(exlEntry.row), stCellNum);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy");
        LocalDate localDate = LocalDate.now();
        String currStatusValue =c2.getStringCellValue();
        String out="";
        if(currStatusValue != null)
        {
            out = currStatusValue+" ";
        }
        c2.setCellValue(out + "Updated "+ message + " on "+dtf.format(localDate));
    }
}
