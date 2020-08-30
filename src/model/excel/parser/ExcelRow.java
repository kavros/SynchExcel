package model.excel.parser;

public class ExcelRow
{
    public final int row;
    public final Double quantity_2;
    public final Double lastPrcPr;

    public ExcelRow(int r,Double q, Double l)
    {
        row = r;
        quantity_2 = q;
        lastPrcPr = l;
    }
}
