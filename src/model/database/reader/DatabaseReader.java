package model.database.reader;

import model.database.credentials.Credentials;
import model.database.credentials.CredentialsReader;
import java.sql.*;

public class DatabaseReader
{
    private final CredentialsReader credManager;
    private DatabaseData dbData;
    public static final String credFilePath = "./credentials.txt";

    public DatabaseReader(CredentialsReader _credManager) throws Exception
    {
        dbData = new DatabaseData();
        credManager = _credManager;
    }

    public DatabaseData GetDataFromWarehouse()
    {
        if(dbData.Size() != 0)
            return dbData;

        Credentials credentials = credManager.GetCredentials(credFilePath);
        try (Connection conn =
                     DriverManager.getConnection(credentials.GetDbURL(), credentials.GetUsername(), credentials.GetPassword());)
        {
            if (conn != null)
            {
                Statement st = conn.createStatement();

                ResultSet res = st.executeQuery( GetProductsQuery() );

                while (res!= null && res.next())
                {
                    AddProduct(res);
                }
                conn.commit();
            }
        }
        catch (SQLException e)
        {
            System.err.println("Failed to get data from database");
            e.printStackTrace();
        }
        return dbData;
    }

    private String GetProductsQuery()
    {
        String storageId = "2";
        return "select sFactCode,sstRemain1,sLastPrcPr,sname,sCode " +
                "        FROM SSTORE " +
                "        JOIN smast on sstore.sfileId=smast.sfileid " +
                "        where spaFileIdNo="+ storageId;
    }

    private void AddProduct(ResultSet res) throws SQLException
    {
        double lastPrcPr    = res.getDouble(3);
        double quantity     = res.getDouble(2);
        String barcode      = res.getString(1);
        String productName  = res.getString(4);
        String productCode  = res.getString(5);

        DatabaseRow val = new DatabaseRow(quantity,lastPrcPr,productName,productCode);
        dbData.Add(barcode,val);
    }
}
