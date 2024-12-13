package tarea4;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class tarea4 {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/dbeventos";
        String user = "root";
        String password = "admin";

        try (Connection miConexion = DriverManager.getConnection(url, user, password)) {

            Scanner scanner = new Scanner(System.in);

            // Pedir el código del evento
            System.out.print("Ingrese el código del evento para consultar la cantidad de asistentes: ");
            int codigoEvento = scanner.nextInt();

            // Llamar a la función almacenada obtener_numero_asistentes
            String consultaFuncion = "{ ? = CALL obtener_numero_asistentes(?) }";
            try (CallableStatement stmtFuncion = miConexion.prepareCall(consultaFuncion)) {
                stmtFuncion.registerOutParameter(1, java.sql.Types.INTEGER);
                stmtFuncion.setInt(2, codigoEvento);

                // Ejecutar la función
                stmtFuncion.execute();

                // Obtener el número de asistentes
                int numeroAsistentes = stmtFuncion.getInt(1);
                System.out.println("El número de asistentes al evento con código " + codigoEvento + " es: " + numeroAsistentes);
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
