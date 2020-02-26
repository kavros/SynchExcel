
import model.credentialsReaderWriter.CredentialsIO;
import model.dbReader.DatabaseReader;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;

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
    public void SaveCredentials_WhenFilePathNotGiven_ShouldThrowNullPointerException() throws Exception
    {
        CredentialsIO srv = new CredentialsIO();

        srv.SaveCredentials();
    }

    @Test(expected = IOException.class)
    public void GetCredentialsFromFile_WhenFileNotExist_ShouldThrowIOException() throws Exception
    {
        CredentialsIO srv = new CredentialsIO();

        srv.GetCredentialsFromFile("fileNotFound");
    }

    @Test(expected = IllegalArgumentException.class)
    public void GetCredentialsFromFile_WhenFileIsEmpty_ShouldThrowIOException() throws Exception
    {
        CredentialsIO srv = new CredentialsIO();
        PrintWriter writer = new PrintWriter("the-file-name.txt", "UTF-8");
        writer.close();

        srv.GetCredentialsFromFile("the-file-name.txt");

    }

    @Test
    public void GetCredentialsFromFile_WhenFileIsCorrect_ReturnsCorrectContent() throws Exception
    {
        CredentialsIO srv = new CredentialsIO();

        srv.GetCredentialsFromFile(DatabaseReader.credFilePath);

        assertTrue(srv.GetUsername().equals("kef"));
        assertTrue(srv.GetDatabaseName().equals("Cmp005"));
    }

    @Test
    public void SaveCredentials_WhenValidSaveCredentials_ThenRetrieveThemCorrectly() throws Exception
    {
        String simulatedUserInput = "kef" + System.getProperty("line.separator") +
                "pass" + System.getProperty("line.separator") +
                "localhost" + System.getProperty("line.separator") +
                "Cmp005" + System.getProperty("line.separator");
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));
        CredentialsIO srv = new CredentialsIO();
        srv.GetCredentialsFromFile("testCredentials.text");
        srv.GetUserInputs();

        srv.SaveCredentials();
        srv.GetCredentialsFromFile("testCredentials.text");

        assertTrue(srv.GetDatabaseName().equals( "Cmp005"));
        assertTrue(srv.GetPass().equals( "pass"));
        assertTrue(srv.GetUsername().equals( "kef"));
        assertTrue(srv.GetHostname().equals( "localhost"));
    }
}
