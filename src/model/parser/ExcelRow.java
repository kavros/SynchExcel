package model.parser;

public class ExcelRow
{
    public final int row;
    public final Double quantity;
    public final Double lastPrcPr;

    public ExcelRow(int r,Double q, Double l)
    {
        row = r;
        quantity = q;
        lastPrcPr = l;
    }
}
