import model.Constants;
import model.ExcelParser;
import model.ExcelProductDetails;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import static junit.framework.TestCase.assertTrue;
import java.util.HashMap;

public class ExcelParserTest
{

    @Test
    public void GetExcelData_WhenBarcodeIsString_ReturnsTheCorrectData()
    {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row row = sheet.createRow(0);
        row.createCell(Constants.bCellNum).setCellValue("43.13");
        row.createCell(Constants.lastPrcPrCellNum).setCellValue(1.00f);
        row.createCell(Constants.qCellNum).setCellValue(6);
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
        row.createCell(Constants.bCellNum).setCellValue(new Double("9999000869"));
        row.createCell(Constants.lastPrcPrCellNum).setCellValue(1.0);
        row.createCell(Constants.qCellNum).setCellValue(6);
        ExcelParser parser = new ExcelParser(workbook);

        ExcelProductDetails result =  parser.GetExcelData().get("9999000869");

        ExcelProductDetails expectedResult = new  ExcelProductDetails(6.0, 1.0, 0);
        assertTrue(result.GetLastPrcPr().equals( expectedResult.GetLastPrcPr() ));
        assertTrue(result.GetQuantity().equals( expectedResult.GetQuantity() ));
        assertTrue(result.GetRow() ==  expectedResult.GetRow() );
    }
}
