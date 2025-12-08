package comoproyect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase singleton para gestionar la conexión a la base de datos MySQL
 */
public class Conexion {
    
    private static Conexion instancia;
    private Connection conexion;
    
    // Configuración de la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/tienda_db?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String PASSWORD = ""; // Cambia esto si tienes contraseña
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    
    /**
     * Constructor privado para patrón Singleton
     */
    private Conexion() {
        try {
            // Cargar el driver de MySQL
            Class.forName(DRIVER);
            System.out.println("✅ Driver MySQL cargado correctamente");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Error al cargar el driver de MySQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Obtiene la instancia única de la clase
     */
    public static Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }
    
    /**
     * Obtiene una conexión activa a la base de datos
     */
    public Connection getConnection() {
        try {
            // Si la conexión no existe o está cerrada, crear una nueva
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
                System.out.println("✅ Conexión a base de datos establecida");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar con la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
        return conexion;
    }
    
    /**
     * Cierra la conexión a la base de datos
     */
    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("✅ Conexión cerrada correctamente");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al cerrar la conexión: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Verifica si la conexión está activa
     */
    public boolean isConectado() {
        try {
            return conexion != null && !conexion.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
