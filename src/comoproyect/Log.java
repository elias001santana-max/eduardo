package comoproyect;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.*;
import java.security.MessageDigest;
import java.util.Base64;

public class Log {

	public JFrame frmLogin;
	private JTextField txtUsername;
	private JPasswordField txtPassword;
	Reg r = new Reg();
	Bar b = new Bar();
	String nombreUsuario = "";

	// Taqueria Colors
	private final Color PRIMARY_COLOR = new Color(230, 81, 0); // Orange
	private final Color DARK_BG = new Color(33, 33, 33); // Dark Grey

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				verificarBaseDeDatos(); // Ensure DB and tables exist
				Log window = new Log();
				window.frmLogin.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private static void verificarBaseDeDatos() {
		String urlServer = "jdbc:mysql://localhost:3306/";
		String dbName = "taqueria_db";
		String user = "root";
		String pass = "";
	
		try (Connection con = DriverManager.getConnection(urlServer, user, pass);
				Statement stmt = con.createStatement()) {

			// Create Database
			stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
			stmt.executeUpdate("USE " + dbName);

			// 1. Table 'usuarios'
			String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (" + "id INT AUTO_INCREMENT PRIMARY KEY, "
					+ "usuario VARCHAR(50) NOT NULL UNIQUE, " + "password VARCHAR(255) NOT NULL, "
					+ "nombre VARCHAR(100) NOT NULL" + ")";
			stmt.executeUpdate(sqlUsuarios);

			// 2. Table 'cliente'
			String sqlCliente = "CREATE TABLE IF NOT EXISTS cliente (" + "id_cliente INT AUTO_INCREMENT PRIMARY KEY, "
					+ "nombre VARCHAR(100) NOT NULL, " + "telefono VARCHAR(20)" + ")";
			stmt.executeUpdate(sqlCliente);

			// 3. Table 'empleado'
			String sqlEmpleado = "CREATE TABLE IF NOT EXISTS empleado ("
					+ "id_empleado INT AUTO_INCREMENT PRIMARY KEY, " + "nombre VARCHAR(100) NOT NULL, "
					+ "puesto VARCHAR(50) NOT NULL" + ")";
			stmt.executeUpdate(sqlEmpleado);

			// 4. Table 'mesa'
			String sqlMesa = "CREATE TABLE IF NOT EXISTS mesa (" + "id_mesa INT AUTO_INCREMENT PRIMARY KEY, "
					+ "numero INT NOT NULL, " + "estado VARCHAR(20) DEFAULT 'Libre'" + ")";
			stmt.executeUpdate(sqlMesa);

			// 5. Table 'producto' (Needed for details)
			String sqlProducto = "CREATE TABLE IF NOT EXISTS producto ("
					+ "id_producto INT AUTO_INCREMENT PRIMARY KEY, " + "nombre VARCHAR(100) NOT NULL, "
					+ "precio DECIMAL(10,2) NOT NULL" + ")";
			stmt.executeUpdate(sqlProducto);

			// 6. Table 'pedido'
			String sqlPedido = "CREATE TABLE IF NOT EXISTS pedido (" + "id_pedido INT AUTO_INCREMENT PRIMARY KEY, "
					+ "fecha DATETIME NOT NULL, " + "total DECIMAL(10,2) NOT NULL, " + "id_cliente INT, "
					+ "id_empleado INT, " + "id_mesa INT, " + "FOREIGN KEY(id_cliente) REFERENCES cliente(id_cliente), "
					+ "FOREIGN KEY(id_empleado) REFERENCES empleado(id_empleado), "
					+ "FOREIGN KEY(id_mesa) REFERENCES mesa(id_mesa)" + ")";
			stmt.executeUpdate(sqlPedido);

			// 7. Table 'detallepedidos'
			String sqlDetalle = "CREATE TABLE IF NOT EXISTS detallepedidos ("
					+ "id_detalle INT AUTO_INCREMENT PRIMARY KEY, " + "id_pedido INT NOT NULL, "
					+ "id_producto INT NOT NULL, " + "cantidad INT NOT NULL, " + "nota VARCHAR(200), "
					+ "subtotal DECIMAL(10,2) NOT NULL, " + "FOREIGN KEY(id_pedido) REFERENCES pedido(id_pedido), "
					+ "FOREIGN KEY(id_producto) REFERENCES producto(id_producto)" + ")";
			stmt.executeUpdate(sqlDetalle);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Log() {
		initialize();
	}

	private void initialize() {

		frmLogin = new JFrame();
		frmLogin.setUndecorated(true);
		frmLogin.setSize(488, 552);
		frmLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLogin.setLocationRelativeTo(null);
		frmLogin.getContentPane().setLayout(null);

		JButton btnCerrar = new JButton("X");
		btnCerrar.setBounds(450, 5, 30, 30);
		btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnCerrar.setForeground(Color.WHITE);
		btnCerrar.setBackground(new Color(200, 0, 0));
		btnCerrar.setFocusPainted(false);
		btnCerrar.setBorder(null);
		btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnCerrar.addActionListener(e -> System.exit(0));
		frmLogin.getContentPane().add(btnCerrar);

		JPanel panelFondo = new JPanel();
		panelFondo.setBackground(DARK_BG);
		panelFondo.setBounds(0, 0, 480, 520);
		panelFondo.setLayout(null);
		frmLogin.getContentPane().add(panelFondo);

		JPanel tarjeta = new JPanel();
		tarjeta.setBackground(Color.WHITE);
		tarjeta.setBounds(60, 11, 360, 498);
		tarjeta.setLayout(null);
		tarjeta.setBorder(new RoundBorder(30));
		panelFondo.add(tarjeta);

		JLabel lblWelcome = new JLabel("");
		try {
			lblWelcome.setIcon(new ImageIcon(Log.class.getResource("/comoproyect/logo_burning_godzilla_257x201.png")));
		} catch (Exception e) {
			lblWelcome.setText("TAQUERÍA");
			lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
		}
		lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 32));
		lblWelcome.setBounds(52, 11, 257, 201);
		tarjeta.add(lblWelcome);

		JLabel lblUser = new JLabel("Email / Usuario");
		lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblUser.setBounds(40, 235, 200, 20);
		tarjeta.add(lblUser);

		txtUsername = new JTextField();
		txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtUsername.setBounds(40, 266, 280, 35);
		txtUsername.setBorder(new RoundBorder(15));
		tarjeta.add(txtUsername);

		JLabel lblPass = new JLabel("Password");
		lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblPass.setBounds(40, 312, 200, 20);
		tarjeta.add(lblPass);

		txtPassword = new JPasswordField();
		txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtPassword.setBounds(40, 343, 280, 35);
		txtPassword.setBorder(new RoundBorder(15));
		tarjeta.add(txtPassword);

		JButton btnLogin = new JButton("Inicia Sesión");
		btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btnLogin.setBounds(89, 416, 180, 40);
		btnLogin.setBackground(PRIMARY_COLOR);
		btnLogin.setForeground(Color.WHITE);
		btnLogin.setBorder(new RoundBorder(20));
		btnLogin.setFocusPainted(false);
		btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
		tarjeta.add(btnLogin);

		btnLogin.addActionListener(e -> iniciarSesion());

		JLabel lblRegistro = new JLabel("Registrarse");
		lblRegistro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblRegistro.setForeground(PRIMARY_COLOR);
		lblRegistro.setBounds(144, 467, 68, 20);
		lblRegistro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		lblRegistro.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				r.frmReg.setLocationRelativeTo(frmLogin);
				r.frmReg.setVisible(true);
			}
		});

		tarjeta.add(lblRegistro);
	}

	// ---------------------------------------------------------
	// MÉTODO CORRECTO PARA INICIAR SESIÓN
	// ---------------------------------------------------------
	private void iniciarSesion() {
		String user = txtUsername.getText();
		String pass = new String(txtPassword.getPassword());
		String encrypted = encryptPassword(pass);

		Connection con = null;
		PreparedStatement ps = null;

		try {
			// Direct connection to 'taqueria_db' DB
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/taqueria_db", "root", "");

			String sql = "SELECT nombre FROM usuarios WHERE usuario=? AND password=?";
			ps = con.prepareStatement(sql);
			ps.setString(1, user);
			ps.setString(2, encrypted);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				nombreUsuario = rs.getString("nombre");

				b.setNombreUsuario(nombreUsuario);
				b.frame.setVisible(true);
				b.startCarga();

				frmLogin.dispose(); // cierra login
			} else {
				JOptionPane.showMessageDialog(frmLogin, "Usuario o contraseña incorrectos");
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(frmLogin, "Error SQL: " + ex.getMessage());
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
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
