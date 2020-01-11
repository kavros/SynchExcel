
import model.CredentialsService;
import model.DatabaseService;
import model.DatabaseProductDetails;
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
import java.util.HashMap;


@RunWith(PowerMockRunner.class)
@PrepareForTest({DriverManager.class,DatabaseService.class})
public class DatabaseServiceTest
{
    Connection connection;
    CredentialsService credServ ;
    Statement statement;

    @Before
    public void Setup()throws SQLException
    {
        connection = mock(Connection.class);
        credServ = mock(CredentialsService.class);
        when(credServ.GetDbURL()).thenReturn("url");
        when(credServ.GetUsername()).thenReturn("kef");
        when(credServ.GetPass()).thenReturn("pass");
        statement = mock(Statement.class);
        when(connection.createStatement()).thenReturn(statement);
        mockStatic(DriverManager.class);
        expect(DriverManager.getConnection("url","kef","pass"))
                .andReturn(connection);
    }

    @Test
    public void GetDataFromWarehouse_WhenDbResultSetIsNull_ReturnsEmptyHashMap()  throws SQLException {


        DatabaseService dbServer =  new DatabaseService(credServ);
        replay(DriverManager.class);

        HashMap result = dbServer.GetDataFromWarehouse();

        assertTrue (result.size() == 0);
    }

    @Test
    public void GetDataFromWarehouse_WhenDbReturnsResultSetExist_ReturnsResults() throws SQLException
    {
        DatabaseService dbServer =  new DatabaseService(credServ);
        ResultSet resultSet = mock(ResultSet.class);
        Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString(1)).thenReturn("4313");
        when(resultSet.getString(4)).thenReturn("Bread");
        when(resultSet.getDouble(2)).thenReturn(2.0);
        when(resultSet.getDouble(3)).thenReturn(1.0);
        when(statement
                .executeQuery(Mockito.anyString())).thenReturn(resultSet);
        replay(DriverManager.class);

        HashMap<String, DatabaseProductDetails> result = dbServer.GetDataFromWarehouse();

        assertTrue (result.get("4313").lastPrcPr == 1.0);
        assertTrue (result.get("4313").productName.equals("Bread"));
        assertTrue (result.get("4313").quantity == 2.0 );
    }

    @Test
    public void GetDataFromWarehouse_WhenCalledTwice_ReturnsCallDatabaseOnce() throws SQLException
    {
        DatabaseService dbServer =  new DatabaseService(credServ);
        ResultSet resultSet = mock(ResultSet.class);
        Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString(1)).thenReturn("4313");
        when(resultSet.getString(4)).thenReturn("Bread");
        when(resultSet.getDouble(2)).thenReturn(2.0);
        when(resultSet.getDouble(3)).thenReturn(1.0);
        when(statement
                .executeQuery(Mockito.anyString())).thenReturn(resultSet);
        replay(DriverManager.class);

        HashMap<String, DatabaseProductDetails> result = dbServer.GetDataFromWarehouse();
        dbServer.GetDataFromWarehouse();

        verify(statement, times(1)).executeQuery(anyString());
    }
}