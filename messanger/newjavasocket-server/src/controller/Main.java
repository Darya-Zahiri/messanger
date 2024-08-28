package controller;

import java.net.*;
import java.io.*;
import java.sql.SQLException;

import helper.*;
public class Main {
    public static void main(String[] args) {
        ServerSocket server=null;
        Database database=null;
        try {
            server = new ServerSocket(5678);
            database=new Database("localhost",3306,"messenger","root","zahmaz123");
            while(true) {
                Socket socket = server.accept();
                System.out.println("new client accepted");
                HandleRequest handleRequest = new HandleRequest(socket,database);
                new Thread(handleRequest).start();
            }
        }catch (IOException e){
            System.out.println("io error: "+e);
        }catch (ClassNotFoundException e){
            System.out.println("domain error: "+e);
        }catch (SQLException e){
            System.out.println("sql error: "+e);
        }finally {
            if(server!=null){
                try{
                    server.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            if(database!=null){
                try{
                    database.closeConnection();
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }
}