package controller;

import helper.Database;
import model.Message;
import model.Session;
import model.User;

import java.io.*;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class HandleRequest implements Runnable{
    private final Socket socket;
    private final Database database;
    private User user;
    private Session session;
    boolean sessionStatus;
    private DataInputStream din;
    private DataOutputStream dout;
    private InputStream in;
    private OutputStream out;
    ObjectInputStream objin;
    ObjectOutputStream objout;
    public HandleRequest(Socket socket, Database database){
        this.socket = socket;
        this.database=database;
        this.user=null;
    }
    @Override
    public void run() {
        try{
            in =socket.getInputStream();
            out=socket.getOutputStream();
            din = new DataInputStream(in);
            sessionStatus=true;
            String command = din.readUTF();
            while(!command.equals("exit")&& sessionStatus){
                switch (command){
                    case "signup":
                        signUp(out,in);
                        break;
                    case "signin":
                        signIn(out,in);
                        break;
                   case "sendmessage":
                       if(user.getAccessed_at()+1800000>new Date().getTime()) {
                           sessionStatus=true;
                       }else{
                           sessionStatus=false;
                       }
                       sendMessage(out, in,sessionStatus);
                        break;
                    case "getallmessage":
                        if(user.getAccessed_at()+1800000>new Date().getTime()) {
                            sessionStatus=true;
                        }else{
                            sessionStatus=false;
                        }
                        getAllMessage(out,in,sessionStatus);
                        break;
                    case "deletemessage":
                        if(user.getAccessed_at()+1800000>new Date().getTime()) {
                            sessionStatus=true;
                        }else{
                            sessionStatus=false;
                        }
                        deleteMessage(out,in,sessionStatus);
                        break;
                    case "":
                        if(user.getAccessed_at()+1800000>new Date().getTime()) {
                            sessionStatus=true;
                        }else{
                            sessionStatus=false;
                        }
                        ping(out);
                        break;
                }
                if(sessionStatus)
                    command = din.readUTF();
            }
            if(sessionStatus)
                database.executeQueryWithoutResult("update session set end="+new Date().getTime()+" where id="+session.getId()+";");
            else
                database.executeQueryWithoutResult("update session set end="+new Date().getTime()+" , status=1 where id="+session.getId()+";");
            in.close();
            out.close();
            socket.close();
        }catch (IOException e){
            System.out.println(e.toString());
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }
    public void ping(OutputStream out){
        try {
            dout = new DataOutputStream(out);
            dout.writeUTF("");
            dout.flush();
        }catch (Exception e){

        }
    }
    public void signUp(OutputStream out,InputStream in) throws IOException {
        int result=-1;
        try{
            dout=new DataOutputStream(out);
            dout.writeUTF("ok");
            objin=new ObjectInputStream(in);
            User user=(User)objin.readObject();
            user.setCreated_at(new Date().getTime());
            user.setAccessed_at(new Date().getTime());
            user.setUpdated_at(new Date().getTime());
            ResultSet resultSet= database.executeQueryWithResult("select id from user where username='"+user.getUsername()+"';");
            if(!resultSet.next()) {
                result = database.executeQueryWithoutResult("insert into user (name,username,password,created_at,updated_at,accessed_at) values('" + user.getName() + "','" + user.getUsername() + "','"+ user.getPassword()+ "'," + user.getCreated_at() + "," + user.getUpdated_at() + "," + user.getAccessed_at() + ");");
                if (result == 1) {
                    this.user = user;
                    dout.writeUTF("1");
                } else {
                    dout.writeUTF("2");
                }
            }else{
                dout.writeUTF("3");
            }
        }catch (SQLException e){
            dout.writeUTF("sql error"+e);
        }catch (IOException e){
            dout.writeUTF("io error"+e);
        }catch (Exception e){
            dout.writeUTF(e.toString());
        }
        dout.flush();
    }

    public void signIn(OutputStream out,InputStream in) throws IOException {
        User user=null;
        try{
            dout=new DataOutputStream(out);
            dout.writeUTF("ok");
            objin=new ObjectInputStream(in);
            user=(User)objin.readObject();
            ResultSet resultSet= database.executeQueryWithResult("select * from user where username='"+user.getUsername()+"' and password='"+user.getPassword()+"';");
            if(resultSet.next()) {
                long accessed_at=new Date().getTime();
                database.executeQueryWithoutResult("update user set accessed_at="+accessed_at+" where username='"+user.getUsername()+"';");
                user.setName(resultSet.getString("name"));
                user.setCreated_at(resultSet.getLong("created_at"));
                user.setUpdated_at(resultSet.getLong("updated_at"));
                user.setAccessed_at(accessed_at);
                user.setId(resultSet.getInt("id"));
                database.executeQueryWithoutResult("update session set end="+accessed_at+" ,status=1 where user_id="+user.getId()+" and end=0;");
                this.user=user;
                Session session=new Session(user,accessed_at);
                database.executeQueryWithoutResult("insert into session (user_id,start) values("+session.getUser().getId()+","+session.getStart()+");");
                ResultSet rs=database.executeQueryWithResult("select id from session where user_id="+session.getUser().getId()+" and start="+accessed_at+";");
                rs.next();
                session.setId(rs.getInt("id"));
                this.session=session;
            }
        }catch (SQLException e){
            user.setName(e.toString());
        }catch (IOException e){
            user.setName(e.toString());
        }catch (Exception e){
            user.setName(e.toString());
        }
        objout=new ObjectOutputStream(out);
        objout.writeObject(user);
        dout.flush();
        objout.flush();
    }
    public void sendMessage(OutputStream out,InputStream in,boolean sessionStatus) throws IOException {
        try{
            dout=new DataOutputStream(out);
            if(sessionStatus) {
                dout.writeUTF("ok");
                objin = new ObjectInputStream(in);
                Message msg = (Message) objin.readObject();
                long accessed_at = new Date().getTime();
                database.executeQueryWithoutResult("update user set accessed_at=" + accessed_at + " where username='" + user.getUsername() + "';");
                user.setAccessed_at(accessed_at);
                ResultSet resultSet=database.executeQueryWithResult("select id from user where username='"+msg.getReciver()+"';");
                resultSet.next();
                database.executeQueryWithoutResult("insert into message (sender,reciver,time,message) values(" + msg.getSender() + "," + resultSet.getInt(1) + "," + new Date().getTime() + ",'" + msg.getMessage() + "');");
                dout.writeUTF("ok");
            }else{
                dout.writeUTF("terminate");
            }
        }catch (SQLException e){
            dout.writeUTF(e.toString());
        }catch (IOException e){
            dout.writeUTF(e.toString());
        }catch (Exception e){
            dout.writeUTF(e.toString());
        }
        dout.flush();
    }
    public void deleteMessage(OutputStream out,InputStream in,boolean sessionStatus) throws IOException {
        try{
            dout=new DataOutputStream(out);
            din=new DataInputStream(in);
            if(sessionStatus) {
                dout.writeUTF("ok");
                String user1=din.readUTF();
                String user2=din.readUTF();
                long accessed_at = new Date().getTime();
                database.executeQueryWithoutResult("update user set accessed_at=" + accessed_at + " where username='" + user.getUsername() + "';");
                user.setAccessed_at(accessed_at);
                ResultSet resultSet=database.executeQueryWithResult("select id from user where username='"+user1+"';");
                resultSet.next();
                int id1=resultSet.getInt(1);
                resultSet=database.executeQueryWithResult("select id from user where username='"+user2+"';");
                resultSet.next();
                int id2=resultSet.getInt(1);
                database.executeQueryWithoutResult("delete from message where (sender="+id1+" and reciver="+id2+") or (sender="+id2+" and reciver="+id1+");");

            }else{
                dout.writeUTF("terminate");
            }
        }catch (SQLException e){
            dout.writeUTF(e.toString());
        }catch (IOException e){
            dout.writeUTF(e.toString());
        }catch (Exception e){
            dout.writeUTF(e.toString());
        }
        dout.flush();
    }
    public void getAllMessage(OutputStream out,InputStream in,boolean sessionStatus) throws IOException {
        try{
            dout=new DataOutputStream(out);
            din=new DataInputStream(in);
           if(sessionStatus){
                dout.writeUTF("ok");
                String reciver=din.readUTF();
                String result1="";
                String result2="";
               long accessed_at = new Date().getTime();
               database.executeQueryWithoutResult("update user set accessed_at=" + accessed_at + " where username='" + user.getUsername() + "';");
                ResultSet resultSet=database.executeQueryWithResult("select id from user where username='"+reciver+"';");
                resultSet.next();
                int id=resultSet.getInt(1);
                if(id==-1)
                    resultSet=database.executeQueryWithResult("select user.username,message.message,message.time from user,message where message.reciver=-1 and user.id=message.sender order by time desc;");
                else
                    resultSet=database.executeQueryWithResult("select user.username,message.message,message.time from user,message where ((message.reciver="+id+" and message.sender="+user.getId()+") or (message.reciver="+user.getId()+" and message.sender="+id+")) and user.id=message.sender order by time desc;");
                while(resultSet.next()){
                    String username=resultSet.getString(1);
                    String message=resultSet.getString(2);
                    long time=resultSet.getLong(3);
                    Date d=new Date(time);
                    result1=result1+"\n"+username+ ":\n"+message+"\n"+d.toString()+"\n";
                }
               resultSet=database.executeQueryWithResult("select user.username from user,session where session.end=0 and user.username!='"+user.getUsername()+"'  and user.id=session.user_id;");
               while(resultSet.next()){
                   String username=resultSet.getString(1);
                   result2=result2+username+"\n";
               }
                dout.writeUTF(result1);
                dout.flush();
                dout.writeUTF(result2);
            }else{
                dout.writeUTF("terminate");
            }
        }catch (SQLException e){
            dout.writeUTF(e.toString());
        }catch (IOException e){
            dout.writeUTF(e.toString());
        }catch (Exception e){
            dout.writeUTF(e.toString());
        }
        dout.flush();
    }
}
