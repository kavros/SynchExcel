
import model.cipher.Cipher;
import model.cipher.CipherException;
import model.logins.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;

import static org.junit.Assert.assertTrue;


public class CredentialsReaderTest
{

    private void SimulateStdinInputs()
    {
        String simulatedUserInput = "kef" + System.getProperty("line.separator") +
                "pass" + System.getProperty("line.separator") +
                "localhost" + System.getProperty("line.separator") +
                "Cmp005" + System.getProperty("line.separator");
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));
    }

    @Test
    public void GetCredentials_WhenFileNotExist_ShouldGetCredentialsFromStdin() throws CipherException
    {
        Cipher en = new Cipher();
        CredentialsWriter cw = new CredentialsWriter(en);
        CredentialsReader srv = new CredentialsReader(cw,en);
        SimulateStdinInputs();

        srv.GetCredentials("testFiles/fileNotFound");
    }

    @Test
    public void GetCredentialsFromFile_WhenFileIsEmpty_ShouldGetCredentialsFromStdin() throws Exception
    {
        Cipher en = new Cipher();
        CredentialsWriter cw = new CredentialsWriter(en);
        CredentialsReader srv = new CredentialsReader(cw,en);
        String fileName = "testFiles/the-file-name.txt";
        PrintWriter writer = new PrintWriter(fileName, "UTF-8");
        writer.close();
        SimulateStdinInputs();

        srv.GetCredentials(fileName);
    }

    @Test
    public void GetCredentialsFromFile_WhenFileIsCorrect_ReturnsCorrectContent()throws CipherException
    {
        Cipher en = new Cipher();
        CredentialsWriter cw = new CredentialsWriter(en);
        CredentialsReader srv = new CredentialsReader(cw,en);

        Credentials res = srv.GetCredentials("testFiles/credentials.txt");

        assertTrue(res.GetUsername().equals("kef"));
        assertTrue(res.GetPassword().equals("pass"));
        assertTrue(res.GetHostname().equals("localhost"));
        assertTrue(res.GetDatabaseName().equals("Cmp005"));
    }
}
