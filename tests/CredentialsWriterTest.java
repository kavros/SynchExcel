import model.credentialsReaderWriter.Credentials;
import model.credentialsReaderWriter.CredentialsWriter;
import model.cipher.Cipher;
import model.cipher.CipherException;
import model.dbReader.DatabaseReader;
import model.parser.ExcelParser;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@PrepareForTest({ExcelParser.class, DatabaseReader.class, Cipher.class})
public class CredentialsWriterTest
{
    @Test(expected = IllegalArgumentException.class)
    public void SaveCredentials_WhenArgsAreInvalid_ShouldThrowIllegalArgumentException() throws CipherException
    {
        CredentialsWriter cw = new CredentialsWriter(null);
        cw.SaveCredentials(null,null);
    }

    @Test
    public void SaveCredentials_WhenEncryptionFail_LogsError() throws CipherException
    {
        Credentials cr = new Credentials();
        cr.SetPassword("testPass");
        Cipher ed = mock(Cipher.class);
        Exception innerEx = new Exception();
        CipherException ex = new CipherException("",innerEx);
        when(ed.encrypt(cr.GetPassword())).thenThrow(ex);
        CredentialsWriter cw = new CredentialsWriter(ed);

        cw.SaveCredentials(cr,"testFileWithCredentials.txt");
        // TODO
    }
}
