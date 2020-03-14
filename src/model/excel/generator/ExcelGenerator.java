package model.excel.generator;

import model.database.DatabaseData;
import model.database.DatabaseReader;
import model.excel.constants.ExcelColumns;
import model.excel.parser.ExcelData;
import model.excel.parser.ExcelParser;
import model.excel.parser.ExcelRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class ExcelGenerator
{
    public static final String outExcel     = "./excel/b.xlsx";
    private final DatabaseReader databaseReader;
    private final ExcelParser exlParser;
    private final XSSFWorkbook workbook;

    public ExcelGenerator( DatabaseReader _databaseReader,
                          ExcelParser _exlParser)
    {
        databaseReader = _databaseReader;
        exlParser= _exlParser;
        workbook = _exlParser.GetWorkbook();
    }

    public void GenerateExcel() throws Exception
    {
        ExcelData excelData = exlParser.GetExcelData();
        DatabaseData dbData = databaseReader.GetDataFromWarehouse();
        int totalRows = exlParser.GetTotalRows();

        if( dbData == null ||dbData.Size() == 0 )
            throw new IllegalArgumentException("Data retrieved from database equals to 0");

        for(String bDbVal:dbData.GetBarcodes())
        {
            Double qValDb = dbData.Get(bDbVal).quantity;
            boolean isBarcodeInExcel = (excelData.Get(bDbVal) != null);

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
    }

    public void SaveExcel() throws IOException
    {
        if(exlParser.GetExcelData() == null)
            System.err.println("SaveExcel failed because parser returns null");

        FileOutputStream output_file = new FileOutputStream(new File(outExcel));
        workbook.write(output_file);
    }

    private void UpdateRow(String bDbVal ) throws Exception
    {

        ExcelRow exlEntry = exlParser.GetExcelData().Get(bDbVal);
        Double qValDb = databaseReader.GetDataFromWarehouse().Get(bDbVal).quantity;
        boolean isQuantityChanged = ( Double.compare(qValDb,exlEntry.quantity) != 0 );
        if ( isQuantityChanged )
        {
            UpdateQuantity(bDbVal);
        }

        Double lastPrcPrDb  = databaseReader.GetDataFromWarehouse().Get(bDbVal).lastPrcPr;
        Double lastPrcPrExl = exlParser.GetExcelData().Get(bDbVal).lastPrcPr;
        boolean isLastPrcPrChanged = (Double.compare(lastPrcPrExl,lastPrcPrDb) != 0);
        if( isLastPrcPrChanged )
        {
            UpdateLastPrcPr(bDbVal);
        }
    }

    private Cell GetCell(Row r, int index)
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
        Double qValDb = databaseReader.GetDataFromWarehouse().Get(barcode).quantity;
        String productName = databaseReader.GetDataFromWarehouse().Get(barcode).productName;
        ExcelRow exlEntry = exlParser.GetExcelData().Get(barcode);

        Cell c = GetCell(sheet.getRow(exlEntry.row), ExcelColumns.QUANTITY);
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
        DatabaseData dbData = databaseReader.GetDataFromWarehouse();
        Double qValDb = dbData.Get(bDbVal).quantity;
        String productName =  dbData.Get(bDbVal).productName;

        XSSFSheet sheet = workbook.getSheetAt(0);

        sheet.createRow(lastRow+1).createCell(ExcelColumns.BARCODE).setCellValue(bDbVal);
        sheet.getRow(lastRow+1)
                .createCell(ExcelColumns.QUANTITY)
                .setCellValue(dbData.Get(bDbVal).quantity);

        sheet.getRow(lastRow+1)
                .createCell(ExcelColumns.PRODUCT_DESCRIPTION)
                .setCellValue(productName);

        sheet.getRow(lastRow+1)
                .createCell(ExcelColumns.LAST_PRICE)
                .setCellValue(dbData.Get(bDbVal).lastPrcPr);

        sheet.getRow(lastRow+1)
                .createCell(ExcelColumns.PRODUCT_CODE)
                .setCellValue(dbData.Get(bDbVal).productCode);

        System.out.println("Added entry ("+ bDbVal+ ","+productName+","+qValDb+")at line "+(lastRow+1));
    }

    private void UpdateLastPrcPr(String barcode) throws Exception
    {
        XSSFSheet sheet = workbook.getSheetAt(0);
        ExcelRow exlEntry = exlParser.GetExcelData().Get(barcode);
        String productName =  databaseReader
                .GetDataFromWarehouse()
                .Get(barcode).productName;
        Double lastPrcPrDb = databaseReader
                .GetDataFromWarehouse()
                .Get(barcode)
                .lastPrcPr;

        Cell c = GetCell(sheet.getRow(exlEntry.row), ExcelColumns.LAST_PRICE);
        c.setCellValue(lastPrcPrDb);

        System.out.println("Updated entry ("+barcode+","+productName+") purchase price from " + exlEntry.lastPrcPr + " to " + lastPrcPrDb + " at line " + (exlEntry.row+1) );
    }

    private void UpdatedStatusCol( String barcode)
    {
        XSSFSheet sheet = workbook.getSheetAt(0);
        ExcelRow exlEntry = exlParser.GetExcelData().Get(barcode);
        Cell c2 = GetCell(sheet.getRow(exlEntry.row), ExcelColumns.UPDATE_STATUS);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy");
        LocalDate localDate = LocalDate.now();

        c2.setCellValue("Updated quantity on "+dtf.format(localDate));
    }


}