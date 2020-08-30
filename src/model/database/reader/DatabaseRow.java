package model.database.reader;

public class DatabaseRow
{
    public final double storageQuantity;
    public final double lastPrcPr;
    public final String productName;
    public final String productCode;
    public final double storeQuantity;

    public DatabaseRow(double _storageQuantity, double _lastPrcPr, String _productName, String _productCode, double _storeQuantity)
    {
        storageQuantity = _storageQuantity;
        storeQuantity   = _storeQuantity;
        lastPrcPr       = _lastPrcPr;
        productName     = _productName;
        productCode     = _productCode;
    }

    @Override
    public String toString() {
        return "DatabaseRow{" +
                "storageQuantity=" + storageQuantity +
                ", lastPrcPr=" + lastPrcPr +
                ", productName='" + productName + '\'' +
                ", productCode='" + productCode + '\'' +
                ", storeQuantity=" + storeQuantity +
                '}';
    }
}
