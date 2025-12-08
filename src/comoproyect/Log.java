package comoproyect;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.*;
import java.security.MessageDigest;
import java.util.Base64;

public class Log {

    public JFrame frmLogin;
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    Reg r;
    Bar b = new Bar(); 
    String nombreUsuario = "";

    // 🎨 COLORES
    private final Color SIDEBAR_COLOR = new Color(30, 41, 59);
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private final Color ACCENT_COLOR = new Color(251, 146, 60);
    private final Color BG_COLOR = new Color(30, 41, 59);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color TEXT_DARK = new Color(30, 41, 59);
    private final Color TEXT_LIGHT = new Color(148, 163, 184);

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                verificarBaseDeDatos();
                Log window = new Log();
                window.frmLogin.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // 📌 CREA LA BASE DE DATOS Y TABLAS AUTOMÁTICAMENTE
    private static void verificarBaseDeDatos() {
        String urlServer = "jdbc:mysql://localhost:3306/";
        String dbName = "tienda_db";

        try (Connection con = DriverManager.getConnection(urlServer, "root", "");
             Statement stmt = con.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            stmt.executeUpdate("USE " + dbName);

            // TABLA USUARIOS
            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS usuarios (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    usuario VARCHAR(50) NOT NULL UNIQUE,
                    password VARCHAR(255) NOT NULL,
                    nombre VARCHAR(100) NOT NULL)
                    """);

            // TABLA CATEGORÍAS
            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS categorias (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(50) NOT NULL)
                    """);

            // TABLA PRODUCTOS
            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS productos (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL,
                    precio DECIMAL(10,2) NOT NULL,
                    stock INT NOT NULL,
                    id_categoria INT,
                    FOREIGN KEY (id_categoria) REFERENCES categorias(id))
                    """);

            // TABLA EMPLEADOS
            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS empleados (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL,
                    telefono VARCHAR(20),
                    puesto VARCHAR(50))
                    """);

            // TABLA CLIENTES
            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS clientes (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL,
                    telefono VARCHAR(20),
                    direccion VARCHAR(150))
                    """);

            // TABLA VENTAS
            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS ventas (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    id_producto INT NOT NULL,
                    id_cliente INT,
                    cantidad INT NOT NULL,
                    total DECIMAL(10,2) NOT NULL,
                    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (id_producto) REFERENCES productos(id),
                    FOREIGN KEY (id_cliente) REFERENCES clientes(id))
                    """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Log() {
        initialize();
    }

    private void initialize() {

        frmLogin = new JFrame();
        frmLogin.setUndecorated(true);
        frmLogin.setSize(500, 560);
        frmLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmLogin.setLocationRelativeTo(null);
        frmLogin.getContentPane().setBackground(BG_COLOR);
        frmLogin.getContentPane().setLayout(null);

        // BOTÓN CERRAR
        JButton btnCerrar = new JButton("X");
        btnCerrar.setBounds(455, 10, 35, 30);
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCerrar.setBackground(new Color(239, 68, 68));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setBorder(null);
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> System.exit(0));
        frmLogin.getContentPane().add(btnCerrar);

        // TARJETA PRINCIPAL
        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(CARD_COLOR);
        tarjeta.setBounds(70, 26, 364, 504);
        tarjeta.setLayout(null);
        tarjeta.setBorder(new RoundBorder(25, Color.LIGHT_GRAY));
        frmLogin.getContentPane().add(tarjeta);

        JLabel lblTitulo = new JLabel("Iniciar Sesión con", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(TEXT_DARK);
        lblTitulo.setBounds(0, 0, 360, 40);
        tarjeta.add(lblTitulo);

        JLabel lblUser = new JLabel("Usuario o Email");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setForeground(TEXT_DARK);
        lblUser.setBounds(40, 220, 200, 20);
        tarjeta.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBounds(40, 251, 280, 35);
        txtUsername.setBorder(new RoundBorder(15, TEXT_LIGHT));
        tarjeta.add(txtUsername);

        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPass.setForeground(TEXT_DARK);
        lblPass.setBounds(40, 297, 200, 20);
        tarjeta.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBounds(40, 328, 280, 35);
        txtPassword.setBorder(new RoundBorder(15, TEXT_LIGHT));
        tarjeta.add(txtPassword);

        JButton btnLogin = new JButton("Entrar");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setBounds(87, 405, 170, 40);
        btnLogin.setBackground(PRIMARY_COLOR);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBorder(new RoundBorder(20, PRIMARY_COLOR));
        btnLogin.setFocusPainted(false);
        btnLogin.addActionListener(e -> iniciarSesion());
        tarjeta.add(btnLogin);

        JLabel lblRegistro = new JLabel("Crear una cuenta");
        lblRegistro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblRegistro.setForeground(ACCENT_COLOR);
        lblRegistro.setBounds(120, 456, 200, 25);
        lblRegistro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        lblRegistro.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                r = new Reg();
                r.frame.setLocationRelativeTo(frmLogin);
                r.frame.setVisible(true);
            }
        });
        tarjeta.add(lblRegistro);

        JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setIcon(new ImageIcon(Log.class.getResource("/comoproyect/don_pepe_206x162.png")));
        lblNewLabel.setBounds(80, 36, 205, 162);
        tarjeta.add(lblNewLabel);
    }

    // INICIAR SESIÓN
    private void iniciarSesion() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(frmLogin, "Rellena todos los campos");
            return;
        }

        String encrypted = encryptPassword(pass);

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/tienda_db", "root", "");
             PreparedStatement ps = con.prepareStatement(
                     "SELECT nombre FROM usuarios WHERE usuario=? AND password=?")) {

            ps.setString(1, user);
            ps.setString(2, encrypted);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                nombreUsuario = rs.getString("nombre");

                b.setNombreUsuario(nombreUsuario);
                b.frame.setVisible(true);
                b.startCarga();

                frmLogin.dispose();

            } else {
                JOptionPane.showMessageDialog(frmLogin, "Usuario o contraseña incorrectos");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frmLogin, "Error SQL: " + ex.getMessage());
        }
    }

    // ENCRIPTAR CONTRASEÑA
    public static String encryptPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error al encriptar", e);
        }
    }

    // BORDE REDONDO
    class RoundBorder extends LineBorder {
        private int radius;

        public RoundBorder(int radius, Color color) {
            super(color, 1, true);
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(lineColor);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }
}
