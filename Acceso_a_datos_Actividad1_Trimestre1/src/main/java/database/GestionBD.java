package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GestionBD {

     static Connection connection;

     private static void crearConexion(){
         try {
             Class.forName("com.mysql.cj.jdbc.Driver");
             String url = String.format("jdbc:mysql://%s/%s", EsquemaBD.host , EsquemaBD.nombreBD);
             connection = DriverManager.getConnection(url,"root","");
         } catch (SQLException | ClassNotFoundException e) {
             throw new RuntimeException(e);
         }
     }

    public static Connection getConnection(){
        if (connection == null){
            crearConexion();
        }
        return connection;
    }


}
