package model.database.reader;

public class DatabaseRow
{
    public final double storageQuantity;
    public final double lastPrcPr;
    public final String productName;
    public final String productCode;
    public final double storeQuantity;
    public final float vatCode;

    public DatabaseRow(double _storageQuantity, double _lastPrcPr, String _productName, String _productCode,
                       double _storeQuantity, float _vatCode)
    {
        storageQuantity = _storageQuantity;
        storeQuantity   = _storeQuantity;
        lastPrcPr       = _lastPrcPr;
        productName     = _productName;
        productCode     = _productCode;
        vatCode = _vatCode;
    }

    @Override
    public String toString() {
        return "DatabaseRow{" +
                "storageQuantity=" + storageQuantity +
                ", lastPrcPr=" + lastPrcPr +
                ", productName='" + productName + '\'' +
                ", productCode='" + productCode + '\'' +
                ", storeQuantity=" + storeQuantity +
                ", fpa="+ vatCode +
                '}';
    }
}
