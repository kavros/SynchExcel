
import model.database.credentials.Credentials;
import model.database.credentials.CredentialsReader;
import model.database.reader.DatabaseData;
import model.database.reader.DatabaseReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import java.sql.*;
import org.powermock.modules.junit4.PowerMockRunner;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;


@RunWith(PowerMockRunner.class)
@PrepareForTest({DriverManager.class, DatabaseReader.class})
public class DatabaseReaderTest
{
    private CredentialsReader credReader;
    private Statement statement;

    @Before
    public void Setup()throws SQLException
    {
        Connection connection = mock(Connection.class);
        credReader = mock(CredentialsReader.class);
        Credentials credentials = new Credentials();
        credentials.SetDbUrl("url");
        credentials.SetPassword("pass");
        credentials.SetUsername("kef");
        when(credReader.GetCredentials(DatabaseReader.CRED_FILE_PATH)).thenReturn(credentials);
        statement = mock(Statement.class);
        when(connection.createStatement()).thenReturn(statement);
        mockStatic(DriverManager.class);
        expect(DriverManager.getConnection("url","kef","pass"))
                .andReturn(connection);
    }

    @Test
    public void GetDataFromWarehouse_WhenDbResultSetIsNull_ThenReturnsEmptyHashMap() throws Exception
    {
        DatabaseReader dbServer =  new DatabaseReader(credReader);
        replay(DriverManager.class);

        DatabaseData result = dbServer.GetDataFromWarehouse();
        assertTrue (result.Size() == 0);
    }

    @Test
    public void GetDataFromWarehouse_WhenDbReturnsResultSetExist_ThenReturnsResults() throws Exception
    {
        DatabaseReader dbServer =  new DatabaseReader(credReader);
        ResultSet resultSet = mock(ResultSet.class);
        Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString(1)).thenReturn("4313");
        when(resultSet.getString(4)).thenReturn("Bread");
        when(resultSet.getDouble(2)).thenReturn(2.0);
        when(resultSet.getDouble(6)).thenReturn(1.0);
        when(resultSet.getDouble(3)).thenReturn(1.0);
        when(resultSet.getString(5)).thenReturn("43.13");
        when(statement
                .executeQuery(Mockito.anyString())).thenReturn(resultSet);
        replay(DriverManager.class);

        DatabaseData result = dbServer.GetDataFromWarehouse();

        assertTrue (result.Get("4313").lastPrcPr == 1.0);
        assertTrue (result.Get("4313").productName.equals("Bread"));
        assertTrue (result.Get("4313").storageQuantity == 2.0 );
        assertTrue (result.Get("4313").productCode.equals("43.13"));
        assertTrue (result.Get("4313").storeQuantity == 1);
    }

    @Test
    public void GetDataFromWarehouse_WhenCalledTwice_ThenCallDatabaseOnce() throws Exception
    {
        DatabaseReader dbServer =  new DatabaseReader(credReader);
        ResultSet resultSet = mock(ResultSet.class);
        Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString(1)).thenReturn("4313");
        when(resultSet.getString(4)).thenReturn("Bread");
        when(resultSet.getDouble(2)).thenReturn(2.0);
        when(resultSet.getDouble(3)).thenReturn(1.0);
        when(statement
                .executeQuery(Mockito.anyString())).thenReturn(resultSet);
        replay(DriverManager.class);

        dbServer.GetDataFromWarehouse();
        dbServer.GetDataFromWarehouse();

        verify(statement, times(1)).executeQuery(anyString());
    }
}