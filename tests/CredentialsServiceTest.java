
import model.Constants;
import model.CredentialsService;
import model.State;
import org.apache.commons.math3.analysis.function.Constant;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertTrue;


public class CredentialsServiceTest
{
    @Test
    public void GetUserInputs_WhenInputsProvided_ShouldSetDbUrl()
    {
        String simulatedUserInput = "kef" + System.getProperty("line.separator") +
            "pass" + System.getProperty("line.separator") +
            "Alexis-PC" + System.getProperty("line.separator") +
            "Cmp005" + System.getProperty("line.separator");
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));
        CredentialsService srv = new CredentialsService();

        srv.GetUserInputs();

        assertTrue(srv.GetDbURL().equals(
                "jdbc:sqlserver://Alexis-PC" + ";" + "databaseName=Cmp005"
        ));
    }

    @Test(expected = NullPointerException.class)
    public void SaveCredentials_WhenFilePathNotGiven_ShouldTrowException()
    {
        CredentialsService srv = new CredentialsService();

        srv.SaveCredentials();
    }

    @Test
    public void GetCredentialsFromFile_WhenFileNotExist_ReturnFail()
    {
        CredentialsService srv = new CredentialsService();

        State res = srv.GetCredentialsFromFile("fileNotFound");

        assertTrue(res == State.FAILURE);
    }

    @Test
    public void GetCredentialsFromFile_WhenFileIsCorrect_ReturnsCorrectContent()
    {
        CredentialsService srv = new CredentialsService();

        State res = srv.GetCredentialsFromFile(Constants.credFilePath);

        assertTrue(srv.GetUsername().equals("kef"));
        assertTrue(srv.GetDatabaseName().equals("Cmp005"));
        assertTrue(res == State.SUCCESS);
    }
}
