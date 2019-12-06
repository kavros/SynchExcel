package main;
import model.CredentialsService;
import model.DatabaseService;
import model.ExcelGenerator;
import model.ExcelParser;


public class Main
{


    public static void main(String[] args)
    {
        try
        {
            ExcelParser exlParser = new ExcelParser();
            DatabaseService conn =  new DatabaseService(new CredentialsService());
            ExcelGenerator gen = new ExcelGenerator(conn,exlParser);

            gen.GenerateExcel();

        }catch (Exception e)
        {
            System.out.println(e);
        }


    }
}
