package tests;

import model.CredentialsService;
import model.DatabaseService;
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

    @Test
    public void GetDataFromWarehouse_WhenDbResultSetIsNull_ReturnsEmptyHashMap() throws SQLException {

        final Connection connection = mock(Connection.class);
        final CredentialsService credServ = mock(CredentialsService.class);
        when(credServ.GetDbURL()).thenReturn("url");
        when(credServ.GetUsername()).thenReturn("kef");
        when(credServ.GetPass()).thenReturn("pass");
        DatabaseService dbServer =  new DatabaseService(credServ);//mock(DatabaseService.class);
        final Statement statement = mock(Statement.class);
        when(connection.createStatement()).thenReturn(statement);
        mockStatic(DriverManager.class);
        expect(DriverManager.getConnection("url","kef","pass"))
                .andReturn(connection);
        replay(DriverManager.class);

        HashMap result = dbServer.GetDataFromWarehouse();

        assertTrue (result.size() == 0);
    }

}