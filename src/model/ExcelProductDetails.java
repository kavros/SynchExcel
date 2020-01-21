package model;

public class ExcelProductDetails
{
    int row;
    Double quantity;
    Double lastPrcPr;


    public int GetRow() {
        return row;
    }

    public Double GetQuantity() {
        return quantity;
    }

    public Double GetLastPrcPr() {
        return lastPrcPr;
    }

    public ExcelProductDetails(Double _quantity, Double _lastPrcPr, int _row)
    {
        row = _row;
        quantity = _quantity;
        lastPrcPr = _lastPrcPr;
    }
}