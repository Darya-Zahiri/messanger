package view;

import controller.HelloApplication;
import helper.Session;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

import helper.*;
import javafx.stage.Window;
import model.Message;

public class Chatroom  {
    Stage stage;
    Scene scene;
    Parent root;
    @FXML
    private ListView listView;
    @FXML
    private TextArea chats;
    @FXML
    private TextField text;
    @FXML
    private Button send;
    @FXML
    private Button exit;
    @FXML
    private Label name;
    @FXML
    private Label chatWith;
    @FXML
    private Hyperlink delete;
    @FXML
    private Button ping;
    String reciver;
    Session session;
    Socket socket;
    DataInputStream din;
    DataOutputStream dout;
    ObjectOutputStream objout;
    boolean sessionstatus;

    public void initialize() {
        reciver="group";
        chatWith.setText("In chat with group : ");
        sessionstatus = true;
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
        name.setText("current user:"+session.currentUser.getUsername());
        new Thread(new Runnable() {
            @Override
            public void run() { while (sessionstatus) {
                try {
                    Thread.sleep(1000);
                }catch (Exception e){

                }
                getAllMessages();
            }
                exit();
            }
        }).start();

    }

    public void setExit(ActionEvent event) {
        exit();
    }

    public void send(ActionEvent event) throws IOException {
        if (!text.getText().trim().equals("")) {
            try {
                dout = new DataOutputStream(socket.getOutputStream());
                din = new DataInputStream(socket.getInputStream());
                dout.writeUTF("sendmessage");
                if (din.readUTF().equals("ok")) {
                    sessionstatus = true;
                    Message msg = new Message(session.currentUser.getId(), reciver, 0, text.getText());
                    objout = new ObjectOutputStream(socket.getOutputStream());
                    objout.writeObject(msg);
                    String err = din.readUTF();
                    if (err.equals("ok")) {
                        chats.setText(chats.getText() + "\n" + text.getText());
                        text.setText("");
                        text.requestFocus();
                    } else {
                        text.setText(err);
                    }
                    objout.flush();
                } else {
                    sessionstatus = false;
                    setExit(event);
                }
                dout.flush();
            } catch (Exception e) {
                sessionstatus = false;
                setExit(event);
            }
        }

    }
    public void getAllMessages()  {
            try {
                if(din==null) {
                    dout = new DataOutputStream(socket.getOutputStream());
                    din = new DataInputStream(socket.getInputStream());
                }
                dout.writeUTF("getallmessage");
                dout.flush();
                String s=din.readUTF();
                if (s.equals("ok")) {
                    dout.writeUTF(reciver);
                    sessionstatus = true;
                    String result = din.readUTF();
                    chats.setText(result);
                    s=din.readUTF();
                    getOnlineUsers(s);
                } else {
                    System.out.println(s);
                    sessionstatus = false;
                }
                dout.flush();
            } catch (Exception e) {
                System.out.println("client:"+e.toString());
                sessionstatus = false;
            }

    }
    public void exit(){
        try {
            if (sessionstatus) {
                dout = new DataOutputStream(socket.getOutputStream());
                dout.writeUTF("exit");
            }
            session.closeSocket();
            stage.setUserData(null);
            root = FXMLLoader.load(getClass().getResource("signIn.fxml"));
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("client2:"+e.toString());
        }
    }
    public void getOnlineUsers(String input){
        int i=1;
        String[] arrOfInput=input.split("\n");
        listView.getItems().clear();
        Hyperlink hyperlink=new Hyperlink("Group");
        Hyperlink finalHyperlink = hyperlink;
        hyperlink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                chatWith.setText("In chat with group : ");
                reciver="group";
                delete.setVisible(false);
            }
        });
        listView.getItems().add(0,hyperlink);
        for (String a : arrOfInput){
            hyperlink=new Hyperlink(a);
            hyperlink.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    chatWith.setText("In chat with "+a+" : ");
                    delete.setVisible(true);
                    reciver=a;
                }
            });
            listView.getItems().add(i,hyperlink);
            i++;

        }

    }
    public void setDelete(ActionEvent event){
        try {
            dout = new DataOutputStream(socket.getOutputStream());
            din = new DataInputStream(socket.getInputStream());
            dout.writeUTF("deletemessage");
            if (din.readUTF().equals("ok")) {
                sessionstatus = true;
                dout.writeUTF(session.currentUser.getUsername());
                dout.flush();
                dout.writeUTF(reciver);
            } else {
                sessionstatus = false;
                setExit(event);
            }
            dout.flush();
        } catch (Exception e) {
            sessionstatus = false;
            setExit(event);
        }
    }
    public void setPing(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("ping.fxml"));
        Stage stage=new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.setUserData(this.stage.getUserData());
        stage.show();
    }

}
