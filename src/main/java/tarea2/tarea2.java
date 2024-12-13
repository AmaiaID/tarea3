package tarea2;

import java.sql.*;
import java.util.Scanner;

public class tarea2 {

    public static void main(String[] args) {
        // Configuración de conexión
        String url = "jdbc:mysql://localhost:3306/dbeventos"; //
        String user = "root"; //
        String password = "admin"; //

        Scanner scanner = new Scanner(System.in);

        try (
            Connection conn = DriverManager.getConnection(url, user, password);
        ) {
            // Pedir al usuario el nombre de la ubicación
            System.out.print("Ingrese el nombre de la ubicación: ");
            String nombreUbicacion = scanner.nextLine();

            // Verificar si la ubicación existe
            String queryCheck = "SELECT id_ubicacion, capacidad FROM ubicaciones WHERE nombre = ?";
            try (PreparedStatement pstmtCheck = conn.prepareStatement(queryCheck)) {
                pstmtCheck.setString(1, nombreUbicacion);
                ResultSet rs = pstmtCheck.executeQuery();

                if (rs.next()) {
                    // Obtener la capacidad actual
                    int idUbicacion = rs.getInt("id_ubicacion");
                    int capacidadActual = rs.getInt("capacidad");

                    System.out.println("La capacidad actual de la ubicación '" + nombreUbicacion + "' es: " + capacidadActual);

                    // Pedir la nueva capacidad al usuario
                    System.out.print("Ingrese la nueva capacidad máxima: ");
                    int nuevaCapacidad = scanner.nextInt();

                    // Actualizar la capacidad en la base de datos
                    String queryUpdate = "UPDATE ubicaciones SET capacidad = ? WHERE id_ubicacion = ?";
                    try (PreparedStatement pstmtUpdate = conn.prepareStatement(queryUpdate)) {
                        pstmtUpdate.setInt(1, nuevaCapacidad);
                        pstmtUpdate.setInt(2, idUbicacion);
                        int filasActualizadas = pstmtUpdate.executeUpdate();

                        if (filasActualizadas > 0) {
                            System.out.println("Capacidad actualizada exitosamente.");
                        } else {
                            System.out.println("Error al actualizar la capacidad.");
                        }
                    }
                } else {
                    System.out.println("La ubicación '" + nombreUbicacion + "' no existe en la base de datos.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}