package helper;
import java.sql.*;
public class Database{
    private Connection connection;
    public Database(String host, int port, String dbName,String user,String password) throws ClassNotFoundException, SQLException {
            connection=null;
            String URL="jdbc:mysql://"+host+":"+port+"/"+dbName;
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection= DriverManager.getConnection(URL,user,password);
    }
    public int executeQueryWithoutResult(String query) throws SQLException {
        //delete,insert,update,create,alter,...
        Statement statement=connection.createStatement();
        int result=statement.executeUpdate(query);
        statement.close();
        return result;
    }
    public ResultSet executeQueryWithResult(String query) throws SQLException {
        //select
        Statement statement=connection.createStatement();
        ResultSet result=statement.executeQuery(query);
        return result;
    }
    public void closeConnection() throws SQLException {
        connection.close();
    }
    public Connection getConnection() {
        return connection;
    }

}
