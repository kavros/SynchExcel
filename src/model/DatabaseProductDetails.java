package model;

public class DatabaseProductDetails
{
    public final double quantity;
    public final double lastPrcPr;
    public final String productName;
    public final String productCode;

    public DatabaseProductDetails(double q, double l, String p, String c)
    {
        quantity = q;
        lastPrcPr = l;
        productName = p;
        productCode = c;
    }
}
