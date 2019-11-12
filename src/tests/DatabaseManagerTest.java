package tests;

import model.Constants;
import model.DatabaseManager;
import org.junit.Test;

import static org.junit.Assert.*;

public class DatabaseManagerTest
{
    @Test
    public void shouldGetCredentials()
    {
        DatabaseManager dbManager = new DatabaseManager(Constants.credFilePath);

    }
}