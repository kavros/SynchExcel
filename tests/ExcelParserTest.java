import model.Columns;
import model.parser.ExcelParser;
import model.ExcelProductDetails;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import static junit.framework.TestCase.assertTrue;

public class ExcelParserTest
{

    @Test
    public void GetExcelData_WhenBarcodeIsString_ReturnsTheCorrectData()
    {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row row = sheet.createRow(0);
        row.createCell(Columns.BARCODE).setCellValue("43.13");
        row.createCell(Columns.LAST_PRICE).setCellValue(1.00f);
        row.createCell(Columns.QUANTITY).setCellValue(6);
        ExcelParser parser = new ExcelParser(workbook);

        ExcelProductDetails result =  parser.GetExcelData().get("43.13");

        ExcelProductDetails expectedResult = new  ExcelProductDetails(6.0, 1.0, 0);
        assertTrue(result.GetLastPrcPr().equals( expectedResult.GetLastPrcPr() ));
        assertTrue(result.GetQuantity().equals( expectedResult.GetQuantity() ));
        assertTrue(result.GetRow() ==  expectedResult.GetRow() );
    }

    @Test
    public void GetExcelData_WhenBarcodeIsDouble_ReturnsTheCorrectData()
    {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row row = sheet.createRow(0);
        row.createCell(Columns.BARCODE).setCellValue(new Double("9999000869"));
        row.createCell(Columns.LAST_PRICE).setCellValue(1.0);
        row.createCell(Columns.QUANTITY).setCellValue(6);
        ExcelParser parser = new ExcelParser(workbook);

        ExcelProductDetails result =  parser.GetExcelData().get("9999000869");

        ExcelProductDetails expectedResult = new  ExcelProductDetails(6.0, 1.0, 0);
        assertTrue(result.GetLastPrcPr().equals( expectedResult.GetLastPrcPr() ));
        assertTrue(result.GetQuantity().equals( expectedResult.GetQuantity() ));
        assertTrue(result.GetRow() ==  expectedResult.GetRow() );
    }
}
