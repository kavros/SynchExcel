import model.credentialsReaderWriter.CredentialsReader;
import model.credentialsReaderWriter.CredentialsWriter;
import org.junit.Test;

public class CredentialsWriterTest
{
    @Test(expected = IllegalArgumentException.class)
    public void SaveCredentials_WhenArgsAreInvalid_ShouldThrowIllegalArgumentException()
    {
        CredentialsWriter.SaveCredentials(null,null);
    }
}
