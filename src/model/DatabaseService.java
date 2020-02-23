package model;

import java.sql.*;
import java.util.HashMap;

public class DatabaseService
{
    private final CredentialsService credManager;
    private final HashMap<String, DatabaseProductDetails> productsInWarehouse = new HashMap<>();
    public static final String credFilePath = "./credentials.txt";

    public DatabaseService(CredentialsService _credManager)
    {
        credManager = _credManager;
        State retrieved = credManager.GetCredentialsFromFile(credFilePath);
        if( retrieved == State.FAILURE)
        {
            credManager.GetUserInputs();
        }
    }

    public HashMap<String, DatabaseProductDetails> GetDataFromWarehouse() throws Exception {

        if(!productsInWarehouse.isEmpty())
            return productsInWarehouse;

        Connection conn = null;
        try
        {
            conn = DriverManager.getConnection(credManager.GetDbURL(),
                                                credManager.GetUsername(),
                                                credManager.GetPass()
                                            );
            if (conn != null)
            {
                credManager.SaveCredentials();
                Statement st = conn.createStatement();
                String storageId = "2";
                ResultSet res = st.executeQuery(
                        "select sFactCode,sstRemain1,sLastPrcPr,sname,sCode " +
                        "        FROM SSTORE " +
                        "        JOIN smast on sstore.sfileId=smast.sfileid " +
                        "        where spaFileIdNo="+ storageId);

                while (res!= null && res.next())
                {
                    double lastPrcPr    = res.getDouble(3);
                    double quantity     = res.getDouble(2);
                    String barcode      = res.getString(1);
                    String productName  = res.getString(4);
                    String productCode  = res.getString(5);

                    DatabaseProductDetails val = new DatabaseProductDetails(quantity,lastPrcPr,productName,productCode);
                    productsInWarehouse.put(barcode,val);
                }
                conn.commit();
            }
        }
        finally
        {
            CloseDbConnection(conn);
        }
        return productsInWarehouse;
    }

    private void CloseDbConnection(Connection conn)
    {
        try
        {
            if (conn != null && !conn.isClosed())
            {
                conn.close();
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }
}
