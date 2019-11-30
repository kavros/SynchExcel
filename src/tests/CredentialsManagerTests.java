package tests;

import org.junit.Test;
import model.CredentialsManager;
import model.Constants;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;


public class CredentialsManagerTests
{
    @Test
    public void CredentialsManager_WhenFileExistsAndCredentialsAreCorrect_ShouldReturnCredentials()
    {
        /*CredentialsManager credentialsManager =
                new CredentialsManager(Constants.credFilePath);
        String expectedURL = "jdbc:sqlserver://" +
                                credentialsManager.GetHostname() +
                                ";"+
                                "databaseName="+
                                credentialsManager.GetDatabaseName();

        assertTrue( credentialsManager.GetDbURL().equals(expectedURL) );*/
    }

    @Test(expected = IllegalArgumentException.class)
    public void CredentialsManager_WhenFileNotFound_ShouldTrowException()
    {
        /*String simulatedUserInput = "kef" + System.getProperty("line.separator") +
                                 "pass" + System.getProperty("line.separator") +
                                 "Alexis-PC" + System.getProperty("line.separator") +
                                 "Cmp005" + System.getProperty("line.separator");

        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        CredentialsManager credentialsManager =
                new CredentialsManager("fileNotFound");*/



    }

    public void CredentialsManager_WhenCredentialsAreIncorect_ShouldAskUser()
    {

    }

}
