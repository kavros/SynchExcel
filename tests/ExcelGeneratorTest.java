import model.database.reader.DatabaseData;
import model.database.reader.DatabaseRow;
import model.database.reader.DatabaseReader;
import model.excel.constants.ExcelColumns;
import model.excel.generator.ExcelGenerator;
import model.excel.parser.ExcelData;
import model.excel.parser.ExcelParser;
import model.excel.parser.ExcelRow;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ExcelParser.class, DatabaseReader.class})
public class ExcelGeneratorTest
{

    private ExcelParser parser;
    private DatabaseReader dbService;
    private DatabaseData dbData;
    private ExcelData excelData;
        private XSSFWorkbook workbook ;

    @Before
    public void Setup()
    {
        parser = mock(ExcelParser.class);
        dbService = mock(DatabaseReader.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void GenerateExcel_WhenExcelDataIsNull_ThrowsNullPointerException() throws Exception
    {
        parser = mock(ExcelParser.class);
        dbService = mock(DatabaseReader.class);
        ExcelGenerator generator = new ExcelGenerator(dbService,parser,null);
        when(parser.GetExcelData()).thenReturn(null);

        generator.GenerateExcel();
    }

    @Test(expected = IllegalArgumentException.class)
    public void GenerateExcel_WhenDatabaseMapIsEmpty_ThrowsNullPointerException() throws Exception
    {
        parser = mock(ExcelParser.class);
        dbService = mock(DatabaseReader.class);
        ExcelGenerator generator = new ExcelGenerator(dbService,parser,null);
        when(dbService.GetDataFromWarehouse()).thenReturn(new DatabaseData());

        generator.GenerateExcel();
    }

    @Test
    public void GenerateExcel_WhenExcelIsUpToDate_WorkbookRemainsSame() throws Exception
    {

        dbData = new DatabaseData();
        excelData = new  ExcelData();
        workbook = new XSSFWorkbook();
        DatabaseRow dbDetails = new DatabaseRow(
                1.0,1.0,"bread",
                "43.13",20.0, 0.13f);
        ExcelRow excelDetails = new ExcelRow(0,1.0,1.0);
        dbData.Add("43.13",dbDetails);
        excelData.Add(excelDetails,"43.13");
        CreateExcelSheet();
        when(dbService.GetDataFromWarehouse()).thenReturn(dbData);
        when(parser.GetExcelData()).thenReturn(excelData);
        when(parser.GetTotalRows()).thenReturn(1);
        ExcelGenerator generator = new ExcelGenerator(dbService,parser,workbook);

        generator.GenerateExcel();

        String productName = "bread";
        double quantity = 1.0;
        String barcode = "43.13";
        double price = 1.0;
        Row row = workbook.getSheetAt(0).getRow(0);
        assertTrue(row.getCell(ExcelColumns.BARCODE).getStringCellValue().equals(barcode) );
        assertTrue(row.getCell(ExcelColumns.STORAGE_QUANTITY).getNumericCellValue() == quantity );
        assertTrue(row.getCell(ExcelColumns.PRODUCT_DESCRIPTION).getStringCellValue().equals(productName));
        assertTrue(row.getCell(ExcelColumns.LAST_PRICE).getNumericCellValue() == price);
        assertTrue(row.getCell(ExcelColumns.STORE_QUANTITY).getNumericCellValue() == 20.0);
    }

    @Test
    public void GenerateExcel_WhenProductsQuantityChange_UpdateWorkbookRow() throws Exception
    {

        dbData = new DatabaseData();
        excelData = new  ExcelData();
        workbook  = new XSSFWorkbook();
        DatabaseRow dbDetails = new DatabaseRow(10,1.1,
                "bread","43.13",10.0,0.13f);
        ExcelRow excelDetails = new ExcelRow(0,1.0,0.0);
        dbData.Add("43.13",dbDetails);
        excelData.Add(excelDetails,"43.13");
        String productName = "bread";
        String barcode = "43.13";
        CreateExcelSheet();
        Row row = workbook.getSheetAt(0).getRow(0);
        when(dbService.GetDataFromWarehouse()).thenReturn(dbData);
        when(parser.GetExcelData()).thenReturn(excelData);
        when(parser.GetTotalRows()).thenReturn(1);
        ExcelGenerator generator = new ExcelGenerator(dbService,parser,workbook);

        generator.GenerateExcel();

        assertTrue(row.getCell(ExcelColumns.BARCODE).getStringCellValue().equals(barcode));
        assertTrue(row.getCell(ExcelColumns.STORAGE_QUANTITY).getNumericCellValue() == 10.0 );
        assertEquals(row.getCell(ExcelColumns.PRODUCT_DESCRIPTION).getStringCellValue(), productName);
        assertTrue(row.getCell(ExcelColumns.LAST_PRICE).getNumericCellValue() == 1.1);
        assertTrue(row.getCell(ExcelColumns.STORE_QUANTITY).getNumericCellValue() == 10.0);
        assertEquals("0.13", row.getCell(ExcelColumns.PRODUCT_VAT_CODE).getStringCellValue());
    }

    @Test
    public void GenerateExcel_WhenAddedNewProduct_InsertRowInToWorkbook() throws Exception
    {

        dbData    = new DatabaseData();
        excelData = new ExcelData();
        workbook  = new XSSFWorkbook();
        DatabaseRow productDetails = new DatabaseRow(
                1.0,1.0,"bread",
                "43.13",0.0, 13f);
        DatabaseRow product2Details = new DatabaseRow(
                1.0,1.0,"paper",
                "43.13",0.0,13f);
        ExcelRow excelDetails = new ExcelRow(0, 1.0, 1.0);
        dbData.Add("43.13",productDetails);
        dbData.Add("43.14",product2Details);
        excelData.Add(excelDetails,"43.13");
        CreateExcelSheet();
        when(dbService.GetDataFromWarehouse()).thenReturn(dbData);
        when(parser.GetExcelData()).thenReturn(excelData);
        when(parser.GetTotalRows()).thenReturn(1);
        ExcelGenerator generator = new ExcelGenerator(dbService,parser,workbook);

        generator.GenerateExcel();

        assertTrue(workbook.getSheetAt(0).getPhysicalNumberOfRows() == 2 );
    }

    @Test
    public void GenerateExcel_WhenAddedNewProductWithZeroQuantity_InsertIgnored() throws Exception
    {

        dbData    = new DatabaseData();
        excelData = new  ExcelData();
        workbook  = new XSSFWorkbook();
        DatabaseRow productDetails = new DatabaseRow(
                1.0,1.0, "bread",
                "43.13",0.0,13f);
        DatabaseRow product2Details = new DatabaseRow(
                0.0,1.0, "paper",
                "43.13",0.0,13f);
        ExcelRow excelDetails = new ExcelRow(0,1.0,1.0);
        dbData.Add("43.13",productDetails);
        dbData.Add("43.14",product2Details);
        excelData.Add(excelDetails,"43.13");
        CreateExcelSheet();
        when(dbService.GetDataFromWarehouse()).thenReturn(dbData);
        when(parser.GetExcelData()).thenReturn(excelData);
        when(parser.GetTotalRows()).thenReturn(1);
        ExcelGenerator generator = new ExcelGenerator(dbService,parser,workbook);

        generator.GenerateExcel();

        assertTrue(workbook.getSheetAt(0).getPhysicalNumberOfRows() == 1 );
    }

    private void CreateExcelSheet()
    {
        String productName = "bread";
        double quantity = 1.0;
        String barcode = "43.13";
        double price = 1.0;

        Sheet sheet = workbook.createSheet("test");
        Row row = sheet.createRow(0);
        row.createCell(ExcelColumns.BARCODE).setCellValue(barcode);
        row.createCell(ExcelColumns.STORAGE_QUANTITY).setCellValue(quantity);
        row.createCell(ExcelColumns.PRODUCT_DESCRIPTION).setCellValue(productName);
        row.createCell(ExcelColumns.LAST_PRICE).setCellValue(price);
    }

}
