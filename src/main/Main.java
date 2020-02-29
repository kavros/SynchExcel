package main;
import model.credentialsReaderWriter.CredentialsReader;
import model.dbReader.DatabaseReader;
import model.generator.ExcelGenerator;
import model.parser.ExcelParser;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class Main
{
    public static final String inputExcel   = "./excel/a.xlsx";

    public static void main(String[] args) throws Exception
    {

        XSSFWorkbook workbook = new  XSSFWorkbook(inputExcel);
        ExcelParser exlParser = new ExcelParser(workbook);
        DatabaseReader dbReader =  new DatabaseReader(new CredentialsReader());
        ExcelGenerator gen = new ExcelGenerator(dbReader,exlParser);
        gen.GenerateExcel();
        gen.SaveExcel();

        //workbook.close();
    }
}
