package main;
import model.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class Main
{


    public static void main(String[] args)
    {
        try
        {
            XSSFWorkbook workbook = new  XSSFWorkbook(Constants.inputExcel);
            ExcelParser exlParser = new ExcelParser(workbook);
            DatabaseService conn =  new DatabaseService(new CredentialsService());
            ExcelGenerator gen = new ExcelGenerator(conn,exlParser);

            if ( gen.GenerateExcel() == State.SUCCESS)
                gen.SaveExcel();
            else
                System.err.println("Failed to generate the excel file");

            //workbook.close();
        }catch (Exception e)
        {
            System.out.println(e);
        }


    }
}
