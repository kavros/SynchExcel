package main;
import java.io.Console;
import java.util.Scanner;

import model.ConnectionManager;
import model.TrippleDes;

public class Main
{



    public static void main(String[] args)
    {


        try
        {

            ConnectionManager conn=new ConnectionManager( "./data/credentials.txt");



        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }









    }
}
