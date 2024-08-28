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
import javafx.stage.Window;
import model.User;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.List;

public class SignIn {
    Stage stage;
    Scene scene;
    Parent root;
    @FXML
    private Button signIn;
    @FXML
    private Button signUp;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label error;
    Socket socket;
    OutputStream out;
    InputStream in;
    ObjectOutputStream objout;
    ObjectInputStream objin;
    DataInputStream din;
    DataOutputStream dout;

    public void initialize(){
        /*try {
            socket = Session.getInstance().getSocket();
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());
        }catch (IOException e){
            socket=null;
            din=null;
            dout=null;
        }catch (Exception e){
            socket=null;
            din=null;
            dout=null;
        }*/
    }
    public void setSignUp(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("signUp.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }
    public void setSignIn(ActionEvent event) throws IOException {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Session session=null;
        if((Session)stage.getUserData()==null){
            session=new Session();
            stage.setUserData(session);
        }else{
            session=(Session)stage.getUserData();
        }
        socket = session.getSocket();
        try {
        din = new DataInputStream(socket.getInputStream());
        dout = new DataOutputStream(socket.getOutputStream());
        error.setText("");
        error.setTextFill(Color.RED);
        if (validateInput(username.getText(),password.getText())){

                User user=new User("",username.getText().trim(),password.getText().trim(),0);
                dout.writeUTF("signin");
                String msg=din.readUTF();
                if(msg.equals("ok")){
                    out=socket.getOutputStream();
                    objout=new ObjectOutputStream(out);
                    objout.writeObject(user);
                    in=socket.getInputStream();
                    objin=new ObjectInputStream(in);
                    user=(User)objin.readObject();
                    if (user.getId()!=-1) {
                        session.currentUser=user;
                        showChatroom(event);
                    } else {
                        error.setText("user not found!!!");
                    }

                    dout.flush();

                    objout.flush();
                }

        }
        }catch (IOException e) {
            error.setText(e.toString());
        }catch (Exception e){
            error.setText("client unknown error:"+e);
        }
    }
    public boolean validateInput(String username,String password){
        if (socket == null){
            error.setText("server not available");
            return false;
        }
        if (username.isEmpty()){
            this.username.requestFocus();
            error.setText("username is empty");
            return false;
        }
        if (password.isEmpty()){
            this.password.requestFocus();
            error.setText("password is empty");
            return false;
        }
        if (password.length()<6){
            this.password.requestFocus();
            error.setText("password should be longer");
            return false;
        }
        return true;
    }

    public void showChatroom(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("chatroom.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
