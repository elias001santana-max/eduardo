package comoproyect;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Base64;

public class Reg {

    public JFrame frame;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtNombre;

    private final Color SIDEBAR_COLOR = new Color(30, 41, 59);
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private final Color BG_COLOR = new Color(241, 245, 249);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color TEXT_DARK = new Color(30, 41, 59);
    private final Color TEXT_LIGHT = new Color(148, 163, 184);

    public Reg() {
        initialize();
    }

    private void initialize() {

        frame = new JFrame();
        frame.setUndecorated(true);
        frame.setSize(488, 552);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(null);
        frame.getContentPane().setBackground(BG_COLOR);

        JButton btnCerrar = new JButton("X");
        btnCerrar.setBounds(448, 5, 35, 25);
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setBackground(new Color(239, 68, 68));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorder(null);
        btnCerrar.addActionListener(e -> frame.dispose());
        frame.getContentPane().add(btnCerrar);

        JPanel panelFondo = new JPanel();
        panelFondo.setBackground(SIDEBAR_COLOR);
        panelFondo.setBounds(0, 0, 488, 552);
        panelFondo.setLayout(null);
        frame.getContentPane().add(panelFondo);

        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(CARD_COLOR);
        tarjeta.setBounds(60, 11, 360, 498);
        tarjeta.setLayout(null);
        tarjeta.setBorder(new RoundBorder(25));
        panelFondo.add(tarjeta);

        JLabel lblLogo = new JLabel("REGISTRO");
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblLogo.setForeground(PRIMARY_COLOR);
        lblLogo.setBounds(52, 11, 257, 50);
        tarjeta.add(lblLogo);

        JLabel lblNombre = new JLabel("Nombre");
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblNombre.setForeground(TEXT_DARK);
        lblNombre.setBounds(40, 100, 200, 20);
        tarjeta.add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNombre.setBounds(40, 125, 280, 40);
        txtNombre.setBorder(new RoundBorder(20));
        tarjeta.add(txtNombre);

        JLabel lblUser = new JLabel("Usuario");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblUser.setForeground(TEXT_DARK);
        lblUser.setBounds(40, 175, 200, 20);
        tarjeta.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBounds(40, 200, 280, 40);
        txtUsername.setBorder(new RoundBorder(20));
        tarjeta.add(txtUsername);

        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblPass.setForeground(TEXT_DARK);
        lblPass.setBounds(40, 250, 200, 20);
        tarjeta.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBounds(40, 275, 280, 40);
        txtPassword.setBorder(new RoundBorder(20));
        tarjeta.add(txtPassword);

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnRegistrar.setBounds(89, 350, 180, 45);
        btnRegistrar.setBackground(PRIMARY_COLOR);
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setBorder(new RoundBorder(25));
        btnRegistrar.setFocusPainted(false);

        btnRegistrar.addActionListener(e -> registrar());
        tarjeta.add(btnRegistrar);
    }

    // -----------------------------
    // REGISTRAR USUARIO EN LA BASE DE DATOS
    // -----------------------------

    private void registrar() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        String nombre = txtNombre.getText().trim();

        if (user.isEmpty() || pass.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Llena todos los campos");
            return;
        }

        try (Connection cx = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/tienda_db", "root", "")) {

            // 1️⃣ Verificar si el usuario YA existe
            String checkSql = "SELECT usuario FROM usuarios WHERE usuario = ?";
            PreparedStatement checkStmt = cx.prepareStatement(checkSql);
            checkStmt.setString(1, user);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(frame, "Ese usuario ya existe");
                return;
            }

            // 2️⃣ Registrar usuario
            String sql = "INSERT INTO usuarios (usuario, password, nombre) VALUES (?, ?, ?)";
            PreparedStatement ps = cx.prepareStatement(sql);

            ps.setString(1, user);
            ps.setString(2, encryptPassword(pass));
            ps.setString(3, nombre);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(frame, "Registro exitoso");
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "No se pudo registrar");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error SQL: " + ex.getMessage());
        }
    }

    public static String encryptPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error al encriptar", e);
        }
    }

    class RoundBorder extends LineBorder {
        private int radius;

        public RoundBorder(int radius) {
            super(new Color(220, 220, 220), 1, true);
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
