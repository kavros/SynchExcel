import model.*;
import model.dbReader.DatabaseData;
import model.dbReader.DatabaseRow;
import model.dbReader.DatabaseService;
import model.generator.ExcelGenerator;
import model.parser.ExcelData;
import model.parser.ExcelParser;
import model.parser.ExcelRow;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import java.util.HashMap;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ExcelParser.class, DatabaseService.class})
public class ExcelGeneratorTest
{

    private ExcelParser parser;
    private DatabaseService dbService;
    private DatabaseData dbData;
    private ExcelData excelData;
        private XSSFWorkbook workbook ;

    @Before
    public void Setup()
    {
        parser = mock(ExcelParser.class);
        dbService = mock(DatabaseService.class);
    }

    @Test
    public void GenerateExcel_WhenExcelDataIsNull_ReturnsFail() throws Exception
    {
        parser = mock(ExcelParser.class);
        dbService = mock(DatabaseService.class);
        ExcelGenerator generator = new ExcelGenerator(dbService,parser);
        when(parser.GetExcelData()).thenReturn(null);

        State result = generator.GenerateExcel();

        assertTrue(result == State.FAILURE);
    }

    @Test
    public void GenerateExcel_WhenDatabaseMapIsEmpty_ReturnsFail() throws Exception
    {
        parser = mock(ExcelParser.class);
        dbService = mock(DatabaseService.class);
        ExcelGenerator generator = new ExcelGenerator(dbService,parser);
        when(dbService.GetDataFromWarehouse()).thenReturn(new DatabaseData());

        State result = generator.GenerateExcel();

        assertTrue(result == State.FAILURE);
    }

    @Test
    public void GenerateExcel_WhenExcelIsUpToDate_WorkbookRemainsSame() throws Exception
    {

        dbData = new DatabaseData();
        excelData = new  ExcelData();
        workbook = new XSSFWorkbook();
        DatabaseRow dbDetails = new DatabaseRow(1.0,1.0,"bread","43.13");
        ExcelRow excelDetails = new ExcelRow(0,1.0,1.0);
        dbData.Add("43.13",dbDetails);
        excelData.Add(excelDetails,"43.13");
        CreateExcelSheet();
        when(dbService.GetDataFromWarehouse()).thenReturn(dbData);
        when(parser.GetExcelData()).thenReturn(excelData);
        when(parser.GetTotalRows()).thenReturn(1);
        when(parser.GetWorkbook()).thenReturn(workbook);
        ExcelGenerator generator = new ExcelGenerator(dbService,parser);

        generator.GenerateExcel();

        String productName = "bread";
        double quantity = 1.0;
        String barcode = "43.13";
        double price = 1.0;
        Row row = workbook.getSheetAt(0).getRow(0);
        assertTrue(row.getCell(4).getStringCellValue().equals(barcode) );
        assertTrue(row.getCell(6).getNumericCellValue() == quantity );
        assertTrue(row.getCell(5).getStringCellValue().equals(productName));
        assertTrue(row.getCell(2).getNumericCellValue() == price);
    }

    @Test
    public void GenerateExcel_WhenProductsQuantityChange_UpdateWorkbookRow() throws Exception
    {

        dbData = new DatabaseData();
        excelData = new  ExcelData();
        workbook  = new XSSFWorkbook();
        DatabaseRow dbDetails = new DatabaseRow(10,1.1,"bread","43.13");
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
        when(parser.GetWorkbook()).thenReturn(workbook);
        ExcelGenerator generator = new ExcelGenerator(dbService,parser);

        generator.GenerateExcel();

        assertTrue(row.getCell(4).getStringCellValue().equals(barcode));
        assertTrue(row.getCell(6).getNumericCellValue() == 10.0 );
        assertTrue(row.getCell(5).getStringCellValue().equals(productName));
        assertTrue(row.getCell(2).getNumericCellValue() == 1.1);
    }

    @Test
    public void GenerateExcel_WhenAddedNewProduct_InsertRowInToWorkbook() throws Exception
    {

        dbData    = new DatabaseData();
        excelData = new ExcelData();
        workbook  = new XSSFWorkbook();
        DatabaseRow productDetails = new DatabaseRow(1.0,1.0,"bread","43.13");
        DatabaseRow product2Details = new DatabaseRow(1.0,1.0,"paper","43.13");
        ExcelRow excelDetails = new ExcelRow(0, 1.0, 1.0);
        dbData.Add("43.13",productDetails);
        dbData.Add("43.14",product2Details);
        excelData.Add(excelDetails,"43.13");
        CreateExcelSheet();
        when(dbService.GetDataFromWarehouse()).thenReturn(dbData);
        when(parser.GetExcelData()).thenReturn(excelData);
        when(parser.GetTotalRows()).thenReturn(1);
        when(parser.GetWorkbook()).thenReturn(workbook);
        ExcelGenerator generator = new ExcelGenerator(dbService,parser);

        generator.GenerateExcel();

        assertTrue(workbook.getSheetAt(0).getPhysicalNumberOfRows() == 2 );
    }

    @Test
    public void GenerateExcel_WhenAddedNewProductWithZeroQuantity_InsertIgnored() throws Exception
    {

        dbData    = new DatabaseData();
        excelData = new  ExcelData();
        workbook  = new XSSFWorkbook();
        DatabaseRow productDetails = new DatabaseRow(1.0,1.0,"bread","43.13");
        DatabaseRow product2Details = new DatabaseRow(0.0,1.0,"paper","43.13");
        ExcelRow excelDetails = new ExcelRow(0,1.0,1.0);
        dbData.Add("43.13",productDetails);
        dbData.Add("43.14",product2Details);
        excelData.Add(excelDetails,"43.13");
        CreateExcelSheet();
        when(dbService.GetDataFromWarehouse()).thenReturn(dbData);
        when(parser.GetExcelData()).thenReturn(excelData);
        when(parser.GetTotalRows()).thenReturn(1);
        when(parser.GetWorkbook()).thenReturn(workbook);
        ExcelGenerator generator = new ExcelGenerator(dbService,parser);

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
        row.createCell(4).setCellValue(barcode);
        row.createCell(6).setCellValue(quantity);
        row.createCell(5).setCellValue(productName);
        row.createCell(2).setCellValue(price);
    }

}
