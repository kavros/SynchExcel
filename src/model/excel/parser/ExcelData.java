package model.excel.parser;

import java.util.HashMap;

public class ExcelData
{

    private final HashMap<String, ExcelRow> excelData;

    public ExcelData()
    {
        excelData = new HashMap<>();
    }

    public void Add(ExcelRow row, String barcode)
    {
        excelData.put(barcode,row);
    }

    public ExcelRow Get(String barcode)
    {
        return excelData.get(barcode);
    }

    public int Size()
    {
        return excelData.size();
    }
}