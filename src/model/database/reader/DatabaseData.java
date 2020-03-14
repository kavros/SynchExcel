package model.database.reader;

import java.util.HashMap;
import java.util.Set;

public class DatabaseData
{
    private final HashMap<String, DatabaseRow> products = new HashMap<>();

    public void Add(String barcode,DatabaseRow row)
    {
        products.put(barcode,row);
    }

    public DatabaseRow Get(String barcode)
    {
        return products.get(barcode);
    }

    public int Size()
    {
        return products.size();
    }

    public Set<String> GetBarcodes()
    {
        return products.keySet();
    }
}
