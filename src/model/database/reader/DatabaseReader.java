package model.database.reader;

import model.database.credentials.Credentials;
import model.database.credentials.CredentialsReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.sql.*;

@Component("databaseReader")
public class DatabaseReader
{
    @Autowired
    private final CredentialsReader credManager;
    private DatabaseData dbData;
    public static final String CRED_FILE_PATH = "./credentials.txt";
    private static final Logger logger = LoggerFactory.getLogger(DatabaseReader.class);

    public DatabaseReader(CredentialsReader _credManager)
    {
        dbData = new DatabaseData();
        credManager = _credManager;
    }

    public DatabaseData GetDataFromWarehouse()
    {
        if(dbData.Size() != 0)
            return dbData;

        Credentials credentials = credManager.GetCredentials(CRED_FILE_PATH);
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
            logger.error("Failed to get data from database");
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
