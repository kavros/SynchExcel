package model;

import java.sql.*;
import java.util.HashMap;

public class DatabaseManager
{

    public class HashValue
    {
        double quantity;
        double lastPrcPr;
        String productName;
        HashValue(double q,double l,String p,String d)
        {
            quantity = q;
            lastPrcPr = l;
            productName = p;
        }
    }

    private final String storageId = "2";
    CredentialsManager credManager;

    public DatabaseManager()
    {
        credManager = new CredentialsManager();
        state retrieved = credManager.GetCredentialsFromFile(Constants.credFilePath);
        if( retrieved == state.FAILURE)
        {
            credManager.GetUserInputs();
        }
    }

    public HashMap<String,HashValue> GetDataFromWarehouse() throws SQLException {
        HashMap<String,HashValue> storageHashMap = new HashMap<>();

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
                ResultSet res = st.executeQuery(
                        "select sFactCode,sstRemain1,sLastPrcPr,sname" +
                        "        FROM SSTORE " +
                        "        JOIN smast on sstore.sfileId=smast.sfileid " +
                        "        where spaFileIdNo="+storageId);

                while (res.next())
                {
                    double lastPrcPr    = res.getDouble(3);
                    double quantity     = res.getDouble(2);
                    String barcode      = res.getString(1);
                    String productName  = res.getString(4);

                    HashValue val = new HashValue(quantity,lastPrcPr,productName," ");
                    storageHashMap.put(barcode,val);
                }

                conn.commit();
            }
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
            CloseDbConnection(conn);
        }
        return  storageHashMap;
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
