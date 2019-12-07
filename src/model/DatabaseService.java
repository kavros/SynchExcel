package model;

import java.sql.*;
import java.util.HashMap;

public class DatabaseService
{

    public class HashValue
    {
        public double quantity;
        public double lastPrcPr;
        public String productName;
        HashValue(double q,double l,String p)
        {
            quantity = q;
            lastPrcPr = l;
            productName = p;
        }
    }

    private final String storageId = "2";
    CredentialsService credManager;

    public DatabaseService(CredentialsService _credManager)
    {
        credManager = _credManager;
        State retrieved = credManager.GetCredentialsFromFile(Constants.credFilePath);
        if( retrieved == State.FAILURE)
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

                while (res!= null && res.next())
                {
                    double lastPrcPr    = res.getDouble(3);
                    double quantity     = res.getDouble(2);
                    String barcode      = res.getString(1);
                    String productName  = res.getString(4);

                    HashValue val = new HashValue(quantity,lastPrcPr,productName);
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
