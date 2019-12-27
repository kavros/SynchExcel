package model;

import java.sql.Struct;

public class DatabaseProductDetails
{
    public double quantity;
    public double lastPrcPr;
    public String productName;
    public String productCode;

    public DatabaseProductDetails(double q, double l, String p, String c)
    {
        quantity = q;
        lastPrcPr = l;
        productName = p;
        productCode = c;
    }
}
