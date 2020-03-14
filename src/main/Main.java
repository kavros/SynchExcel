package main;
import model.database.credentials.CredentialsReader;
import model.database.credentials.CredentialsWriter;
import model.cipher.Cipher;
import model.database.reader.DatabaseReader;
import model.excel.generator.ExcelGenerator;
import model.excel.parser.ExcelParser;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class Main
{
    public static final String inputExcel   = "./excel/a.xlsx";

    public static void main(String[] args) throws Exception
    {
        Cipher ed = new Cipher();
        CredentialsWriter cw = new CredentialsWriter(ed);
        XSSFWorkbook workbook = new  XSSFWorkbook(inputExcel);
        ExcelParser exlParser = new ExcelParser(workbook);
        DatabaseReader dbReader =  new DatabaseReader(new CredentialsReader(cw,ed));
        ExcelGenerator gen = new ExcelGenerator(dbReader,exlParser,workbook);
        gen.GenerateExcel();
        gen.SaveExcel();

        //workbook.close();
    }
}
