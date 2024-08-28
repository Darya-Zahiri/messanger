package view;

import controller.HelloApplication;
import helper.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.User;

import java.awt.*;
import java.io.*;
import java.net.Socket;

public class SignUp {
    Stage stage;
    Scene scene;
    Parent root;
    @FXML
    private TextField name;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password1;
    @FXML
    private PasswordField password2;
    @FXML
    private Button signUp;
    @FXML
    private Button back;
    @FXML
    private Label error;

    Socket socket;
    OutputStream out=null;
    ObjectOutputStream objout=null;
    DataInputStream din;
    DataOutputStream dout;

    public void initialize(){


    }
    public void setSignUp(ActionEvent event) throws IOException {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Session session=null;
        if((Session)stage.getUserData()==null){
            session=new Session();
            stage.setUserData(session);
        }else{
            session=(Session)stage.getUserData();
        }
        socket = session.getSocket();
        din = new DataInputStream(socket.getInputStream());
        dout = new DataOutputStream(socket.getOutputStream());
        error.setText("");
        error.setTextFill(Color.RED);
        if (validateInput(name.getText(),username.getText(),password1.getText(),password2.getText())){
            try {
                User user=new User(name.getText().trim(),username.getText().trim(),password1.getText().trim(),0);
                dout.writeUTF("signup");
                String msg=din.readUTF();
                if(msg.equals("ok")){
                    out=socket.getOutputStream();
                    objout=new ObjectOutputStream(out);
                    objout.writeObject(user);
                    if (out != null)
                        out.flush();
                    msg = din.readUTF();
                    if (msg.equals("1")) {
                        msg = "user added succefully.";
                    } else if (msg.equals("2")) {
                        msg = "user added abort.";
                    } else if (msg.equals("3")) {
                        msg = "username already exist.";
                    }
                    error.setText(msg);
                    dout.flush();
                    objout.flush();
                }
            }catch (IOException e) {
                error.setText(e.toString());
            }catch (Exception e){
                error.setText("unknown error"+e);
            }
        }
    }
    public boolean validateInput(String name,String username,String first,String second){
        if (socket == null){
            error.setText("server not available");
            return false;
        }
        if (name.isEmpty()){
            this.name.requestFocus();
            error.setText("name is empty");
            return false;
        }

        if (username.isEmpty()){
            this.username.requestFocus();
            error.setText("username is empty");
            return false;
        }
        if (first.isEmpty()){
            this.password1.requestFocus();
            error.setText("password is empty");
            return false;
        }
        if (first.length()<6){
            this.password1.requestFocus();
            error.setText("password should be longer");
            return false;
        }
        if (!first.equals(second)){
            this.password2.requestFocus();
            error.setText("passwords are not equal");
            return false;
        }
        return true;
    }
    public void setBack(ActionEvent event) throws IOException {
        //Session.getInstance().closeSocket();
        root = FXMLLoader.load(getClass().getResource("signIn.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void showChatroom(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("chatroom.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
