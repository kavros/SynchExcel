package model;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class DatabaseManager
{

    public class HashValue
    {
        double quantity;
        double lastPrcPr;
        String productName;
        String lastInOrOutDate;
        HashValue(double q,double l,String p,String d)
        {
            quantity = q;
            lastPrcPr = l;
            productName = p;
            lastInOrOutDate = d;
        }
    }

    private final String storageId = "2";

    public DatabaseManager()
    {

    }


    public HashMap<String,HashValue> GetDataFromWarehouse()
    {
        HashMap<String,HashValue> storageHashMap = new HashMap<>();
        CredentialsManager credManager = new CredentialsManager(Constants.credFilePath);

        Connection conn = null;
        try
        {
            conn = DriverManager.getConnection(credManager.GetDbURL(),
                                                credManager.GetUsername(),
                                                credManager.GetPass()
                                            );
            if (conn != null)
            {
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

                for(String barcode : storageHashMap.keySet())
                {
                        ResultSet res2 = st.executeQuery(
                        "select stdate "+
                                "from STRN"+
                                " JOIN SMAST on SMAST.sfileId=STRN.sfileid"+
                                " where (stTranskind=7 or stTranskind=6) and stLocation=2 and"+
                                " sFactCode='"+barcode+
                                "' order by stDate desc");
                    res2.next();

                    storageHashMap.get(barcode).lastInOrOutDate = res2.getDate(1).toString();
                    //System.out.println(barcode+" "+storageHashMap.get(barcode).lastInOrOutDate);

                }

                conn.commit();
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
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
