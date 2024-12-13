package DatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:h2:file:C:/Users/Pc/Documents/dbeventos";  // Ruta de la base de datos H2
    private static final String USER = "root"; // Usuario predeterminado de H2
    private static final String PASSWORD = "admin"; // Contraseña predeterminada de H2

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);  // Establece la conexión
    }
}