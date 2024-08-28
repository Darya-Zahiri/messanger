package helper;

import model.User;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
public class Session {
    private Socket socket;
    public UnknownHostException hostexception;
    public IOException ioexception;
    public User currentUser;
    public Session(){
        try {
            InetAddress ip = InetAddress.getByName("localhost");
            socket = new Socket(ip , 5678);
        }catch (UnknownHostException e){
            hostexception=e;
            socket=null;
        }catch (IOException e){
            ioexception=e;
            socket=null;
        }
        currentUser=null;
    }
    public void closeSocket(){
        try {
            socket.close();
        }catch (Exception e){

        }
        socket=null;
        currentUser=null;
    }

    public Socket getSocket() {
        return socket;
    }
}
