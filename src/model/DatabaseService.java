package model;

import java.sql.*;
import java.util.HashMap;

public class DatabaseService
{
    CredentialsService credManager;
    HashMap<String, DatabaseProductDetails> storageHashMap;

    public DatabaseService(CredentialsService _credManager)
    {
        credManager = _credManager;
        State retrieved = credManager.GetCredentialsFromFile(Constants.credFilePath);
        if( retrieved == State.FAILURE)
        {
            credManager.GetUserInputs();
        }
        storageHashMap = new HashMap<>();
    }

    public HashMap<String, DatabaseProductDetails> GetDataFromWarehouse() throws Exception {

        if(!storageHashMap.isEmpty())
            return storageHashMap;

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
                    storageHashMap.put(barcode,val);
                }
                conn.commit();
            }
        }
        finally
        {
            CloseDbConnection(conn);
        }
        return  storageHashMap;
    }

    public void CloseDbConnection(Connection conn)
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
