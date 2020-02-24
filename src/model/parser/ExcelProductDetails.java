package model.parser;

public class ExcelProductDetails
{
    public final int row;
    public final Double quantity;
    public final Double lastPrcPr;

    public ExcelProductDetails(Double _quantity, Double _lastPrcPr, int _row)
    {
        row = _row;
        quantity = _quantity;
        lastPrcPr = _lastPrcPr;
    }
}