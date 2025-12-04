
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

    // Colores
    private final Color PRIMARY_COLOR = new Color(230, 81, 0); // Naranja
    private final Color DARK_BG = new Color(33, 33, 33);       // Gris oscuro

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Reg window = new Reg();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Reg() {
        initialize();
    }

    private void initialize() {

        frame = new JFrame();
        frame.setUndecorated(true);
        frame.setSize(488, 552);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(null);

        // ---- BOTÓN DE CERRAR ----
        JButton btnCerrar = new JButton("X");
        btnCerrar.setBounds(448, 5, 35, 25);
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCerrar.setBackground(new Color(200, 0, 0));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorder(null);
        btnCerrar.addActionListener(e -> frame.dispose());
        frame.getContentPane().add(btnCerrar);

        // PANEL DE FONDO
        JPanel panelFondo = new JPanel();
        panelFondo.setBackground(DARK_BG);
        panelFondo.setBounds(0, 0, 480, 520);
        panelFondo.setLayout(null);
        frame.getContentPane().add(panelFondo);

        // TARJETA BLANCA
        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBounds(60, 11, 360, 498);
        tarjeta.setLayout(null);
        tarjeta.setBorder(new RoundBorder(30));
        panelFondo.add(tarjeta);

        // LOGO
        JLabel lblLogo = new JLabel("TAQUERÍA");
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblLogo.setBounds(52, 11, 257, 203);
        tarjeta.add(lblLogo);

        // ---- CAMPOS ----
        JLabel lblNombre = new JLabel("Nombre");
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblNombre.setBounds(40, 210, 200, 20);
        tarjeta.add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNombre.setBounds(40, 232, 280, 35);
        txtNombre.setBorder(new RoundBorder(15));
        tarjeta.add(txtNombre);

        JLabel lblUser = new JLabel("Usuario");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setBounds(40, 272, 200, 20);
        tarjeta.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBounds(40, 294, 280, 35);
        txtUsername.setBorder(new RoundBorder(15));
        tarjeta.add(txtUsername);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPass.setBounds(40, 334, 200, 20);
        tarjeta.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBounds(40, 356, 280, 35);
        txtPassword.setBorder(new RoundBorder(15));
        tarjeta.add(txtPassword);

        // ---- BOTÓN REGISTRAR ----
        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnRegistrar.setBounds(89, 416, 180, 40);
        btnRegistrar.setBackground(PRIMARY_COLOR);
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setBorder(new RoundBorder(20));
        btnRegistrar.setFocusPainted(false);
        tarjeta.add(btnRegistrar);

        btnRegistrar.addActionListener(e -> registrar());
    }

    // =============================
    //      MÉTODO REGISTRAR
    // =============================
    private void registrar() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        String nombre = txtNombre.getText().trim();

        if (user.isEmpty() || pass.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Llena todos los campos");
            return;
        }

        String sql = "INSERT INTO usuarios (usuario, password, nombre) VALUES (?, ?, ?)";

        try (Connection cx = DriverManager.getConnection("jdbc:mysql://localhost:3306/taqueria_db", "root", "");
             PreparedStatement ps = cx.prepareStatement(sql)) {

            ps.setString(1, user);
            ps.setString(2, encryptPassword(pass));
            ps.setString(3, nombre);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(frame, "SE REGISTRO CORRECTAMENTE");
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "No se pudo registrar");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error SQL: " + ex.getMessage());
        }
    }

    // =============================
    //      ENCRIPTACIÓN SHA-256
    // =============================
    public static String encryptPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error al encriptar", e);
        }
    }

    // =============================
    //     BORDE REDONDEADO
    // =============================
    class RoundBorder extends LineBorder {
        private int radius;

        public RoundBorder(int radius) {
            super(Color.LIGHT_GRAY, 1, true);
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

