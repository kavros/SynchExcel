import model.ExcelCellNumber;
import model.parser.ExcelData;
import model.parser.ExcelParser;
import model.parser.ExcelRow;
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
        row.createCell(ExcelCellNumber.BARCODE).setCellValue("43.13");
        row.createCell(ExcelCellNumber.LAST_PRICE).setCellValue(1.00f);
        row.createCell(ExcelCellNumber.QUANTITY).setCellValue(6);
        ExcelParser parser = new ExcelParser(workbook);

        ExcelRow result =  parser.GetExcelData().Get("43.13");

        ExcelRow expectedResult = new ExcelRow(0, 6.0, 1.0);
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
        row.createCell(ExcelCellNumber.BARCODE).setCellValue(new Double("9999000869"));
        row.createCell(ExcelCellNumber.LAST_PRICE).setCellValue(1.0);
        row.createCell(ExcelCellNumber.QUANTITY).setCellValue(6);
        ExcelParser parser = new ExcelParser(workbook);

        ExcelRow result =  parser.GetExcelData().Get("9999000869");

        ExcelRow expectedResult = new ExcelRow(0, 6.0, 1.0);
        assertTrue(result.lastPrcPr.equals( expectedResult.lastPrcPr ));
        assertTrue(result.quantity.equals( expectedResult.quantity ));
        assertTrue(result.row ==  expectedResult.row );
    }
}
