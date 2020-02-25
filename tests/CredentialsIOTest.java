
import model.credentialsReaderWriter.CredentialsIO;
import model.dbReader.DatabaseReader;
import model.State;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertTrue;


public class CredentialsIOTest
{
    @Test
    public void GetUserInputs_WhenInputsProvided_ShouldSetDbUrl()
    {
        String simulatedUserInput = "kef" + System.getProperty("line.separator") +
            "pass" + System.getProperty("line.separator") +
            "Alexis-PC" + System.getProperty("line.separator") +
            "Cmp005" + System.getProperty("line.separator");
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));
        CredentialsIO srv = new CredentialsIO();

        srv.GetUserInputs();

        assertTrue(srv.GetDbURL().equals(
                "jdbc:sqlserver://Alexis-PC" + ";" + "databaseName=Cmp005"
        ));
    }

    @Test(expected = NullPointerException.class)
    public void SaveCredentials_WhenFilePathNotGiven_ShouldTrowException() throws Exception {
        CredentialsIO srv = new CredentialsIO();

        srv.SaveCredentials();
    }

    @Test
    public void GetCredentialsFromFile_WhenFileNotExist_ReturnFail()
    {
        CredentialsIO srv = new CredentialsIO();

        State res = srv.GetCredentialsFromFile("fileNotFound");

        assertTrue(res == State.FAILURE);
    }

    @Test
    public void GetCredentialsFromFile_WhenFileIsCorrect_ReturnsCorrectContent()
    {
        CredentialsIO srv = new CredentialsIO();

        State res = srv.GetCredentialsFromFile(DatabaseReader.credFilePath);

        assertTrue(srv.GetUsername().equals("kef"));
        assertTrue(srv.GetDatabaseName().equals("Cmp005"));
        assertTrue(res == State.SUCCESS);
    }

    @Test
    public void SaveCredentials_WhenValidSaveCredentials_ThenRetrieveThemCorrectly() throws Exception {
        String simulatedUserInput = "kef" + System.getProperty("line.separator") +
                "pass" + System.getProperty("line.separator") +
                "localhost" + System.getProperty("line.separator") +
                "Cmp005" + System.getProperty("line.separator");
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));
        CredentialsIO srv = new CredentialsIO();
        srv.GetCredentialsFromFile("testCredentials.text");
        srv.GetUserInputs();

        srv.SaveCredentials();
        State res = srv.GetCredentialsFromFile("testCredentials.text");

        assertTrue(res == State.SUCCESS);
        assertTrue(srv.GetDatabaseName().equals( "Cmp005"));
        assertTrue(srv.GetPass().equals( "pass"));
        assertTrue(srv.GetUsername().equals( "kef"));
        assertTrue(srv.GetHostname().equals( "localhost"));
    }
}
