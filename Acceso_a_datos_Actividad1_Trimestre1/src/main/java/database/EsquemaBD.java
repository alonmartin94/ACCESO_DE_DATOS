package database;

public interface EsquemaBD {

    String nombreBD= "Almacén";
    String host= "localhost:3306";

    //TABLA PRODUCTOS
    String tab_productos="productos";
    String col_productos_id="id_producto";
    String col_productos_nombre="nombre";
    String col_productos_descripcion="descripción";
    String col_productos_cantidad="cantidad";
    String col_productos_precio="precio";

    //TABLA EMPLEADOS
    String tab_empleados="empleados";
    String col_empleados_id="id";
    String col_empleados_nombre="nombre";
    String col_empleados_apellidos="apellidos";
    String col_empleados_correo="correo";

    //TABLA PEDIDOS
    String tab_pedidos="pedidos";
    String col_pedidos_id="id";
    String col_pedidos_id_producto="id_producto";
    String col_pedidos_descripcion="descripcion";
    String col_pedidos_precio_total="precio_total";

    //TABLA PRODUCTOS_FAV
    String tab_productos_fav="productos_fav";
    String col_productos_fav_id="id";
    String col_productos_fav_id_producto="id_producto";

}
