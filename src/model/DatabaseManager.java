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
        HashValue(double q,double l,String p)
        {
            quantity = q;
            lastPrcPr = l;
            productName = p;
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
                /*SELECT SSTORE.sfileid,sname,sFactCode, sstRemain1,sLastPrcPr,stDate,stDoc,stTransKind,SpaFileIdNo
                FROM SSTORE
                JOIN SMAST on SSTORE.sfileId=SMAST.sfileid
                JOIN STRN on STRN.sFileId = SSTORE.sFileId
                where SpaFileIdNo='2' and (stTranskind=7 or stTranskind=6) and stLocation=2
                order by stDate desc;*/


                while (res.next())
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
