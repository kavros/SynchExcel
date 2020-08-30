package model.excel.parser;

public class ExcelRow
{
    public final int row;
    public final Double storageQuantity;
    public final Double lastPrcPr;

    public ExcelRow(int r,Double q, Double l)
    {
        row = r;
        storageQuantity = q;
        lastPrcPr = l;
    }
}
