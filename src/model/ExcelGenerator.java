package model;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private final int productCodeCellNum = 3;
    private DatabaseService databaseService;
    private ExcelParser exlParser;
    XSSFWorkbook workbook;

    public ExcelGenerator( DatabaseService _databaseService,
                          ExcelParser _exlParser)
    {
        databaseService = _databaseService;
        exlParser= _exlParser;
        workbook = _exlParser.GetWorkbook();
    }

    public State GenerateExcel() throws Exception
    {
        HashMap<String, ExcelProductDetails> excelData = exlParser.GetExcelData();
        HashMap<String, DatabaseProductDetails> dbData = databaseService.GetDataFromWarehouse();

        if( excelData == null || dbData.size() == 0)
        {
            System.out.println("Database service or parser failed to provide data to generator.");
            return State.FAILURE;
        }
        int totalRows = exlParser.GetTotalRows();

        for(String bDbVal:dbData.keySet())
        {
            Double qValDb = dbData.get(bDbVal).quantity;
            boolean isBarcodeInExcel = (excelData.get(bDbVal) != null);

            if (isBarcodeInExcel)
            {
                UpdateRow(bDbVal);
            }
            else if(qValDb !=0) // if barcode is not inside excel and quantity is not 0 then add new excel entry
            {
                InsertRowLast(totalRows, bDbVal);
                totalRows++;
            }
        }

        return State.SUCCESS;
    }

    public void SaveExcel() throws IOException
    {
        if(exlParser.GetExcelData() == null)
            System.err.println("SaveExcel failed because parser returns null");

        FileOutputStream output_file = new FileOutputStream(new File(Constants.outExcel));
        workbook.write(output_file);
    }

    private void UpdateRow(String bDbVal ) throws Exception
    {

        ExcelProductDetails exlEntry = exlParser.GetExcelData().get(bDbVal);
        Double qValDb = databaseService.GetDataFromWarehouse().get(bDbVal).quantity;
        boolean isQuantityChanged = ( Double.compare(qValDb,exlEntry.quantity) != 0 );
        if ( isQuantityChanged )
        {
            UpdateQuantity(bDbVal);
        }

        Double lastPrcPrDb  = databaseService.GetDataFromWarehouse().get(bDbVal).lastPrcPr;
        Double lastPrcPrExl = exlParser.GetExcelData().get(bDbVal).lastPrcPr;
        boolean isLastPrcPrChanged = (Double.compare(lastPrcPrExl,lastPrcPrDb) != 0);
        if( isLastPrcPrChanged )
        {
            UpdateLastPrcPr(bDbVal);
        }
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


    private void UpdateQuantity(String barcode) throws Exception
    {
        XSSFSheet sheet = workbook.getSheetAt(0);
        Double qValDb = databaseService.GetDataFromWarehouse().get(barcode).quantity;
        String productName = databaseService.GetDataFromWarehouse().get(barcode).productName;
        ExcelProductDetails exlEntry = exlParser.GetExcelData().get(barcode);

        Cell c = getCell(sheet.getRow(exlEntry.row), qCellNum);
        c.setCellValue(qValDb);

        UpdatedStatusCol(barcode);
        System.out.println
                (
                        "Updated entry ("+barcode+","+productName+") quantity from "
                        + exlEntry.quantity + " to " + qValDb+ " at line "
                        + (exlEntry.row+1)
                );
    }

    private void InsertRowLast(int lastRow, String bDbVal) throws Exception
    {
        HashMap<String, DatabaseProductDetails> dbData = databaseService.GetDataFromWarehouse();
        Double qValDb = dbData.get(bDbVal).quantity;
        String productName =  dbData.get(bDbVal).productName;

        XSSFSheet sheet = workbook.getSheetAt(0);

        sheet.createRow(lastRow+1).createCell(bCellNum).setCellValue(bDbVal);
        sheet.getRow(lastRow+1)
                .createCell(qCellNum)
                .setCellValue(dbData.get(bDbVal).quantity);

        sheet.getRow(lastRow+1)
                .createCell(descCellNum)
                .setCellValue(productName);

        sheet.getRow(lastRow+1)
                .createCell(lastPrcPrCellNum)
                .setCellValue(dbData.get(bDbVal).lastPrcPr);

        sheet.getRow(lastRow+1)
                .createCell(productCodeCellNum)
                .setCellValue(dbData.get(bDbVal).productCode);

        System.out.println("Added entry ("+ bDbVal+ ","+productName+","+qValDb+")at line "+(lastRow+1));
    }

    private void UpdateLastPrcPr(String barcode) throws Exception
    {
        XSSFSheet sheet = workbook.getSheetAt(0);
        ExcelProductDetails exlEntry = exlParser.GetExcelData().get(barcode);
        String productName =  databaseService
                .GetDataFromWarehouse()
                .get(barcode).productName;
        Double lastPrcPrDb = databaseService
                .GetDataFromWarehouse()
                .get(barcode)
                .lastPrcPr;

        Cell c = getCell(sheet.getRow(exlEntry.row), lastPrcPrCellNum);
        c.setCellValue(lastPrcPrDb);

        System.out.println("Updated entry ("+barcode+","+productName+") purchase price from " + exlEntry.lastPrcPr + " to " + lastPrcPrDb + " at line " + (exlEntry.row+1) );
    }

    private void UpdatedStatusCol( String barcode)
    {
        XSSFSheet sheet = workbook.getSheetAt(0);
        ExcelProductDetails exlEntry = exlParser.GetExcelData().get(barcode);
        Cell c2 = getCell(sheet.getRow(exlEntry.row), stCellNum);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy");
        LocalDate localDate = LocalDate.now();

        c2.setCellValue("Updated quantity on "+dtf.format(localDate));
    }
}
