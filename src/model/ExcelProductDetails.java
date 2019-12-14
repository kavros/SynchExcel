package model;

public class ExcelProductDetails
{
    int row;
    Double quantity;
    Double lastPrcPr;

    public ExcelProductDetails(Double _quantity, Double _lastPrcPr, int _row)
    {
        row = _row;
        quantity = _quantity;
        lastPrcPr = _lastPrcPr;
    }
}