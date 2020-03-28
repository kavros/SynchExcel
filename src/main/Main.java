package main;
import model.database.reader.DatabaseReader;
import model.excel.generator.ExcelGenerator;
import model.excel.parser.ExcelParser;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main
{
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static final String inputExcel = "./excel/a.xlsx";

    public static void main(String[] args) throws Exception
    {
        AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext();
        appContext.scan("model");
        appContext.refresh();
        logger.info("Application started");

        ZipSecureFile.setMinInflateRatio(0);
        XSSFWorkbook workbook = new  XSSFWorkbook(inputExcel);

        ExcelParser exlParser = new ExcelParser(workbook);

        DatabaseReader dbReader = (DatabaseReader) appContext.getBean("databaseReader");
        ExcelGenerator gen = new ExcelGenerator(dbReader,exlParser,workbook);
        gen.GenerateExcel();
        gen.SaveExcel();
        //workbook.close();
    }
}
