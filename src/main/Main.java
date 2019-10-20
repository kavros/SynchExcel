package main;
import model.ExcelGenerator;


public class Main
{


    public static void main(String[] args)
    {
        try
        {
            ExcelGenerator gen =new ExcelGenerator();
            gen.GenerateExcel();

        }catch (Exception e)
        {
            System.out.println(e);
        }


    }
}
