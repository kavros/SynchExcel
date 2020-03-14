import model.credentialsReaderWriter.Credentials;
import model.credentialsReaderWriter.CredentialsWriter;
import model.cipher.EncrypterDecrypter;
import model.cipher.EncrypterDecrypterException;
import model.dbReader.DatabaseReader;
import model.parser.ExcelParser;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@PrepareForTest({ExcelParser.class, DatabaseReader.class, EncrypterDecrypter.class})
public class CredentialsWriterTest
{
    @Test(expected = IllegalArgumentException.class)
    public void SaveCredentials_WhenArgsAreInvalid_ShouldThrowIllegalArgumentException() throws  EncrypterDecrypterException
    {
        CredentialsWriter cw = new CredentialsWriter(null);
        cw.SaveCredentials(null,null);
    }

    @Test
    public void SaveCredentials_WhenEncryptionFail_LogsError() throws EncrypterDecrypterException
    {
        Credentials cr = new Credentials();
        cr.SetPassword("testPass");
        EncrypterDecrypter ed = mock(EncrypterDecrypter.class);
        Exception innerEx = new Exception();
        EncrypterDecrypterException ex = new EncrypterDecrypterException("",innerEx);
        when(ed.encrypt(cr.GetPassword())).thenThrow(ex);
        CredentialsWriter cw = new CredentialsWriter(ed);

        cw.SaveCredentials(cr,"testFileWithCredentials.txt");
        // TODO
    }
}
