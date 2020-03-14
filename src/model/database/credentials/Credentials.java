package model.database.credentials;

public class Credentials
{
    protected String username;
    protected String password;
    protected String hostname;
    protected String dbURL;
    protected String databaseName;

    public void SetDbUrl(String url) {dbURL = url;}

    public void SetUsername(String username) {this.username = username;}

    public void SetPassword(String password) {this.password = password;}

    public String GetUsername() { return username; }

    public String GetPassword(){ return password; }

    public String GetHostname() { return hostname; }

    public String GetDbURL() { return dbURL; }

    public String GetDatabaseName() { return databaseName; }
}
