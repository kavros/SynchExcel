import model.*;
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
    private HashMap<String, DatabaseProductDetails> dbData;
    private HashMap<String, ExcelProductDetails> excelData;
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
        when(dbService.GetDataFromWarehouse()).thenReturn(new HashMap<>());

        State result = generator.GenerateExcel();

        assertTrue(result == State.FAILURE);
    }

    @Test
    public void GenerateExcel_WhenExcelIsUpToDate_WorkbookRemainsSame() throws Exception
    {

        dbData = new HashMap<>();
        excelData = new  HashMap<>();
        workbook = new XSSFWorkbook();
        DatabaseProductDetails dbDetails = new DatabaseProductDetails(1.0,1.0,"bread","43.13");
        ExcelProductDetails excelDetails = new ExcelProductDetails(1.0,1.0,0);
        dbData.put("43.13",dbDetails);
        excelData.put("43.13",excelDetails);
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

        dbData = new HashMap<>();
        excelData = new  HashMap<>();
        workbook  = new XSSFWorkbook();
        DatabaseProductDetails dbDetails = new DatabaseProductDetails(10,1.1,"bread","43.13");
        ExcelProductDetails excelDetails = new ExcelProductDetails(1.0,1.0,0);
        dbData.put("43.13",dbDetails);
        excelData.put("43.13",excelDetails);
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

        dbData    = new HashMap<>();
        excelData = new  HashMap<>();
        workbook  = new XSSFWorkbook();
        DatabaseProductDetails productDetails = new DatabaseProductDetails(1.0,1.0,"bread","43.13");
        DatabaseProductDetails product2Details = new DatabaseProductDetails(1.0,1.0,"paper","43.13");
        ExcelProductDetails excelDetails = new ExcelProductDetails(1.0,1.0,0);
        dbData.put("43.13",productDetails);
        dbData.put("43.14",product2Details);
        excelData.put("43.13",excelDetails);
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

        dbData    = new HashMap<>();
        excelData = new  HashMap<>();
        workbook  = new XSSFWorkbook();
        DatabaseProductDetails productDetails = new DatabaseProductDetails(1.0,1.0,"bread","43.13");
        DatabaseProductDetails product2Details = new DatabaseProductDetails(0.0,1.0,"paper","43.13");
        ExcelProductDetails excelDetails = new ExcelProductDetails(1.0,1.0,0);
        dbData.put("43.13",productDetails);
        dbData.put("43.14",product2Details);
        excelData.put("43.13",excelDetails);
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
