package tarea;

import java.sql.*;

public class Tarea {

    public static void main(String[] args) {

        // Propiedades de conexión
        String url = "jdbc:mysql://localhost:3306/dbeventos"; // Cambiar si el nombre de la base de datos es diferente
        String user = "root"; // Cambiar según tu usuario de MySQL
        String password = "admin"; // Cambiar según tu contraseña de MySQL



        // Consulta SQL para obtener los datos de los eventos
        String query = "SELECT " +
                       "e.nombre_evento AS Evento, " +
                       "COUNT(ae.dni) AS Asistentes, " +
                       "u.nombre AS Ubicacion, " +
                       "u.direccion AS Direccion " +
                       "FROM eventos e " +
                       "LEFT JOIN ubicaciones u ON e.id_ubicacion = u.id_ubicacion " +
                       "LEFT JOIN asistentes_eventos ae ON e.id_evento = ae.id_evento " +
                       "GROUP BY e.id_evento, e.nombre_evento, u.nombre, u.direccion " +
                       "ORDER BY e.fecha;";

        // Conexión a la base de datos y ejecución de la consulta
        try (
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)
        ) {
            // Encabezado de la tabla
            System.out.println("Listado de eventos:");
            System.out.println("--------------------------------------------------------");
            System.out.printf("%-30s %-15s %-25s %-25s%n", 
                              "Evento", "Asistentes", "Ubicación", "Dirección");
            System.out.println("--------------------------------------------------------");

            // Iterar sobre los resultados y mostrarlos
            while (rs.next()) {
                String evento = rs.getString("Evento");
                int asistentes = rs.getInt("Asistentes");
                String ubicacion = rs.getString("Ubicacion");
                String direccion = rs.getString("Direccion");

                System.out.printf("%-30s %-15d %-25s %-25s%n", 
                                  evento, asistentes, ubicacion, direccion);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    }


