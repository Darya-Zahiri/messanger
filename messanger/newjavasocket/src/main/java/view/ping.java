package view;

import helper.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

public class ping {
    Stage stage;
    Scene scene;
    Parent root;

    @FXML
    private Button back;
    @FXML
    private TextArea ping;
    Session session;
    Socket socket;
    DataInputStream din;
    DataOutputStream dout;
    boolean status;
    public void initialize(){
        status=true;
        stage = (Stage) Stage.getWindows().stream().findFirst().orElse(null);
        session = (Session) stage.getUserData();
        socket = session.getSocket();
        try {
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());
        }catch (Exception e){
            din=null;
            dout=null;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(status) {
                    try {
                        Thread.sleep(1000);
                        ping();
                    } catch (Exception e) {

                    }
                }
            }
        }).start();
    }

    public void setBack(ActionEvent event) throws IOException {
        status=false;
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    public void ping(){
        try {
            long send=new Date().getTime();
            dout.writeUTF("");
            din.readUTF();
            long pingtime=new Date().getTime()-send;
            ping.setText(ping.getText()+"\n"+"Reply from "+session.getSocket().getRemoteSocketAddress()+" time="+pingtime+"ms");

        }catch (Exception e){

        }

    }
}
