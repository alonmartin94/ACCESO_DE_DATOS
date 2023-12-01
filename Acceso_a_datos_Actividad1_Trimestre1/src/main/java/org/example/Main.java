package org.example;

import database.EsquemaBD;
import database.GestionBD;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        //CONEXION BASE DE DATOS
        Connection connection = GestionBD.getConnection();

        Scanner scanner = new Scanner(System.in);
        int selec=0;

        while(selec!=7) {
            System.out.println("---------------MENÚ-------------");
            System.out.println("1. Agregar los productos del JSON");
            System.out.println("2. Agragar empleados");
            System.out.println("3. Agragar pedidos");
            System.out.println("4. mostrar Empleados");
            System.out.println("5. mostrar pedidos");
            System.out.println("6. mostrar productos");
            System.out.println("7. Salir");
            System.out.println("Introduce el numero de la opcion que desea");

            selec = scanner.nextInt();
            switch(selec) {
                case 1:
                    rellenarBD(connection);
                    break;
                case 2:
                    agregarEmpleados(connection);
                    break;
                case 3:
                    agregarPedidios(connection);
                    break;
                case 4:
                    mostrarEmpleados(connection);
                    break;
                case 5:
                    mostrarPedidos(connection);
                    break;
                case 6:
                    mostrarProductos(connection);
                    break;
                case 7:
                    System.out.println("FIN DEL PROGRAMA");
                    break;
                default:
                    System.out.println("seleccion errónea");
                    break;

            }
        }

    }
    public static void rellenarBD(Connection connection){
        //OBTENER EL JSON DE UNA API EXTERNA
        try {
            URL url = new URL("https://dummyjson.com/products");
            HttpURLConnection connectionJSON = (HttpURLConnection) url.openConnection();
            BufferedReader reader =new BufferedReader(new InputStreamReader(connectionJSON.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            String linea= null;
            //asegura que lee todos los saltos de linea
            while ((linea = reader.readLine())!=null){
                stringBuffer.append(linea);
            }
            //pasamos de StringBuffer a JSON
            JSONObject objeto = new JSONObject(stringBuffer.toString());
            JSONArray productos = objeto.getJSONArray("products");
            for(int i = 0 ; i < productos.length(); i++){
                JSONObject producto = productos.getJSONObject(i);
                int id = producto.getInt("id");
                String nombre = producto.getString("title");
                String descripcion = producto.getString("description");
                int cantidad = producto.getInt("stock");
                int precio = producto.getInt("price");
                PreparedStatement preparedStatement = connection.prepareStatement(String.format("INSERT INTO %s (%s,%s,%s,%s,%s) VALUE (?,?,?,?,?)",
                        EsquemaBD.tab_productos,
                        EsquemaBD.col_productos_id,
                        EsquemaBD.col_productos_nombre,
                        EsquemaBD.col_productos_descripcion,
                        EsquemaBD.col_productos_cantidad,
                        EsquemaBD.col_productos_precio));
                preparedStatement.setInt(1, id);
                preparedStatement.setString(2, nombre);
                preparedStatement.setString(3, descripcion);
                preparedStatement.setInt(4, cantidad);
                preparedStatement.setInt(5, precio);
                preparedStatement.execute();
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void agregarEmpleados(Connection connection){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduce el nombre: ");
        String nombre = scanner.nextLine();
        System.out.println("Introduce los apellidos: ");
        String apellidos = scanner.nextLine();
        System.out.println("Introduce el correo: ");
        String correo = scanner.nextLine();


        try {
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement(String.format("INSERT INTO %s (%s,%s,%s,%s) VALUE (?,?,?,?)",
                    EsquemaBD.tab_empleados,
                    EsquemaBD.col_empleados_id,
                    EsquemaBD.col_empleados_nombre,
                    EsquemaBD.col_empleados_apellidos,
                    EsquemaBD.col_empleados_correo));
            int id=0;
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, nombre);
            preparedStatement.setString(3, apellidos);
            preparedStatement.setString(4, correo);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void agregarPedidios(Connection connection){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduce el id del producto que quieres: ");
        int id = scanner.nextInt();
        System.out.println("Introduce la cantidad que quieres: ");
        int cantidad = scanner.nextInt();


        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("SELECT (%s*%s) as precioTotal FROM %s WHERE id_producto = %s",
                    cantidad,
                    EsquemaBD.col_productos_precio,
                    EsquemaBD.tab_productos,
                    id));
            resultSet.next();
            int precioTotal = resultSet.getInt("precioTotal");
            String descripcion = "Id del producto " + id + " X " + " Cantidad: " + cantidad;
            statement.execute(String.format("INSERT INTO %s (%s,%s,%s) VALUE (%s,'%s',%s)",
                    EsquemaBD.tab_pedidos,
                    EsquemaBD.col_pedidos_id_producto,
                    EsquemaBD.col_pedidos_descripcion,
                    EsquemaBD.col_pedidos_precio_total,
                    id,
                    descripcion,
                    precioTotal));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    public static void mostrarEmpleados(Connection connection){
        try {
            StringBuilder empleados = new StringBuilder();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + EsquemaBD.tab_empleados);
            while(resultSet.next()){
                int id = resultSet.getInt(EsquemaBD.col_empleados_id);
                String nombre = resultSet.getString(EsquemaBD.col_empleados_nombre);
                String apellidos = resultSet.getString(EsquemaBD.col_empleados_apellidos);
                String correo = resultSet.getString(EsquemaBD.col_empleados_correo);
                empleados.append(String.format("Id: %d || Nombre: %s || Apellidos: %s || Correo: %s\n",id,nombre,apellidos,correo));
            }
            System.out.println(empleados);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void mostrarPedidos(Connection connection){
        try {
            StringBuilder pedidos = new StringBuilder();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + EsquemaBD.tab_pedidos);
            while(resultSet.next()){
                int idPedido = resultSet.getInt(EsquemaBD.col_pedidos_id);
                String idProducto = resultSet.getString(EsquemaBD.col_pedidos_id_producto);
                String descripcion = resultSet.getString(EsquemaBD.col_pedidos_descripcion);
                String precioTotal = resultSet.getString(EsquemaBD.col_pedidos_precio_total);
                pedidos.append(String.format("Id: %d || idProducto: %s || descripcion: %s || precioTotal: %s\n",idPedido,idProducto, descripcion, precioTotal));
            }
            System.out.println(pedidos);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void mostrarProductos(Connection connection){
        try {
            StringBuilder productos = new StringBuilder();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + EsquemaBD.tab_productos);
            while(resultSet.next()){
                int id = resultSet.getInt(EsquemaBD.col_productos_id);
                String nombre = resultSet.getString(EsquemaBD.col_productos_nombre);
                String descripcion = resultSet.getString(EsquemaBD.col_productos_descripcion);
                String precio = resultSet.getString(EsquemaBD.col_productos_precio);
                productos.append(String.format("Id: %d || idProducto: %s || descripcion: %s || precio: %s\n",id,nombre,descripcion,precio
                ));
            }
            System.out.println(productos);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
