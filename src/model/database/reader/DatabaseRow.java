package model.database.reader;

public class DatabaseRow
{
    public final double quantity_2;
    public final double lastPrcPr;
    public final String productName;
    public final String productCode;
    public final double quantity_1;

    public DatabaseRow(double q2, double l, String p, String c, double q1)
    {
        quantity_2 = q2;
        quantity_1 = q1;
        lastPrcPr = l;
        productName = p;
        productCode = c;
    }

    @Override
    public String toString() {
        return "DatabaseRow{" +
                "quantity_2=" + quantity_2 +
                ", lastPrcPr=" + lastPrcPr +
                ", productName='" + productName + '\'' +
                ", productCode='" + productCode + '\'' +
                ", quantity_1=" + quantity_1 +
                '}';
    }
}
