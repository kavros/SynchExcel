import model.ExcelCell;
import model.parser.ExcelParser;
import model.parser.ExcelProductDetails;
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
        row.createCell(ExcelCell.BARCODE).setCellValue("43.13");
        row.createCell(ExcelCell.LAST_PRICE).setCellValue(1.00f);
        row.createCell(ExcelCell.QUANTITY).setCellValue(6);
        ExcelParser parser = new ExcelParser(workbook);

        ExcelProductDetails result =  parser.GetExcelData().get("43.13");

        ExcelProductDetails expectedResult = new  ExcelProductDetails(6.0, 1.0, 0);
        assertTrue(result.lastPrcPr.equals( expectedResult.lastPrcPr ));
        assertTrue(result.quantity.equals( expectedResult.quantity ));
        assertTrue(result.row ==  expectedResult.row );
    }

    @Test
    public void GetExcelData_WhenBarcodeIsDouble_ReturnsTheCorrectData()
    {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row row = sheet.createRow(0);
        row.createCell(ExcelCell.BARCODE).setCellValue(new Double("9999000869"));
        row.createCell(ExcelCell.LAST_PRICE).setCellValue(1.0);
        row.createCell(ExcelCell.QUANTITY).setCellValue(6);
        ExcelParser parser = new ExcelParser(workbook);

        ExcelProductDetails result =  parser.GetExcelData().get("9999000869");

        ExcelProductDetails expectedResult = new  ExcelProductDetails(6.0, 1.0, 0);
        assertTrue(result.lastPrcPr.equals( expectedResult.lastPrcPr ));
        assertTrue(result.quantity.equals( expectedResult.quantity ));
        assertTrue(result.row ==  expectedResult.row );
    }
}
