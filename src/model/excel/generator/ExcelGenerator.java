package model.excel.generator;

import model.database.reader.DatabaseData;
import model.database.reader.DatabaseReader;
import model.excel.constants.ExcelColumns;
import model.excel.parser.ExcelData;
import model.excel.parser.ExcelParser;
import model.excel.parser.ExcelRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class ExcelGenerator
{
    public static final String OUTPUT_FILE_NAME = "./excel/b.xlsx";
    private final DatabaseReader databaseReader;
    private final ExcelParser exlParser;
    private final XSSFWorkbook workbook;
    private static final Logger logger = LoggerFactory.getLogger(ExcelGenerator.class);

    public ExcelGenerator( DatabaseReader databaseReader,
                          ExcelParser exlParser, XSSFWorkbook workbook)
    {
        this.databaseReader = databaseReader;
        this.exlParser= exlParser;
        this.workbook = workbook;
    }

    public void GenerateExcel()
    {
        ExcelData excelData = exlParser.GetExcelData();
        DatabaseData dbData = databaseReader.GetDataFromWarehouse();
        int totalRows = exlParser.GetTotalRows();

        if( dbData == null ||dbData.Size() == 0 )
            throw new IllegalArgumentException("Data retrieved from database equals to 0");

        for(String bDbVal:dbData.GetBarcodes())
        {
            Double qValDb = dbData.Get(bDbVal).quantity_2;
            boolean isBarcodeInExcel = (excelData.Get(bDbVal) != null);

            if (isBarcodeInExcel)
            {
                UpdateCells(bDbVal);
            }
            else if(qValDb !=0) // if barcode is not inside excel and quantity is not 0 then add new excel entry
            {
                InsertCells(totalRows, bDbVal);
                totalRows++;
            }
        }
    }

    public void SaveExcel() throws IOException
    {
        if(exlParser.GetExcelData() == null)
            logger.error("SaveExcel failed because parser returns null");

        FileOutputStream output_file = new FileOutputStream(new File(OUTPUT_FILE_NAME));
        workbook.write(output_file);
    }

    private void UpdateCells(String bDbVal )
    {

        ExcelRow exlEntry = exlParser.GetExcelData().Get(bDbVal);
        Double qValDb = databaseReader.GetDataFromWarehouse().Get(bDbVal).quantity_2;
        boolean isQuantityChanged = ( Double.compare(qValDb,exlEntry.quantity_2) != 0 );
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

        UpdateQuantity_1(bDbVal);
    }

    private void UpdateQuantity_1(String barcode)
    {
        XSSFSheet sheet = workbook.getSheetAt(0);
        Double q1ValDb = databaseReader.GetDataFromWarehouse().Get(barcode).quantity_1;
        ExcelRow exlEntry = exlParser.GetExcelData().Get(barcode);

        Cell c = GetCell(sheet.getRow(exlEntry.row), ExcelColumns.QUANTITY_1);
        c.setCellValue(q1ValDb);
    }

    private void UpdateQuantity(String barcode)
    {
        XSSFSheet sheet = workbook.getSheetAt(0);
        Double qValDb = databaseReader.GetDataFromWarehouse().Get(barcode).quantity_2;
        String productName = databaseReader.GetDataFromWarehouse().Get(barcode).productName;
        ExcelRow exlEntry = exlParser.GetExcelData().Get(barcode);

        Cell c = GetCell(sheet.getRow(exlEntry.row), ExcelColumns.QUANTITY_2);
        c.setCellValue(qValDb);

        UpdatedStatusCol(barcode);

        String updateInfo = "Updated entry ("+barcode+","+productName+") quantity_2 from "
            + exlEntry.quantity_2 + " to " + qValDb+ " at line "
            + (exlEntry.row+1);
        logger.info (updateInfo);
    }

    private Cell GetCell(Row r, int index)
    {
        if(r == null)
        {
            logger.error("Error: row cannot be null");
            System.exit(-1);
        }
        Cell c = r.getCell(index);
        if(c == null)
        {
            c = r.createCell(index);
        }
        return  c;
    }

    private void InsertCells(int lastRow, String bDbVal)
    {
        DatabaseData dbData = databaseReader.GetDataFromWarehouse();
        Double qValDb = dbData.Get(bDbVal).quantity_2;
        String productName =  dbData.Get(bDbVal).productName;
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow endOfSheet = sheet.createRow(lastRow+1);

        endOfSheet
            .createCell(ExcelColumns.BARCODE)
            .setCellValue(bDbVal);

        endOfSheet
                .createCell(ExcelColumns.QUANTITY_2)
                .setCellValue(dbData.Get(bDbVal).quantity_2);

        endOfSheet
                .createCell(ExcelColumns.PRODUCT_DESCRIPTION)
                .setCellValue(productName);

        endOfSheet
                .createCell(ExcelColumns.LAST_PRICE)
                .setCellValue(dbData.Get(bDbVal).lastPrcPr);

        endOfSheet
                .createCell(ExcelColumns.PRODUCT_CODE)
                .setCellValue(dbData.Get(bDbVal).productCode);

        endOfSheet
                .createCell(ExcelColumns.QUANTITY_1)
                .setCellValue(dbData.Get(bDbVal).quantity_1);

        String addInfo ="Added entry ("+ bDbVal+ ","+productName+","+qValDb+")at line "+(lastRow+1);
        logger.info(addInfo);
    }

    private void UpdateLastPrcPr(String barcode)
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
        String updateInfo ="Updated entry ("+barcode+","+productName+") purchase price from " + exlEntry.lastPrcPr + " to " + lastPrcPrDb + " at line " + (exlEntry.row+1);
        logger.info(updateInfo);
    }

    private void UpdatedStatusCol( String barcode)
    {
        XSSFSheet sheet = workbook.getSheetAt(0);
        ExcelRow exlEntry = exlParser.GetExcelData().Get(barcode);
        Cell c2 = GetCell(sheet.getRow(exlEntry.row), ExcelColumns.UPDATE_STATUS);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy");
        LocalDate localDate = LocalDate.now();

        c2.setCellValue("Updated quantity_2 on "+dtf.format(localDate));
    }


}