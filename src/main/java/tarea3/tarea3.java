package tarea3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;
import java.util.regex.Pattern;

public class tarea3 {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/dbeventos";
        String user = "root";
        String password = "admin";

        try (Connection miConexion = DriverManager.getConnection(url, user, password);
				var scanner = new Scanner(System.in)) {

            // Pedir el DNI del asistente
            System.out.print("Ingrese el DNI del asistente (8 números y una letra): ");
            String dni = scanner.nextLine();

            // Validar el formato del DNI usando regex
            if (!Pattern.matches("\\d{8}[A-Za-z]", dni)) {
                System.out.println("El DNI no tiene un formato válido. Debe contener 8 números seguidos de una letra.");
                return;
            }

            // Mostrar lista de eventos
            String consultaEventos = "SELECT e.id_evento, e.nombre_evento, u.nombre AS ubicacion, u.capacidad " +
                                      "FROM eventos e JOIN ubicaciones u ON e.id_ubicacion = u.id_ubicacion";
            try (PreparedStatement stmtEventos = miConexion.prepareStatement(consultaEventos);
                 ResultSet rsEventos = stmtEventos.executeQuery()) {

                System.out.println("Eventos disponibles:");
                while (rsEventos.next()) {
                    System.out.println(rsEventos.getInt("id_evento") + ". " + rsEventos.getString("nombre_evento") +
                            " - " + rsEventos.getString("ubicacion") + " (Capacidad: " + rsEventos.getInt("capacidad") + ")");
                }
            }

            // Seleccionar un evento
            System.out.print("Seleccione el número del evento: ");
            int idEvento = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            // Verificar si el DNI ya está registrado
            String consultaDNI = "SELECT dni FROM asistentes WHERE dni = ?";
            boolean dniExiste;
            try (PreparedStatement stmtDNI = miConexion.prepareStatement(consultaDNI)) {
                stmtDNI.setString(1, dni);
                try (ResultSet rsDNI = stmtDNI.executeQuery()) {
                    dniExiste = rsDNI.next();
                }
            }

            // Si el DNI no existe, registrar el asistente
            if (!dniExiste) {
                System.out.print("Ingrese el nombre del asistente: ");
                String nombre = scanner.nextLine();

                String insertarAsistente = "INSERT INTO asistentes (dni, nombre) VALUES (?, ?)";
                try (PreparedStatement stmtInsertar = miConexion.prepareStatement(insertarAsistente)) {
                    stmtInsertar.setString(1, dni);
                    stmtInsertar.setString(2, nombre);
                    stmtInsertar.executeUpdate();
                    System.out.println("Asistente registrado exitosamente.");
                }
            }

            // Verificar la capacidad del evento
            String consultaCapacidad = "SELECT u.capacidad, COUNT(ae.dni) AS registrados " +
                                       "FROM eventos e JOIN ubicaciones u ON e.id_ubicacion = u.id_ubicacion " +
                                       "LEFT JOIN asistentes_eventos ae ON e.id_evento = ae.id_evento " +
                                       "WHERE e.id_evento = ? GROUP BY u.capacidad";
            int capacidadMaxima = 0;
            int registrados = 0;
            try (PreparedStatement stmtCapacidad = miConexion.prepareStatement(consultaCapacidad)) {
                stmtCapacidad.setInt(1, idEvento);
                try (ResultSet rsCapacidad = stmtCapacidad.executeQuery()) {
                    if (rsCapacidad.next()) {
                        capacidadMaxima = rsCapacidad.getInt("capacidad");
                        registrados = rsCapacidad.getInt("registrados");
                    }
                }
            }

            if (registrados >= capacidadMaxima) {
                System.out.println("El evento ya alcanzó su capacidad máxima. No se puede registrar al asistente.");
                return;
            }

            // Registrar el asistente al evento
            String registrarEvento = "INSERT INTO asistentes_eventos (dni, id_evento) VALUES (?, ?)";
            try (PreparedStatement stmtRegistrar = miConexion.prepareStatement(registrarEvento)) {
                stmtRegistrar.setString(1, dni);
                stmtRegistrar.setInt(2, idEvento);
                stmtRegistrar.executeUpdate();
                System.out.println("Asistente registrado al evento exitosamente.");
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}