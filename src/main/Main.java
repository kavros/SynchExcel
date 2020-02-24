package main;
import model.*;
import model.dbReader.CredentialsService;
import model.dbReader.DatabaseService;
import model.generator.ExcelGenerator;
import model.parser.ExcelParser;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class Main
{
    public static final String inputExcel   = "./excel/a.xlsx";

    public static void main(String[] args) throws Exception {

        XSSFWorkbook workbook = new  XSSFWorkbook(inputExcel);
        ExcelParser exlParser = new ExcelParser(workbook);
        DatabaseService conn =  new DatabaseService(new CredentialsService());
        ExcelGenerator gen = new ExcelGenerator(conn,exlParser);

        if ( gen.GenerateExcel() == State.SUCCESS)
            gen.SaveExcel();
        else
            System.err.println("Failed to generate the excel file");

        //workbook.close();
    }
}
