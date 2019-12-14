package model;

public class DatabaseProductDetails
{
    public double quantity;
    public double lastPrcPr;
    public String productName;

    public DatabaseProductDetails(double q, double l, String p)
    {
        quantity = q;
        lastPrcPr = l;
        productName = p;
    }
}
