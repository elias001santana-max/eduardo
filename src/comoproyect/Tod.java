package comoproyect;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.util.Vector;

public class Tod {

    public JFrame frame;
    private String nombreUsuario = "Usuario";
    
    // Componentes de la tabla de usuarios
    private DefaultTableModel modeloUsuarios;
    private JTable tablaUsuarios;
    private JTextField txtIdUsuario, txtNombreUsuario, txtApellidoUsuario, txtEmailUsuario, txtCargoUsuario, txtTelefonoUsuario, txtEstadoUsuario;
    
    // Componentes de la tabla de productos
    private DefaultTableModel modeloProductos;
    private JTable tablaProductos;
    private JTextField txtIdProducto, txtNombreProducto, txtPrecio, txtStock, txtIdCategoria, txtEstado;
    
    // Componentes de la tabla de clientes
    private DefaultTableModel modeloClientes;
    private JTable tablaClientes;
    private JTextField txtIdCliente, txtNombreCliente, txtApellidoCliente, txtTelefonoCliente, txtEmailCliente, txtDireccionCliente, txtCiudadCliente;
    
    // Componentes de la tabla de categorías
    private DefaultTableModel modeloCategorias;
    private JTable tablaCategorias;
    private JTextField txtIdCategoriaCat, txtNombreCategoria, txtDescripcionCategoria, txtProductoCategoria;
    
    // Variables para preservar el estado de categorías entre cambios de panel
    private String categoriaIdPreservado = "";
    private String categoriaNombrePreservado = "";
    private String categoriaDescripcionPreservado = "";
    private String categoriaProductoPreservado = "";
    private int categoriaFilaSeleccionada = -1; // Índice de fila seleccionada
    
    // Componentes del panel de ventas
    private DefaultTableModel modeloProductosDisponibles;
    private DefaultTableModel modeloCarrito;
    private JTable tablaProductosDisponibles;
    private JTable tablaCarrito;
    private JLabel lblTotal;
    private double totalVenta = 0.0;
    private int nextIdVentaProducto = 1; // ID auto-incremental para productos en ventas (diferente al ID de productos)
    
    
    private JPanel mainContent;
    
    // Contadores auto-incrementables para IDs (ya no se usan, IDs vienen de la BD)
    private int nextIdUsuario = 1;
    private int nextIdProducto = 1;
    private int nextIdCliente = 1;
    private int nextIdCategoria = 1;
    
    // Variable para preservar selección de producto entre cambios de panel
    private int productoFilaSeleccionada = -1;
    
    // Variables para la configuración de TIENDA
    private String nombreTiendaGuardado = "";
    private String rutaImagenTiendaGuardada = null;


    // Colores del Dashboard - AZUL THEME
    private final Color SIDEBAR_COLOR = new Color(30, 41, 59);
    private final Color SIDEBAR_HOVER = new Color(51, 65, 85);
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private final Color ACCENT_COLOR = new Color(251, 146, 60);
    private final Color BG_COLOR = new Color(241, 245, 249);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color TEXT_DARK = new Color(30, 41, 59);
    private final Color TEXT_LIGHT = new Color(148, 163, 184);

    private JLabel avatarCircle;
    private JLabel lblNombre;

    public Tod() {
        // Inicializar modelos de ventas ANTES de initialize() 
        // para que estén listos cuando se agreguen productos
        modeloProductosDisponibles = new DefaultTableModel();
        modeloProductosDisponibles.addColumn("ID");
        modeloProductosDisponibles.addColumn("Nombre");
        modeloProductosDisponibles.addColumn("Precio");
        modeloProductosDisponibles.addColumn("Stock");
        
        modeloCarrito = new DefaultTableModel();
        modeloCarrito.addColumn("ID");
        modeloCarrito.addColumn("Producto");
        modeloCarrito.addColumn("Precio");
        modeloCarrito.addColumn("Cantidad");
        modeloCarrito.addColumn("Subtotal");
        
        // Inicializar modelo de Empleados para sincronización automática
        modeloUsuarios = new DefaultTableModel();
        modeloUsuarios.addColumn("ID");
        modeloUsuarios.addColumn("Nombre");
        modeloUsuarios.addColumn("Apellido");
        modeloUsuarios.addColumn("Email");
        modeloUsuarios.addColumn("Cargo");
        modeloUsuarios.addColumn("Teléfono");
        modeloUsuarios.addColumn("Estado");
        
        // Inicializar modelo de Clientes para sincronización automática
        modeloClientes = new DefaultTableModel();
        modeloClientes.addColumn("ID");
        modeloClientes.addColumn("Nombre");
        modeloClientes.addColumn("Apellido");
        modeloClientes.addColumn("Teléfono");
        modeloClientes.addColumn("Email");
        modeloClientes.addColumn("Dirección");
        modeloClientes.addColumn("Ciudad");
        
        initialize();
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;

        if (avatarCircle != null) {
            avatarCircle.setText(nombreUsuario.substring(0, 1).toUpperCase());
        }
        if (lblNombre != null) {
            lblNombre.setText(nombreUsuario);
        }
    }

    private void initialize() {
        frame = new JFrame();
        frame.setUndecorated(true); // ❌ QUITA BARRA DE WINDOWS
        
        // 📺 PANTALLA COMPLETA - Obtener dimensiones de la pantalla
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize.width, screenSize.height);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizar ventana
        
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.getContentPane().setBackground(BG_COLOR);
        
        // ==================== ATAJOS DE TECLADO GLOBALES ====================
        // F3 = Producto, F4 = Empleado, F5 = Cliente, F6 = Ventas, F7 = Categoría
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                // ATAJOS DE TECLADO DESHABILITADOS TEMPORALMENTE
                /*
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    switch(e.getKeyCode()) {
                        case KeyEvent.VK_F3:
                            mostrarPanelProductos();
                            return true;
                        case KeyEvent.VK_F4:
                            mostrarPanelUsuarios();
                            return true;
                        case KeyEvent.VK_F5:
                            mostrarPanelClientes();
                            return true;
                        case KeyEvent.VK_F6:
                            mostrarPanelVentas();
                            return true;
                        case KeyEvent.VK_F7:
                            mostrarPanelCategorias();
                            return true;
                    }
                }
                */
                return false;
            }
        });

        // SIDEBAR
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setBounds(0, 0, 250, screenSize.height);
        sidebar.setLayout(null);
        frame.add(sidebar);

        // Avatar del usuario
        avatarCircle = new JLabel();
        avatarCircle.setBounds(85, 30, 80, 80);
        avatarCircle.setOpaque(true);
        avatarCircle.setBackground(PRIMARY_COLOR);
        avatarCircle.setHorizontalAlignment(SwingConstants.CENTER);
        avatarCircle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        avatarCircle.setForeground(Color.WHITE);
        avatarCircle.setText(nombreUsuario.substring(0, 1).toUpperCase());
        avatarCircle.setBorder(new RoundBorder(40, PRIMARY_COLOR));
        sidebar.add(avatarCircle);

        lblNombre = new JLabel(nombreUsuario, SwingConstants.CENTER);
        lblNombre.setBounds(20, 115, 210, 25);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNombre.setForeground(Color.WHITE);
        sidebar.add(lblNombre);

        JLabel lblRole = new JLabel("Administrator", SwingConstants.CENTER);
        lblRole.setBounds(20, 140, 210, 20);
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(TEXT_LIGHT);
        sidebar.add(lblRole);

        // Menú lateral (sin Dashboard)
        String[] menuItems = {"Producto", "Empleado", "Cliente", "Ventas", "Categoría", "Configuración"};

        int yPos = 190;
        for (int i = 0; i < menuItems.length; i++) {
            final String itemName = menuItems[i];
            final int index = i;
            JPanel menuItem = createMenuItem(menuItems[i], i == 0);
            menuItem.setBounds(15, yPos, 220, 45);
            
            // Agregar funcionalidad de clic
            menuItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (itemName.equals("Dashboard")) {
                        mostrarPanelDashboard();
                    } else if (itemName.equals("Producto")) {
                        mostrarPanelProductos();
                    } else if (itemName.equals("Empleado")) {
                        mostrarPanelUsuarios();
                    } else if (itemName.equals("Cliente")) {
                        mostrarPanelClientes();
                    } else if (itemName.equals("Ventas")) {
                        mostrarPanelVentas();
                    } else if (itemName.equals("Categoría")) {
                        mostrarPanelCategorias();
                    } else if (itemName.equals("Configuración")) {
                        mostrarPanelConfiguracion();
                    }
                }
            });
            
            sidebar.add(menuItem);
            yPos += 50;
        }

        // Panel principal
        mainContent = new JPanel();
        mainContent.setBackground(BG_COLOR);
        mainContent.setBounds(250, 0, screenSize.width - 250, screenSize.height);
        mainContent.setLayout(null);
        frame.add(mainContent);

        // Mostrar panel de Productos al iniciar (en lugar de Dashboard)
        mostrarPanelProductos();
    }
    
    private void mostrarPanelDashboard() {
        mainContent.removeAll();
        
        JLabel lblHeader = new JLabel("Dashboard");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblHeader.setForeground(TEXT_DARK);
        lblHeader.setBounds(30, 20, 300, 40);
        mainContent.add(lblHeader);

        JLabel lblDate = new JLabel("Diciembre 04, 2025");
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDate.setForeground(TEXT_LIGHT);
        lblDate.setBounds(30, 55, 300, 25);
        mainContent.add(lblDate);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(820, 20, 100, 35);
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setBackground(new Color(239, 68, 68));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> frame.dispose());
        mainContent.add(btnCerrar);

        // Dashboard limpio - sin tarjetas ni gráficas
        
        mainContent.revalidate();
        mainContent.repaint();
    }
    
    private void mostrarPanelUsuarios() {
        mainContent.removeAll();
        
        // Header
        JLabel lblHeader = new JLabel("Gestión de Usuarios");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblHeader.setForeground(TEXT_DARK);
        lblHeader.setBounds(30, 20, 400, 40);
        mainContent.add(lblHeader);
        
        // Botón Cerrar
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(820, 20, 100, 35);
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setBackground(new Color(239, 68, 68));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        mainContent.add(btnCerrar);
        
        // Panel de formulario (extendido)
        JPanel formPanel = new JPanel();
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBounds(30, 80, 740, 180);
        formPanel.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        formPanel.setLayout(null);
        mainContent.add(formPanel);
        
        // === LOGO DE TIENDA (lado derecho, arriba) ===
        JPanel panelLogoTienda = new JPanel();
        panelLogoTienda.setBackground(CARD_COLOR);
        panelLogoTienda.setBounds(780, 60, 160, 110);
        panelLogoTienda.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        panelLogoTienda.setLayout(null);
        mainContent.add(panelLogoTienda);
        
        JLabel lblLogoTienda = new JLabel("Imagen", SwingConstants.CENTER);
        lblLogoTienda.setBounds(5, 5, 150, 100);
        lblLogoTienda.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLogoTienda.setForeground(TEXT_LIGHT);
        lblLogoTienda.setBorder(BorderFactory.createDashedBorder(Color.LIGHT_GRAY, 2, 5));
        if (rutaImagenTiendaGuardada != null && !rutaImagenTiendaGuardada.isEmpty()) {
            try {
                ImageIcon iconoOriginal = new ImageIcon(rutaImagenTiendaGuardada);
                Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(140, 90, Image.SCALE_SMOOTH);
                lblLogoTienda.setIcon(new ImageIcon(imagenEscalada));
                lblLogoTienda.setText("");
            } catch (Exception ex) { lblLogoTienda.setText("Error"); }
        }
        panelLogoTienda.add(lblLogoTienda);
        
        // === NOMBRE DE TIENDA ===
        JLabel lblNombreTiendaDisplay = new JLabel("", SwingConstants.CENTER);
        lblNombreTiendaDisplay.setBounds(780, 175, 160, 25);
        lblNombreTiendaDisplay.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNombreTiendaDisplay.setForeground(TEXT_DARK);
        if (nombreTiendaGuardado != null && !nombreTiendaGuardado.isEmpty()) {
            lblNombreTiendaDisplay.setText(nombreTiendaGuardado);
        }
        mainContent.add(lblNombreTiendaDisplay);
        
        // Fila 1: ID, Nombre, Apellido
        JLabel lblId = new JLabel("ID:");
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblId.setBounds(20, 20, 80, 25);
        formPanel.add(lblId);
        
        txtIdUsuario = new JTextField();
        txtIdUsuario.setBounds(20, 45, 120, 35);
        txtIdUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtIdUsuario.setEditable(false);
        txtIdUsuario.setBackground(new Color(240, 240, 240));
        formPanel.add(txtIdUsuario);
        
        JLabel lblNombreUsr = new JLabel("Nombre:");
        lblNombreUsr.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombreUsr.setBounds(160, 20, 80, 25);
        formPanel.add(lblNombreUsr);
        
        txtNombreUsuario = new JTextField();
        txtNombreUsuario.setBounds(160, 45, 200, 35);
        txtNombreUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtNombreUsuario);
        
        JLabel lblApellido = new JLabel("Apellido:");
        lblApellido.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblApellido.setBounds(380, 20, 80, 25);
        formPanel.add(lblApellido);
        
        txtApellidoUsuario = new JTextField();
        txtApellidoUsuario.setBounds(380, 45, 200, 35);
        txtApellidoUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtApellidoUsuario);
        
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEmail.setBounds(600, 20, 80, 25);
        formPanel.add(lblEmail);
        
        txtEmailUsuario = new JTextField();
        txtEmailUsuario.setBounds(600, 45, 260, 35);
        txtEmailUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtEmailUsuario);
        
        // Fila 2: Cargo, Teléfono, Estado
        JLabel lblCargo = new JLabel("Cargo:");
        lblCargo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCargo.setBounds(20, 95, 80, 25);
        formPanel.add(lblCargo);
        
        txtCargoUsuario = new JTextField();
        txtCargoUsuario.setBounds(20, 120, 250, 35);
        txtCargoUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtCargoUsuario);
        
        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTelefono.setBounds(290, 95, 80, 25);
        formPanel.add(lblTelefono);
        
        txtTelefonoUsuario = new JTextField();
        txtTelefonoUsuario.setBounds(290, 120, 200, 35);
        txtTelefonoUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtTelefonoUsuario);
        
        JLabel lblEstado = new JLabel("Estado:");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEstado.setBounds(510, 95, 80, 25);
        formPanel.add(lblEstado);
        
        txtEstadoUsuario = new JTextField();
        txtEstadoUsuario.setBounds(510, 120, 150, 35);
        txtEstadoUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtEstadoUsuario);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BG_COLOR);
        buttonPanel.setBounds(30, 280, 890, 50);
        buttonPanel.setLayout(null);
        mainContent.add(buttonPanel);
        
        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(0, 0, 140, 40);
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAgregar.setBackground(PRIMARY_COLOR);
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFocusPainted(false);
        btnAgregar.addActionListener(e -> agregarUsuario());
        buttonPanel.add(btnAgregar);
        
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(160, 0, 140, 40);
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnActualizar.setBackground(ACCENT_COLOR);
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.addActionListener(e -> actualizarUsuario());
        buttonPanel.add(btnActualizar);
        
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(320, 0, 140, 40);
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEliminar.setBackground(new Color(239, 68, 68));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.addActionListener(e -> eliminarUsuario());
        buttonPanel.add(btnEliminar);
        
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(480, 0, 140, 40);
        btnLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLimpiar.setBackground(new Color(100, 116, 139));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.addActionListener(e -> limpiarCampos());
        buttonPanel.add(btnLimpiar);
        
        // Tabla de usuarios - SOLO crear modelo si no existe (preservar datos sincronizados)
        if (modeloUsuarios == null) {
            modeloUsuarios = new DefaultTableModel();
            modeloUsuarios.addColumn("ID");
            modeloUsuarios.addColumn("Nombre");
            modeloUsuarios.addColumn("Apellido");
            modeloUsuarios.addColumn("Email");
            modeloUsuarios.addColumn("Cargo");
            modeloUsuarios.addColumn("Teléfono");
            modeloUsuarios.addColumn("Estado");
        }
        
        tablaUsuarios = new JTable(modeloUsuarios);
        tablaUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaUsuarios.setRowHeight(30);
        tablaUsuarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaUsuarios.getTableHeader().setBackground(PRIMARY_COLOR);
        tablaUsuarios.getTableHeader().setForeground(Color.WHITE);
        
        // Listener para seleccionar fila
        tablaUsuarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaUsuarios.getSelectedRow();
                if (fila >= 0) {
                    txtIdUsuario.setText(tablaUsuarios.getValueAt(fila, 0).toString());
                    txtNombreUsuario.setText(tablaUsuarios.getValueAt(fila, 1).toString());
                    txtApellidoUsuario.setText(tablaUsuarios.getValueAt(fila, 2).toString());
                    txtEmailUsuario.setText(tablaUsuarios.getValueAt(fila, 3).toString());
                    txtCargoUsuario.setText(tablaUsuarios.getValueAt(fila, 4).toString());
                    txtTelefonoUsuario.setText(tablaUsuarios.getValueAt(fila, 5).toString());
                    txtEstadoUsuario.setText(tablaUsuarios.getValueAt(fila, 6).toString());
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        scrollPane.setBounds(30, 350, 890, 310);
        scrollPane.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        mainContent.add(scrollPane);
        
        // Solo cargar de BD si el modelo está vacío (preservar filas sincronizadas)
        // cargarEmpleados();  // COMENTADO para preservar sincronización
        
        mainContent.revalidate();
        mainContent.repaint();
    }
    
    private void cargarDatosEjemplo() {
        modeloUsuarios.addRow(new Object[]{"1", "John", "Doe", "john@example.com", "Gerente", "555-0001", "Activo"});
        modeloUsuarios.addRow(new Object[]{"2", "Jane", "Smith", "jane@example.com", "Vendedor", "555-0002", "Activo"});
        modeloUsuarios.addRow(new Object[]{"3", "Carlos", "López", "carlos@example.com", "Cajero", "555-0003", "Activo"});
        modeloUsuarios.addRow(new Object[]{"4", "María", "García", "maria@example.com", "Vendedor", "555-0004", "Inactivo"});
        modeloUsuarios.addRow(new Object[]{"5", "Pedro", "Martínez", "pedro@example.com", "Supervisor", "555-0005", "Activo"});
        nextIdUsuario = 6; // Siguiente ID disponible
    }
    
    private void agregarUsuario() {
        if (validarCampos()) {
            try {
                String nombre = txtNombreUsuario.getText();
                String apellido = txtApellidoUsuario.getText();
                String email = txtEmailUsuario.getText();
                String cargo = txtCargoUsuario.getText();
                String telefono = txtTelefonoUsuario.getText();
                String estado = txtEstadoUsuario.getText();
                
                // Insertar en la base de datos
                Connection conn = Conexion.getInstancia().getConnection();
                String sql = "INSERT INTO empleados (nombre, apellido, email, cargo, telefono, estado) VALUES (?, ?, ?, ?, ?, ?)";
                java.sql.PreparedStatement pst = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
                pst.setString(1, nombre);
                pst.setString(2, apellido);
                pst.setString(3, email);
                pst.setString(4, cargo);
                pst.setString(5, telefono);
                pst.setString(6, estado);
                
                int filasAfectadas = pst.executeUpdate();
                
                if (filasAfectadas > 0) {
                    // Obtener el ID generado
                    java.sql.ResultSet rs = pst.getGeneratedKeys();
                    int nuevoId = 0;
                    if (rs.next()) {
                        nuevoId = rs.getInt(1);
                    }
                    
                    // Agregar a la tabla visual
                    Vector<String> fila = new Vector<>();
                    fila.add(String.valueOf(nuevoId));
                    fila.add(nombre);
                    fila.add(apellido);
                    fila.add(email);
                    fila.add(cargo);
                    fila.add(telefono);
                    fila.add(estado);
                    modeloUsuarios.addRow(fila);
                    
                    // 🔄 SINCRONIZACIÓN AUTOMÁTICA
                    sincronizarEmpleadoEnOtrasTablas(nuevoId, nombre, apellido);
                    
                    limpiarCampos();
                    JOptionPane.showMessageDialog(frame, 
                        "Empleado agregado exitosamente con ID: " + nuevoId + "\n✅ Guardado en base de datos", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
                
                pst.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, 
                    "Error al agregar empleado: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void actualizarUsuario() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila >= 0) {
            if (validarCampos()) {
                try {
                    int id = Integer.parseInt(txtIdUsuario.getText());
                    String nombre = txtNombreUsuario.getText();
                    String apellido = txtApellidoUsuario.getText();
                    String email = txtEmailUsuario.getText();
                    String cargo = txtCargoUsuario.getText();
                    String telefono = txtTelefonoUsuario.getText();
                    String estado = txtEstadoUsuario.getText();
                    
                    // Actualizar en la base de datos
                    Connection conn = Conexion.getInstancia().getConnection();
                    String sql = "UPDATE empleados SET nombre=?, apellido=?, email=?, cargo=?, telefono=?, estado=? WHERE id=?";
                    java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, nombre);
                    pst.setString(2, apellido);
                    pst.setString(3, email);
                    pst.setString(4, cargo);
                    pst.setString(5, telefono);
                    pst.setString(6, estado);
                    pst.setInt(7, id);
                    
                    int filasAfectadas = pst.executeUpdate();
                    
                    if (filasAfectadas > 0) {
                        // Actualizar en la tabla visual
                        modeloUsuarios.setValueAt(String.valueOf(id), fila, 0);
                        modeloUsuarios.setValueAt(nombre, fila, 1);
                        modeloUsuarios.setValueAt(apellido, fila, 2);
                        modeloUsuarios.setValueAt(email, fila, 3);
                        modeloUsuarios.setValueAt(cargo, fila, 4);
                        modeloUsuarios.setValueAt(telefono, fila, 5);
                        modeloUsuarios.setValueAt(estado, fila, 6);
                        
                        limpiarCampos();
                        JOptionPane.showMessageDialog(frame, "Usuario actualizado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                    pst.close();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, 
                        "Error al actualizar usuario: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Seleccione un usuario de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void eliminarUsuario() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila >= 0) {
            int confirmacion = JOptionPane.showConfirmDialog(frame, 
                "¿Está seguro de eliminar este usuario?", 
                "Confirmar eliminación", 
                JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    int id = Integer.parseInt(tablaUsuarios.getValueAt(fila, 0).toString());
                    
                    // Eliminar de la base de datos
                    Connection conn = Conexion.getInstancia().getConnection();
                    String sql = "DELETE FROM empleados WHERE id=?";
                    java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setInt(1, id);
                    
                    int filasAfectadas = pst.executeUpdate();
                    
                    if (filasAfectadas > 0) {
                        // Eliminar de la tabla visual
                        modeloUsuarios.removeRow(fila);
                        limpiarCampos();
                        JOptionPane.showMessageDialog(frame, "Usuario eliminado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                    pst.close();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, 
                        "Error al eliminar usuario: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Seleccione un usuario de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void limpiarCampos() {
        txtIdUsuario.setText("");
        txtNombreUsuario.setText("");
        txtApellidoUsuario.setText("");
        txtEmailUsuario.setText("");
        txtCargoUsuario.setText("");
        txtTelefonoUsuario.setText("");
        txtEstadoUsuario.setText("");
        tablaUsuarios.clearSelection();
    }
    
    private boolean validarCampos() {
        if (txtNombreUsuario.getText().trim().isEmpty() || 
            txtApellidoUsuario.getText().trim().isEmpty() || 
            txtEmailUsuario.getText().trim().isEmpty() || 
            txtCargoUsuario.getText().trim().isEmpty() || 
            txtTelefonoUsuario.getText().trim().isEmpty() || 
            txtEstadoUsuario.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    private void mostrarPanelProductos() {
        mainContent.removeAll();
        
        // Header
        JLabel lblHeader = new JLabel("Gestión de Productos");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblHeader.setForeground(TEXT_DARK);
        lblHeader.setBounds(30, 20, 400, 40);
        mainContent.add(lblHeader);
        
        // Botón Cerrar
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(820, 20, 100, 35);
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setBackground(new Color(239, 68, 68));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> frame.dispose());
        mainContent.add(btnCerrar);
        
        // Panel de formulario (reducido para dejar espacio a la derecha)
        JPanel formPanel = new JPanel();
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBounds(30, 80, 680, 180);
        formPanel.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        formPanel.setLayout(null);
        mainContent.add(formPanel);
        
        // === CUADRO DE IMAGEN (lado derecho, arriba) ===
        JPanel panelImagenTienda = new JPanel();
        panelImagenTienda.setBackground(CARD_COLOR);
        panelImagenTienda.setBounds(730, 70, 200, 160);
        panelImagenTienda.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        panelImagenTienda.setLayout(null);
        mainContent.add(panelImagenTienda);
        
        JLabel lblImagenTienda = new JLabel("Imagen", SwingConstants.CENTER);
        lblImagenTienda.setBounds(5, 5, 190, 150);
        lblImagenTienda.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblImagenTienda.setForeground(TEXT_LIGHT);
        lblImagenTienda.setBorder(BorderFactory.createDashedBorder(Color.LIGHT_GRAY, 2, 5));
        
        // Mostrar imagen guardada de la tienda si existe
        if (rutaImagenTiendaGuardada != null && !rutaImagenTiendaGuardada.isEmpty()) {
            try {
                ImageIcon iconoOriginal = new ImageIcon(rutaImagenTiendaGuardada);
                Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(180, 140, Image.SCALE_SMOOTH);
                lblImagenTienda.setIcon(new ImageIcon(imagenEscalada));
                lblImagenTienda.setText("");
            } catch (Exception ex) {
                lblImagenTienda.setText("Error");
            }
        }
        panelImagenTienda.add(lblImagenTienda);
        
        // === NOMBRE DE LA TIENDA (lado derecho, abajo) ===
        // Solo mostrar el nombre sin fondo ni etiqueta "Nombre"
        JLabel lblNombreTiendaDisplay = new JLabel("", SwingConstants.CENTER);
        lblNombreTiendaDisplay.setBounds(730, 240, 200, 40);
        lblNombreTiendaDisplay.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNombreTiendaDisplay.setForeground(TEXT_DARK);
        // Mostrar nombre guardado de la tienda si existe
        if (nombreTiendaGuardado != null && !nombreTiendaGuardado.isEmpty()) {
            lblNombreTiendaDisplay.setText(nombreTiendaGuardado);
        }
        mainContent.add(lblNombreTiendaDisplay);
        
        // Fila 1: ID, Nombre, Precio
        JLabel lblId = new JLabel("ID Producto:");
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblId.setBounds(20, 20, 100, 25);
        formPanel.add(lblId);
        
        txtIdProducto = new JTextField();
        txtIdProducto.setBounds(20, 45, 180, 35);
        txtIdProducto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtIdProducto.setEditable(false);
        txtIdProducto.setBackground(new Color(240, 240, 240));
        formPanel.add(txtIdProducto);
        
        JLabel lblNombreProd = new JLabel("Nombre:");
        lblNombreProd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombreProd.setBounds(220, 20, 100, 25);
        formPanel.add(lblNombreProd);
        
        txtNombreProducto = new JTextField();
        txtNombreProducto.setBounds(220, 45, 280, 35);
        txtNombreProducto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtNombreProducto);
        
        JLabel lblPrecio = new JLabel("Precio:");
        lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPrecio.setBounds(520, 20, 100, 25);
        formPanel.add(lblPrecio);
        
        txtPrecio = new JTextField();
        txtPrecio.setBounds(520, 45, 150, 35);
        txtPrecio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtPrecio);
        
        // Fila 2: Stock, Categoría, Estado
        JLabel lblStock = new JLabel("Stock:");
        lblStock.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStock.setBounds(20, 95, 100, 25);
        formPanel.add(lblStock);
        
        txtStock = new JTextField();
        txtStock.setBounds(20, 120, 150, 35);
        txtStock.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtStock);
        
        JLabel lblCategoria = new JLabel("ID Categoría:");
        lblCategoria.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCategoria.setBounds(190, 95, 120, 25);
        formPanel.add(lblCategoria);
        
        txtIdCategoria = new JTextField();
        txtIdCategoria.setBounds(190, 120, 150, 35);
        txtIdCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtIdCategoria);
        
        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDescripcion.setBounds(360, 95, 100, 25);
        formPanel.add(lblDescripcion);
        
        txtEstado = new JTextField();
        txtEstado.setBounds(360, 120, 300, 35);
        txtEstado.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtEstado);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BG_COLOR);
        buttonPanel.setBounds(30, 280, 890, 50);
        buttonPanel.setLayout(null);
        mainContent.add(buttonPanel);
        
        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(0, 0, 140, 40);
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAgregar.setBackground(PRIMARY_COLOR);
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFocusPainted(false);
        btnAgregar.addActionListener(e -> agregarProducto());
        buttonPanel.add(btnAgregar);
        
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(160, 0, 140, 40);
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnActualizar.setBackground(ACCENT_COLOR);
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.addActionListener(e -> actualizarProducto());
        buttonPanel.add(btnActualizar);
        
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(320, 0, 140, 40);
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEliminar.setBackground(new Color(239, 68, 68));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.addActionListener(e -> eliminarProducto());
        buttonPanel.add(btnEliminar);
        
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(480, 0, 140, 40);
        btnLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLimpiar.setBackground(new Color(100, 116, 139));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.addActionListener(e -> limpiarCamposProducto());
        buttonPanel.add(btnLimpiar);
        
        // Tabla de productos
        modeloProductos = new DefaultTableModel();
        modeloProductos.addColumn("ID");
        modeloProductos.addColumn("Nombre");
        modeloProductos.addColumn("Precio");
        modeloProductos.addColumn("Stock");
        modeloProductos.addColumn("Categoría");
        modeloProductos.addColumn("Descripción");
        
        tablaProductos = new JTable(modeloProductos);
        tablaProductos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaProductos.setRowHeight(30);
        tablaProductos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaProductos.getTableHeader().setBackground(new Color(16, 185, 129));
        tablaProductos.getTableHeader().setForeground(Color.WHITE);
        
        // Listener para seleccionar fila Y sincronizar con ventas
        tablaProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaProductos.getSelectedRow();
                if (fila >= 0) {
                    // Guardar índice de fila seleccionada
                    productoFilaSeleccionada = fila;
                    
                    // Cargar datos en los campos
                    txtIdProducto.setText(tablaProductos.getValueAt(fila, 0).toString());
                    txtNombreProducto.setText(tablaProductos.getValueAt(fila, 1).toString());
                    txtPrecio.setText(tablaProductos.getValueAt(fila, 2).toString());
                    txtStock.setText(tablaProductos.getValueAt(fila, 3).toString());
                    txtIdCategoria.setText(tablaProductos.getValueAt(fila, 4).toString());
                    txtEstado.setText(tablaProductos.getValueAt(fila, 5).toString());
                    
                    // ✨ SINCRONIZAR CON VENTAS: Agregar producto a tabla de ventas
                    if (modeloProductosDisponibles != null) {
                        try {
                            int idProducto = Integer.parseInt(tablaProductos.getValueAt(fila, 0).toString());
                            String nombre = tablaProductos.getValueAt(fila, 1).toString();
                            double precio = Double.parseDouble(tablaProductos.getValueAt(fila, 2).toString());
                            int stock = Integer.parseInt(tablaProductos.getValueAt(fila, 3).toString());
                            
                            // Verificar si ya existe en ventas (buscar por nombre)
                            boolean existe = false;
                            for (int i = 0; i < modeloProductosDisponibles.getRowCount(); i++) {
                                String nombreVenta = modeloProductosDisponibles.getValueAt(i, 1).toString();
                                if (nombreVenta.equals(nombre)) {
                                    existe = true;
                                    break;
                                }
                            }
                            
                            // Solo agregar si no existe
                            if (!existe && stock > 0) {
                                Vector<String> filaVenta = new Vector<>();
                                filaVenta.add(String.valueOf(nextIdVentaProducto)); // ID diferente
                                filaVenta.add(nombre);
                                filaVenta.add(String.format("%.2f", precio));
                                filaVenta.add(String.valueOf(stock));
                                modeloProductosDisponibles.addRow(filaVenta);
                                
                                System.out.println("✅ Producto '" + nombre + "' agregado a Ventas (ID Venta:" + nextIdVentaProducto + ", ID Producto:" + idProducto + ")");
                                nextIdVentaProducto++;
                            }
                        } catch (Exception ex) {
                            System.err.println("Error sincronizando producto seleccionado: " + ex.getMessage());
                        }
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        scrollPane.setBounds(30, 350, 890, 310);
        scrollPane.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        mainContent.add(scrollPane);
        
        // Cargar datos guardados
        cargarProductos();
        
        // ✨ RESTAURAR SELECCIÓN PRESERVADA
        SwingUtilities.invokeLater(() -> {
            if (productoFilaSeleccionada >= 0 && productoFilaSeleccionada < tablaProductos.getRowCount()) {
                tablaProductos.setRowSelectionInterval(productoFilaSeleccionada, productoFilaSeleccionada);
                tablaProductos.scrollRectToVisible(tablaProductos.getCellRect(productoFilaSeleccionada, 0, true));
                System.out.println("✅ Selección de producto restaurada: fila " + productoFilaSeleccionada);
            }
        });
        
        mainContent.revalidate();
        mainContent.repaint();
    }
    
    private void cargarDatosEjemploProductos() {
        modeloProductos.addRow(new Object[]{"1", "Laptop Dell XPS 15", "1299.99", "15", "1", "Laptop de alto rendimiento con pantalla 4K"});
        modeloProductos.addRow(new Object[]{"2", "Mouse Logitech MX Master", "99.99", "50", "2", "Mouse ergonómico inalámbrico"});
        modeloProductos.addRow(new Object[]{"3", "Teclado Mecánico Corsair", "149.99", "30", "2", "Teclado mecánico RGB retroiluminado"});
        modeloProductos.addRow(new Object[]{"4", "Monitor Samsung 27\"", "349.99", "8", "3", "Monitor curvo Full HD 144Hz"});
        modeloProductos.addRow(new Object[]{"5", "Auriculares Sony WH-1000XM4", "279.99", "12", "4", "Auriculares con cancelación de ruido"});
        modeloProductos.addRow(new Object[]{"6", "SSD Samsung 1TB", "89.99", "100", "5", "Disco sólido NVMe de alta velocidad"});
        nextIdProducto = 7; // Siguiente ID disponible
    }
    
    private void agregarProducto() {
        if (validarCamposProducto()) {
            try {
                String nombre = txtNombreProducto.getText();
                double precio = Double.parseDouble(txtPrecio.getText());
                int stock = Integer.parseInt(txtStock.getText());
                String idCategoriaStr = txtIdCategoria.getText().trim();
                Integer idCategoria = idCategoriaStr.isEmpty() ? null : Integer.parseInt(idCategoriaStr);
                String descripcion = txtEstado.getText();
                
                // Insertar en la base de datos
                Connection conn = Conexion.getInstancia().getConnection();
                String sql = "INSERT INTO productos (nombre, precio, stock, id_categoria, descripcion) VALUES (?, ?, ?, ?, ?)";
                java.sql.PreparedStatement pst = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
                pst.setString(1, nombre);
                pst.setDouble(2, precio);
                pst.setInt(3, stock);
                if (idCategoria != null) {
                    pst.setInt(4, idCategoria);
                } else {
                    pst.setNull(4, java.sql.Types.INTEGER);
                }
                pst.setString(5, descripcion);
                
                int filasAfectadas = pst.executeUpdate();
                
                if (filasAfectadas > 0) {
                    // Obtener el ID generado
                    java.sql.ResultSet rs = pst.getGeneratedKeys();
                    int nuevoId = 0;
                    if (rs.next()) {
                        nuevoId = rs.getInt(1);
                    }
                    
                    // Agregar a la tabla visual
                    Vector<String> fila = new Vector<>();
                    fila.add(String.valueOf(nuevoId));
                    fila.add(nombre);
                    fila.add(String.format("%.2f", precio));
                    fila.add(String.valueOf(stock));
                    fila.add(idCategoriaStr);
                    fila.add(descripcion);
                    modeloProductos.addRow(fila);
                    
                    // 🔄 SINCRONIZACIÓN AUTOMÁTICA: Agregar a ventas
                    sincronizarProductoEnOtrasTablas(nuevoId, nombre, precio, stock);
                    
                    limpiarCamposProducto();
                    JOptionPane.showMessageDialog(frame, 
                        "Producto agregado exitosamente con ID: " + nuevoId + "\n✅ Guardado en base de datos", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
                
                pst.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, 
                    "Error al agregar producto: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void actualizarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila >= 0) {
            if (validarCamposProducto()) {
                try {
                    int id = Integer.parseInt(txtIdProducto.getText());
                    String nombre = txtNombreProducto.getText();
                    double precio = Double.parseDouble(txtPrecio.getText());
                    int stock = Integer.parseInt(txtStock.getText());
                    String idCategoriaStr = txtIdCategoria.getText().trim();
                    Integer idCategoria = idCategoriaStr.isEmpty() ? null : Integer.parseInt(idCategoriaStr);
                    String descripcion = txtEstado.getText();
                    
                    // Actualizar en la base de datos
                    Connection conn = Conexion.getInstancia().getConnection();
                    String sql = "UPDATE productos SET nombre=?, precio=?, stock=?, id_categoria=?, descripcion=? WHERE id=?";
                    java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, nombre);
                    pst.setDouble(2, precio);
                    pst.setInt(3, stock);
                    if (idCategoria != null) {
                        pst.setInt(4, idCategoria);
                    } else {
                        pst.setNull(4, java.sql.Types.INTEGER);
                    }
                    pst.setString(5, descripcion);
                    pst.setInt(6, id);
                    
                    int filasAfectadas = pst.executeUpdate();
                    
                    if (filasAfectadas > 0) {
                        // Actualizar en la tabla visual
                        modeloProductos.setValueAt(String.valueOf(id), fila, 0);
                        modeloProductos.setValueAt(nombre, fila, 1);
                        modeloProductos.setValueAt(String.format("%.2f", precio), fila, 2);
                        modeloProductos.setValueAt(String.valueOf(stock), fila, 3);
                        modeloProductos.setValueAt(idCategoriaStr, fila, 4);
                        modeloProductos.setValueAt(descripcion, fila, 5);
                        
                        limpiarCamposProducto();
                        JOptionPane.showMessageDialog(frame, "Producto actualizado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                    pst.close();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, 
                        "Error al actualizar producto: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Seleccione un producto de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void eliminarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila >= 0) {
            int confirmacion = JOptionPane.showConfirmDialog(frame, 
                "¿Está seguro de eliminar este producto?", 
                "Confirmar eliminación", 
                JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    int id = Integer.parseInt(tablaProductos.getValueAt(fila, 0).toString());
                    
                    // Eliminar de la base de datos
                    Connection conn = Conexion.getInstancia().getConnection();
                    String sql = "DELETE FROM productos WHERE id=?";
                    java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setInt(1, id);
                    
                    int filasAfectadas = pst.executeUpdate();
                    
                    if (filasAfectadas > 0) {
                        // Eliminar de la tabla visual
                        modeloProductos.removeRow(fila);
                        limpiarCamposProducto();
                        JOptionPane.showMessageDialog(frame, "Producto eliminado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                    pst.close();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, 
                        "Error al eliminar producto: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Seleccione un producto de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void limpiarCamposProducto() {
        txtIdProducto.setText("");
        txtNombreProducto.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        txtIdCategoria.setText("");
        txtEstado.setText("");
        tablaProductos.clearSelection();
    }
    
    private boolean validarCamposProducto() {
        if (txtNombreProducto.getText().trim().isEmpty() || 
            txtPrecio.getText().trim().isEmpty() || 
            txtStock.getText().trim().isEmpty() || 
            txtIdCategoria.getText().trim().isEmpty() || 
            txtEstado.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validar que precio sea un número decimal
        try {
            Double.parseDouble(txtPrecio.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "El precio debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validar que stock e id_categoria sean números enteros
        try {
            Integer.parseInt(txtStock.getText());
            Integer.parseInt(txtIdCategoria.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Stock y Categoría deben ser números enteros", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    
    // ==================== PANEL DE CLIENTES ====================
    
    private void mostrarPanelClientes() {
        mainContent.removeAll();
        
        // Header
        JLabel lblHeader = new JLabel("Gestión de Clientes");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblHeader.setForeground(TEXT_DARK);
        lblHeader.setBounds(30, 20, 300, 40);
        mainContent.add(lblHeader);
        
        // Botón Cerrar
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(820, 20, 100, 35);
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setBackground(new Color(239, 68, 68));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> frame.dispose());
        mainContent.add(btnCerrar);
        
        // === LOGO DE TIENDA (lado derecho, arriba) ===
        JPanel panelLogoTienda = new JPanel();
        panelLogoTienda.setBackground(CARD_COLOR);
        panelLogoTienda.setBounds(950, 60, 160, 110);
        panelLogoTienda.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        panelLogoTienda.setLayout(null);
        mainContent.add(panelLogoTienda);
        
        JLabel lblLogoTienda = new JLabel("Imagen", SwingConstants.CENTER);
        lblLogoTienda.setBounds(5, 5, 150, 100);
        lblLogoTienda.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLogoTienda.setForeground(TEXT_LIGHT);
        lblLogoTienda.setBorder(BorderFactory.createDashedBorder(Color.LIGHT_GRAY, 2, 5));
        if (rutaImagenTiendaGuardada != null && !rutaImagenTiendaGuardada.isEmpty()) {
            try {
                ImageIcon iconoOriginal = new ImageIcon(rutaImagenTiendaGuardada);
                Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(140, 90, Image.SCALE_SMOOTH);
                lblLogoTienda.setIcon(new ImageIcon(imagenEscalada));
                lblLogoTienda.setText("");
            } catch (Exception ex) { lblLogoTienda.setText("Error"); }
        }
        panelLogoTienda.add(lblLogoTienda);
        
        // === NOMBRE DE TIENDA ===
        JLabel lblNombreTiendaDisplay = new JLabel("", SwingConstants.CENTER);
        lblNombreTiendaDisplay.setBounds(950, 175, 160, 25);
        lblNombreTiendaDisplay.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNombreTiendaDisplay.setForeground(TEXT_DARK);
        if (nombreTiendaGuardado != null && !nombreTiendaGuardado.isEmpty()) {
            lblNombreTiendaDisplay.setText(nombreTiendaGuardado);
        }
        mainContent.add(lblNombreTiendaDisplay);
        
        // Buscador
        JLabel lblBuscar = new JLabel("Buscar Cliente:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBuscar.setForeground(TEXT_DARK);
        lblBuscar.setBounds(30, 80, 120, 25);
        mainContent.add(lblBuscar);
        
        JTextField txtBuscarCliente = new JTextField();
        txtBuscarCliente.setBounds(150, 80, 300, 30);
        txtBuscarCliente.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainContent.add(txtBuscarCliente);
        
        // Formulario
        JLabel lblId = new JLabel("ID:");
        lblId.setBounds(30, 130, 100, 25);
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainContent.add(lblId);
        
        txtIdCliente = new JTextField();
        txtIdCliente.setBounds(140, 130, 200, 30);
        txtIdCliente.setEditable(false);
        mainContent.add(txtIdCliente);
        
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(30, 170, 100, 25);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainContent.add(lblNombre);
        
        txtNombreCliente = new JTextField();
        txtNombreCliente.setBounds(140, 170, 200, 30);
        mainContent.add(txtNombreCliente);
        
        JLabel lblApellido = new JLabel("Apellido:");
        lblApellido.setBounds(370, 170, 100, 25);
        lblApellido.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainContent.add(lblApellido);
        
        txtApellidoCliente = new JTextField();
        txtApellidoCliente.setBounds(480, 170, 200, 30);
        mainContent.add(txtApellidoCliente);
        
        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setBounds(30, 210, 100, 25);
        lblTelefono.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainContent.add(lblTelefono);
        
        txtTelefonoCliente = new JTextField();
        txtTelefonoCliente.setBounds(140, 210, 200, 30);
        mainContent.add(txtTelefonoCliente);
        
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(370, 210, 100, 25);
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainContent.add(lblEmail);
        
        txtEmailCliente = new JTextField();
        txtEmailCliente.setBounds(480, 210, 200, 30);
        mainContent.add(txtEmailCliente);
        
        JLabel lblDireccion = new JLabel("Dirección:");
        lblDireccion.setBounds(30, 250, 100, 25);
        lblDireccion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainContent.add(lblDireccion);
        
        txtDireccionCliente = new JTextField();
        txtDireccionCliente.setBounds(140, 250, 540, 30);
        mainContent.add(txtDireccionCliente);
        
        JLabel lblCiudad = new JLabel("Ciudad:");
        lblCiudad.setBounds(30, 290, 100, 25);
        lblCiudad.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainContent.add(lblCiudad);
        
        txtCiudadCliente = new JTextField();
        txtCiudadCliente.setBounds(140, 290, 200, 30);
        mainContent.add(txtCiudadCliente);
        
        // Botones CRUD
        JButton btnAgregarCliente = new JButton("Agregar");
        btnAgregarCliente.setBounds(720, 130, 180, 40);
        btnAgregarCliente.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAgregarCliente.setBackground(new Color(16, 185, 129));
        btnAgregarCliente.setForeground(Color.WHITE);
        btnAgregarCliente.setFocusPainted(false);
        btnAgregarCliente.addActionListener(e -> agregarCliente());
        mainContent.add(btnAgregarCliente);
        
        JButton btnActualizarCliente = new JButton("Actualizar");
        btnActualizarCliente.setBounds(720, 180, 180, 40);
        btnActualizarCliente.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnActualizarCliente.setBackground(new Color(59, 130, 246));
        btnActualizarCliente.setForeground(Color.WHITE);
        btnActualizarCliente.setFocusPainted(false);
        btnActualizarCliente.addActionListener(e -> actualizarCliente());
        mainContent.add(btnActualizarCliente);
        
        JButton btnEliminarCliente = new JButton("Eliminar");
        btnEliminarCliente.setBounds(720, 230, 180, 40);
        btnEliminarCliente.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEliminarCliente.setBackground(new Color(239, 68, 68));
        btnEliminarCliente.setForeground(Color.WHITE);
        btnEliminarCliente.setFocusPainted(false);
        btnEliminarCliente.addActionListener(e -> eliminarCliente());
        mainContent.add(btnEliminarCliente);
        
        JButton btnLimpiarCliente = new JButton("Limpiar");
        btnLimpiarCliente.setBounds(720, 280, 180, 40);
        btnLimpiarCliente.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLimpiarCliente.setBackground(new Color(100, 116, 139));
        btnLimpiarCliente.setForeground(Color.WHITE);
        btnLimpiarCliente.setFocusPainted(false);
        btnLimpiarCliente.addActionListener(e -> limpiarCamposCliente());
        mainContent.add(btnLimpiarCliente);
        
        // Tabla de clientes - SOLO crear modelo si no existe (preservar datos sincronizados)
        if (modeloClientes == null) {
            modeloClientes = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Apellido", "Teléfono", "Email", "Dirección", "Ciudad"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
        }
        
        tablaClientes = new JTable(modeloClientes);
        tablaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaClientes.setRowHeight(30);
        tablaClientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaClientes.getTableHeader().setBackground(new Color(59, 130, 246));
        tablaClientes.getTableHeader().setForeground(Color.WHITE);
        
        // Listener para seleccionar fila
        tablaClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaClientes.getSelectedRow();
                if (fila >= 0) {
                    txtIdCliente.setText(tablaClientes.getValueAt(fila, 0).toString());
                    txtNombreCliente.setText(tablaClientes.getValueAt(fila, 1).toString());
                    txtApellidoCliente.setText(tablaClientes.getValueAt(fila, 2).toString());
                    txtTelefonoCliente.setText(tablaClientes.getValueAt(fila, 3).toString());
                    txtEmailCliente.setText(tablaClientes.getValueAt(fila, 4).toString());
                    txtDireccionCliente.setText(tablaClientes.getValueAt(fila, 5).toString());
                    txtCiudadCliente.setText(tablaClientes.getValueAt(fila, 6).toString());
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        scrollPane.setBounds(30, 340, 890, 320);
        scrollPane.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        mainContent.add(scrollPane);
        
        // Solo cargar de BD si el modelo está vacío (preservar filas sincronizadas)
        // Si ya tiene datos de sincronización, no cargar de BD
        // cargarClientes();  // COMENTADO para preservar sincronización
        
        // Configurar filtro de búsqueda
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloClientes);
        tablaClientes.setRowSorter(sorter);
        
        txtBuscarCliente.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filtrar();
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filtrar();
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filtrar();
            }
            
            private void filtrar() {
                String texto = txtBuscarCliente.getText().trim();
                if (texto.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
                }
            }
        });
        
        mainContent.revalidate();
        mainContent.repaint();
    }
    
    // ==================== MÉTODOS CRUD PARA CLIENTES ====================
    
    private void agregarCliente() {
        // Validar que los campos necesarios no estén vacíos
        if (txtNombreCliente.getText().trim().isEmpty() || 
            txtApellidoCliente.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "Nombre y Apellido son campos obligatorios", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Generar nuevo ID
        int nuevoId = nextIdCliente++;
        
        // Agregar fila a la tabla
        Vector<String> fila = new Vector<>();
        fila.add(String.valueOf(nuevoId));
        fila.add(txtNombreCliente.getText().trim());
        fila.add(txtApellidoCliente.getText().trim());
        fila.add(txtTelefonoCliente.getText().trim());
        fila.add(txtEmailCliente.getText().trim());
        fila.add(txtDireccionCliente.getText().trim());
        fila.add(txtCiudadCliente.getText().trim());
        modeloClientes.addRow(fila);
        
        // Limpiar campos después de agregar
        limpiarCamposCliente();
        
        JOptionPane.showMessageDialog(frame, 
            "Cliente agregado exitosamente con ID: " + nuevoId, 
            "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void actualizarCliente() {
        int filaSeleccionada = tablaClientes.getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(frame, 
                "Seleccione un cliente de la tabla para actualizar", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validar campos obligatorios
        if (txtNombreCliente.getText().trim().isEmpty() || 
            txtApellidoCliente.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "Nombre y Apellido son campos obligatorios", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Actualizar fila en la tabla
        modeloClientes.setValueAt(txtNombreCliente.getText().trim(), filaSeleccionada, 1);
        modeloClientes.setValueAt(txtApellidoCliente.getText().trim(), filaSeleccionada, 2);
        modeloClientes.setValueAt(txtTelefonoCliente.getText().trim(), filaSeleccionada, 3);
        modeloClientes.setValueAt(txtEmailCliente.getText().trim(), filaSeleccionada, 4);
        modeloClientes.setValueAt(txtDireccionCliente.getText().trim(), filaSeleccionada, 5);
        modeloClientes.setValueAt(txtCiudadCliente.getText().trim(), filaSeleccionada, 6);
        
        JOptionPane.showMessageDialog(frame, 
            "Cliente actualizado exitosamente", 
            "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void eliminarCliente() {
        int filaSeleccionada = tablaClientes.getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(frame, 
                "Seleccione un cliente de la tabla para eliminar", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(frame, 
            "¿Está seguro de eliminar este cliente?", 
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            modeloClientes.removeRow(filaSeleccionada);
            limpiarCamposCliente();
            JOptionPane.showMessageDialog(frame, 
                "Cliente eliminado exitosamente", 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void limpiarCamposCliente() {
        txtIdCliente.setText("");
        txtNombreCliente.setText("");
        txtApellidoCliente.setText("");
        txtTelefonoCliente.setText("");
        txtEmailCliente.setText("");
        txtDireccionCliente.setText("");
        txtCiudadCliente.setText("");
        tablaClientes.clearSelection();
    }
    
    // ==================== PANEL DE CLIENTES ====================
    
    private void mostrarPanelClientes() {
        mainContent.removeAll();
        
        // Header
        JLabel lblHeader = new JLabel("Gestión de Clientes");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblHeader.setForeground(TEXT_DARK);
        lblHeader.setBounds(30, 20, 300, 40);
        mainContent.add(lblHeader);
        
        // Botón Cerrar
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(820, 20, 100, 35);
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setBackground(new Color(239, 68, 68));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> frame.dispose());
        mainContent.add(btnCerrar);
        
        // === LOGO DE TIENDA (lado derecho, arriba) ===
        JPanel panelLogoTienda = new JPanel();
        panelLogoTienda.setBackground(CARD_COLOR);
        panelLogoTienda.setBounds(1050, 70, 200, 160);
        panelLogoTienda.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        panelLogoTienda.setLayout(null);
        mainContent.add(panelLogoTienda);
        
        JLabel lblLogoTienda = new JLabel("Imagen", SwingConstants.CENTER);
        lblLogoTienda.setBounds(5, 5, 190, 150);
        lblLogoTienda.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLogoTienda.setForeground(TEXT_LIGHT);
        lblLogoTienda.setBorder(BorderFactory.createDashedBorder(Color.LIGHT_GRAY, 2, 5));
        if (rutaImagenTiendaGuardada != null && !rutaImagenTiendaGuardada.isEmpty()) {
            try {
                ImageIcon iconoOriginal = new ImageIcon(rutaImagenTiendaGuardada);
                Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(180, 140, Image.SCALE_SMOOTH);
                lblLogoTienda.setIcon(new ImageIcon(imagenEscalada));
                lblLogoTienda.setText("");
            } catch (Exception ex) { lblLogoTienda.setText("Error"); }
        }
        panelLogoTienda.add(lblLogoTienda);
        
        // === NOMBRE DE TIENDA ===
        JLabel lblNombreTiendaDisplay = new JLabel("", SwingConstants.CENTER);
        lblNombreTiendaDisplay.setBounds(1050, 240, 200, 40);
        lblNombreTiendaDisplay.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNombreTiendaDisplay.setForeground(TEXT_DARK);
        if (nombreTiendaGuardado != null && !nombreTiendaGuardado.isEmpty()) {
            lblNombreTiendaDisplay.setText(nombreTiendaGuardado);
        }
        mainContent.add(lblNombreTiendaDisplay);
        
        // Panel de formulario
        JPanel formPanel = new JPanel();
        modeloProductosDisponibles = new DefaultTableModel();
        modeloProductosDisponibles.addColumn("ID");
        modeloProductosDisponibles.addColumn("Nombre");
        modeloProductosDisponibles.addColumn("Precio");
        modeloProductosDisponibles.addColumn("Stock");
        
        tablaProductosDisponibles = new JTable(modeloProductosDisponibles);
        tablaProductosDisponibles.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaProductosDisponibles.setRowHeight(30);
        tablaProductosDisponibles.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaProductosDisponibles.getTableHeader().setBackground(new Color(16, 185, 129));
        tablaProductosDisponibles.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollProductos = new JScrollPane(tablaProductosDisponibles);
        scrollProductos.setBounds(30, 120, 420, 400);
        scrollProductos.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        mainContent.add(scrollProductos);
        
        // Botones de acción en el medio
        JButton btnAgregarCarrito = new JButton("Agregar ►");
        btnAgregarCarrito.setBounds(470, 250, 120, 45);
        btnAgregarCarrito.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAgregarCarrito.setBackground(PRIMARY_COLOR);
        btnAgregarCarrito.setForeground(Color.WHITE);
        btnAgregarCarrito.setFocusPainted(false);
        btnAgregarCarrito.addActionListener(e -> agregarAlCarrito());
        mainContent.add(btnAgregarCarrito);
        
        JButton btnQuitarCarrito = new JButton("◄ Quitar");
        btnQuitarCarrito.setBounds(470, 310, 120, 45);
        btnQuitarCarrito.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnQuitarCarrito.setBackground(new Color(239, 68, 68));
        btnQuitarCarrito.setForeground(Color.WHITE);
        btnQuitarCarrito.setFocusPainted(false);
        btnQuitarCarrito.addActionListener(e -> quitarDelCarrito());
        mainContent.add(btnQuitarCarrito);
        
        // Panel derecho - Carrito de Compra
        JLabel lblCarrito = new JLabel("Carrito de Compra");
        lblCarrito.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblCarrito.setForeground(TEXT_DARK);
        lblCarrito.setBounds(610, 80, 250, 30);
        mainContent.add(lblCarrito);
        
        // Tabla del carrito
        modeloCarrito = new DefaultTableModel();
        modeloCarrito.addColumn("ID");
        modeloCarrito.addColumn("Producto");
        modeloCarrito.addColumn("Precio");
        modeloCarrito.addColumn("Cant.");
        modeloCarrito.addColumn("Subtotal");
        
        tablaCarrito = new JTable(modeloCarrito);
        tablaCarrito.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaCarrito.setRowHeight(30);
        tablaCarrito.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaCarrito.getTableHeader().setBackground(ACCENT_COLOR);
        tablaCarrito.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollCarrito = new JScrollPane(tablaCarrito);
        scrollCarrito.setBounds(610, 120, 310, 340);
        scrollCarrito.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        mainContent.add(scrollCarrito);
        
        // Panel de total y finalizar venta
        JPanel panelTotal = new JPanel();
        panelTotal.setBackground(CARD_COLOR);
        panelTotal.setBounds(610, 480, 310, 120);
        panelTotal.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        panelTotal.setLayout(null);
        mainContent.add(panelTotal);
        
        JLabel lblTotalTexto = new JLabel("TOTAL:");
        lblTotalTexto.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotalTexto.setForeground(TEXT_DARK);
        lblTotalTexto.setBounds(20, 15, 100, 30);
        panelTotal.add(lblTotalTexto);
        
        lblTotal = new JLabel("$0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTotal.setForeground(PRIMARY_COLOR);
        lblTotal.setBounds(150, 10, 150, 40);
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        panelTotal.add(lblTotal);
        
        JButton btnFinalizarVenta = new JButton("Finalizar Venta");
        btnFinalizarVenta.setBounds(20, 65, 270, 40);
        btnFinalizarVenta.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnFinalizarVenta.setBackground(new Color(16, 185, 129));
        btnFinalizarVenta.setForeground(Color.WHITE);
        btnFinalizarVenta.setFocusPainted(false);
        btnFinalizarVenta.addActionListener(e -> finalizarVenta());
        panelTotal.add(btnFinalizarVenta);
        
        // Botón para limpiar carrito
        JButton btnLimpiarCarrito = new JButton("Vaciar Carrito");
        btnLimpiarCarrito.setBounds(30, 540, 150, 40);
        btnLimpiarCarrito.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLimpiarCarrito.setBackground(new Color(100, 116, 139));
        btnLimpiarCarrito.setForeground(Color.WHITE);
        btnLimpiarCarrito.setFocusPainted(false);
        btnLimpiarCarrito.addActionListener(e -> vaciarCarrito());
        mainContent.add(btnLimpiarCarrito);
        
        // Cargar productos disponibles
        cargarProductosDisponibles();
        
        mainContent.revalidate();
        mainContent.repaint();
    }
    
    private void cargarProductosDisponibles() {
        // Cargar productos de ejemplo - DESACTIVADO para que la tabla inicie vacía
        // modeloProductosDisponibles.addRow(new Object[]{"1", "Laptop Dell XPS 15", "1299.99", "15"});
        // modeloProductosDisponibles.addRow(new Object[]{"2", "Mouse Logitech MX Master", "99.99", "50"});
        // modeloProductosDisponibles.addRow(new Object[]{"3", "Teclado Mecánico Corsair", "149.99", "30"});
        // modeloProductosDisponibles.addRow(new Object[]{"4", "Monitor Samsung 27\"", "349.99", "8"});
        // modeloProductosDisponibles.addRow(new Object[]{"5", "Auriculares Sony WH-1000XM4", "279.99", "5"});
        // modeloProductosDisponibles.addRow(new Object[]{"6", "SSD Samsung 1TB", "89.99", "100"});
        // modeloProductosDisponibles.addRow(new Object[]{"7", "Webcam Logitech C920", "79.99", "25"});
        // modeloProductosDisponibles.addRow(new Object[]{"8", "Hub USB-C", "49.99", "40"});
    }
    
    private void agregarAlCarrito() {
        int fila = tablaProductosDisponibles.getSelectedRow();
        if (fila >= 0) {
            String id = tablaProductosDisponibles.getValueAt(fila, 0).toString();
            String nombre = tablaProductosDisponibles.getValueAt(fila, 1).toString();
            String precioStr = tablaProductosDisponibles.getValueAt(fila, 2).toString();
            int stockDisponible = Integer.parseInt(tablaProductosDisponibles.getValueAt(fila, 3).toString());
            
            if (stockDisponible <= 0) {
                JOptionPane.showMessageDialog(frame, "Producto sin stock disponible", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double precio = Double.parseDouble(precioStr);
            
            // Verificar si el producto ya está en el carrito
            boolean encontrado = false;
            for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
                if (modeloCarrito.getValueAt(i, 0).toString().equals(id)) {
                    // Incrementar cantidad
                    int cantActual = Integer.parseInt(modeloCarrito.getValueAt(i, 3).toString());
                    if (cantActual < stockDisponible) {
                        int nuevaCant = cantActual + 1;
                        double subtotal = precio * nuevaCant;
                        modeloCarrito.setValueAt(nuevaCant, i, 3);
                        modeloCarrito.setValueAt(String.format("%.2f", subtotal), i, 4);
                        encontrado = true;
                    } else {
                        JOptionPane.showMessageDialog(frame, "No hay más stock disponible", "Advertencia", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    break;
                }
            }
            
            if (!encontrado) {
                // Agregar nuevo producto al carrito
                double subtotal = precio * 1;
                modeloCarrito.addRow(new Object[]{id, nombre, precioStr, "1", String.format("%.2f", subtotal)});
            }
            
            calcularTotal();
        } else {
            JOptionPane.showMessageDialog(frame, "Seleccione un producto de la lista", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void quitarDelCarrito() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila >= 0) {
            String id = tablaCarrito.getValueAt(fila, 0).toString();
            int cantidad = Integer.parseInt(tablaCarrito.getValueAt(fila, 3).toString());
            
            if (cantidad > 1) {
                // Decrementar cantidad
                int nuevaCant = cantidad - 1;
                double precio = Double.parseDouble(tablaCarrito.getValueAt(fila, 2).toString());
                double subtotal = precio * nuevaCant;
                modeloCarrito.setValueAt(nuevaCant, fila, 3);
                modeloCarrito.setValueAt(String.format("%.2f", subtotal), fila, 4);
            } else {
                // Eliminar del carrito
                modeloCarrito.removeRow(fila);
            }
            
            calcularTotal();
        } else {
            JOptionPane.showMessageDialog(frame, "Seleccione un producto del carrito", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void vaciarCarrito() {
        if (modeloCarrito.getRowCount() > 0) {
            int confirmacion = JOptionPane.showConfirmDialog(frame, 
                "¿Está seguro de vaciar el carrito?", 
                "Confirmar", 
                JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                modeloCarrito.setRowCount(0);
                calcularTotal();
            }
        }
    }
    
    private void calcularTotal() {
        totalVenta = 0.0;
        for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
            double subtotal = Double.parseDouble(modeloCarrito.getValueAt(i, 4).toString());
            totalVenta += subtotal;
            String idCarrito = modeloCarrito.getValueAt(i, 0).toString();
            int cantidadVendida = Integer.parseInt(modeloCarrito.getValueAt(i, 3).toString());
            
            // Buscar el producto en la tabla de disponibles y actualizar stock
            for (int j = 0; j < modeloProductosDisponibles.getRowCount(); j++) {
                String idDisp = modeloProductosDisponibles.getValueAt(j, 0).toString();
                if (idDisp.equals(idCarrito)) {
                    int stockActual = Integer.parseInt(modeloProductosDisponibles.getValueAt(j, 3).toString());
                    int nuevoStock = stockActual - cantidadVendida;
                    modeloProductosDisponibles.setValueAt(String.valueOf(nuevoStock), j, 3);
                    break;
                }
            }
        }
    }
    // ==================== PANEL DE CLIENTES ====================
    
    private void mostrarPanelClientes() {
        mainContent.removeAll();
        
        // Header
        JLabel lblHeader = new JLabel("Gestión de Clientes");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblHeader.setForeground(TEXT_DARK);
        lblHeader.setBounds(30, 20, 400, 40);
        mainContent.add(lblHeader);
        
        // Botón Cerrar
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(820, 20, 100, 35);
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setBackground(new Color(239, 68, 68));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> frame.dispose());
        mainContent.add(btnCerrar);
        
        // Panel de formulario
        JPanel formPanel = new JPanel();
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBounds(30, 80, 890, 180);
        formPanel.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        formPanel.setLayout(null);
        mainContent.add(formPanel);
        
        // Fila 1: ID, Nombre, Apellido, Teléfono
        JLabel lblId = new JLabel("ID Cliente:");
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblId.setBounds(20, 20, 100, 25);
        formPanel.add(lblId);
        
        txtIdCliente = new JTextField();
        txtIdCliente.setBounds(20, 45, 100, 35);
        txtIdCliente.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtIdCliente.setEditable(false);
        txtIdCliente.setBackground(new Color(240, 240, 240));
        formPanel.add(txtIdCliente);
        
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombre.setBounds(140, 20, 100, 25);
        formPanel.add(lblNombre);
        
        txtNombreCliente = new JTextField();
        txtNombreCliente.setBounds(140, 45, 200, 35);
        txtNombreCliente.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtNombreCliente);
        
        JLabel lblApellido = new JLabel("Apellido:");
        lblApellido.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblApellido.setBounds(360, 20, 100, 25);
        formPanel.add(lblApellido);
        
        txtApellidoCliente = new JTextField();
        txtApellidoCliente.setBounds(360, 45, 200, 35);
        txtApellidoCliente.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtApellidoCliente);
        
        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTelefono.setBounds(580, 20, 100, 25);
        formPanel.add(lblTelefono);
        
        txtTelefonoCliente = new JTextField();
        txtTelefonoCliente.setBounds(580, 45, 150, 35);
        txtTelefonoCliente.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtTelefonoCliente);
        
        // Fila 2: Email, Dirección, Ciudad
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEmail.setBounds(20, 95, 100, 25);
        formPanel.add(lblEmail);
        
        txtEmailCliente = new JTextField();
        txtEmailCliente.setBounds(20, 120, 250, 35);
        txtEmailCliente.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtEmailCliente);
        
        JLabel lblDireccion = new JLabel("Dirección:");
        lblDireccion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDireccion.setBounds(290, 95, 100, 25);
        formPanel.add(lblDireccion);
        
        txtDireccionCliente = new JTextField();
        txtDireccionCliente.setBounds(290, 120, 280, 35);
        txtDireccionCliente.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtDireccionCliente);
        
        JLabel lblCiudad = new JLabel("Ciudad:");
        lblCiudad.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCiudad.setBounds(590, 95, 100, 25);
        formPanel.add(lblCiudad);
        
        txtCiudadCliente = new JTextField();
        txtCiudadCliente.setBounds(590, 120, 140, 35);
        txtCiudadCliente.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtCiudadCliente);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BG_COLOR);
        buttonPanel.setBounds(30, 280, 890, 50);
        buttonPanel.setLayout(null);
        mainContent.add(buttonPanel);
        
        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(0, 0, 140, 40);
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAgregar.setBackground(PRIMARY_COLOR);
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFocusPainted(false);
        btnAgregar.addActionListener(e -> agregarCliente());
        buttonPanel.add(btnAgregar);
        
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(160, 0, 140, 40);
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnActualizar.setBackground(ACCENT_COLOR);
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.addActionListener(e -> actualizarCliente());
        buttonPanel.add(btnActualizar);
        
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(320, 0, 140, 40);
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEliminar.setBackground(new Color(239, 68, 68));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.addActionListener(e -> eliminarCliente());
        buttonPanel.add(btnEliminar);
        
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(480, 0, 140, 40);
        btnLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLimpiar.setBackground(new Color(100, 116, 139));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.addActionListener(e -> limpiarCamposCliente());
        buttonPanel.add(btnLimpiar);
        
        // Campo de búsqueda
        JLabel lblBuscar = new JLabel("🔍 Buscar:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBuscar.setForeground(TEXT_DARK);
        lblBuscar.setBounds(30, 340, 100, 30);
        mainContent.add(lblBuscar);
        
        JTextField txtBuscarCliente = new JTextField();
        txtBuscarCliente.setBounds(130, 340, 300, 35);
        txtBuscarCliente.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscarCliente.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(10, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        mainContent.add(txtBuscarCliente);
        
        // Tabla de clientes
        modeloClientes = new DefaultTableModel();
        modeloClientes.addColumn("ID");
        modeloClientes.addColumn("Nombre");
        modeloClientes.addColumn("Apellido");
        modeloClientes.addColumn("Teléfono");
        modeloClientes.addColumn("Email");
        modeloClientes.addColumn("Dirección");
        modeloClientes.addColumn("Ciudad");
        
        tablaClientes = new JTable(modeloClientes);
        tablaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaClientes.setRowHeight(30);
        tablaClientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaClientes.getTableHeader().setBackground(new Color(59, 130, 246));
        tablaClientes.getTableHeader().setForeground(Color.WHITE);
        
        // Listener para seleccionar fila
        tablaClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaClientes.getSelectedRow();
                if (fila >= 0) {
                    txtIdCliente.setText(tablaClientes.getValueAt(fila, 0).toString());
                    txtNombreCliente.setText(tablaClientes.getValueAt(fila, 1).toString());
                    txtApellidoCliente.setText(tablaClientes.getValueAt(fila, 2).toString());
                    txtTelefonoCliente.setText(tablaClientes.getValueAt(fila, 3).toString());
                    txtEmailCliente.setText(tablaClientes.getValueAt(fila, 4).toString());
                    txtDireccionCliente.setText(tablaClientes.getValueAt(fila, 5).toString());
                    txtCiudadCliente.setText(tablaClientes.getValueAt(fila, 6).toString());
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        scrollPane.setBounds(30, 385, 890, 275);
        scrollPane.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        mainContent.add(scrollPane);
        
        // Cargar datos guardados
        cargarClientes();
        
        // Configurar filtro de búsqueda en tiempo real
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloClientes);
        tablaClientes.setRowSorter(sorter);
        
        txtBuscarCliente.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filtrarClientes();
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filtrarClientes();
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filtrarClientes();
            }
            
            private void filtrarClientes() {
                String texto = txtBuscarCliente.getText().trim();
                if (texto.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    try {
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
                    } catch (java.util.regex.PatternSyntaxException ex) {
                        sorter.setRowFilter(null);
                    }
                }
            }
        });
        
        mainContent.revalidate();
        mainContent.repaint();
    }
    
    private void cargarDatosEjemploClientes() {
        modeloClientes.addRow(new Object[]{"1", "Ana", "Martínez", "555-0101", "ana.martinez@email.com", "Calle Principal 123", "México"});
        modeloClientes.addRow(new Object[]{"2", "Carlos", "Rodríguez", "555-0102", "carlos.r@email.com", "Av. Central 456", "Guadalajara"});
        modeloClientes.addRow(new Object[]{"3", "María", "López", "555-0103", "maria.lopez@email.com", "Boulevard Norte 789", "Monterrey"});
        modeloClientes.addRow(new Object[]{"4", "Juan", "Pérez", "555-0104", "juan.perez@email.com", "Calle Sur 321", "Puebla"});
        modeloClientes.addRow(new Object[]{"5", "Laura", "González", "555-0105", "laura.g@email.com", "Av. Este 654", "Tijuana"});
        nextIdCliente = 6; // Siguiente ID disponible
    }
    
    private void agregarCliente() {
        if (validarCamposCliente()) {
            Vector<String> fila = new Vector<>();
            int nuevoId = nextIdCliente;
            String nombre = txtNombreCliente.getText();
            String apellido = txtApellidoCliente.getText();
            
            fila.add(String.valueOf(nuevoId)); // ID auto-generado
            fila.add(nombre);
            fila.add(apellido);
            fila.add(txtTelefonoCliente.getText());
            fila.add(txtEmailCliente.getText());
            fila.add(txtDireccionCliente.getText());
            fila.add(txtCiudadCliente.getText());
            modeloClientes.addRow(fila);
            nextIdCliente++; // Incrementar para el próximo
            
            // 🔄 SINCRONIZACIÓN AUTOMÁTICA
            sincronizarClienteEnOtrasTablas(nuevoId, nombre, apellido);
            
            // 💾 GUARDAR AUTOMÁTICAMENTE
            guardarClientes();
            
            limpiarCamposCliente();
            JOptionPane.showMessageDialog(frame, 
                "Cliente agregado exitosamente con ID: " + nuevoId + "\n✅ Guardado", 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void actualizarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila >= 0) {
            if (validarCamposCliente()) {
                modeloClientes.setValueAt(txtIdCliente.getText(), fila, 0);
                modeloClientes.setValueAt(txtNombreCliente.getText(), fila, 1);
                modeloClientes.setValueAt(txtApellidoCliente.getText(), fila, 2);
                modeloClientes.setValueAt(txtTelefonoCliente.getText(), fila, 3);
                modeloClientes.setValueAt(txtEmailCliente.getText(), fila, 4);
                modeloClientes.setValueAt(txtDireccionCliente.getText(), fila, 5);
                modeloClientes.setValueAt(txtCiudadCliente.getText(), fila, 6);
                guardarClientes(); // Guardar cambios
                limpiarCamposCliente();
                JOptionPane.showMessageDialog(frame, "Cliente actualizado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Seleccione un cliente de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void eliminarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila >= 0) {
            int confirmacion = JOptionPane.showConfirmDialog(frame, 
                "¿Está seguro de eliminar este cliente?", 
                "Confirmar eliminación", 
                JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                modeloClientes.removeRow(fila);
                guardarClientes(); // Guardar cambios
                limpiarCamposCliente();
                JOptionPane.showMessageDialog(frame, "Cliente eliminado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Seleccione un cliente de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void limpiarCamposCliente() {
        txtIdCliente.setText("");
        txtNombreCliente.setText("");
        txtApellidoCliente.setText("");
        txtTelefonoCliente.setText("");
        txtEmailCliente.setText("");
        txtDireccionCliente.setText("");
        txtCiudadCliente.setText("");
        tablaClientes.clearSelection();
    }
    
    private boolean validarCamposCliente() {
        if (txtNombreCliente.getText().trim().isEmpty() || 
            txtApellidoCliente.getText().trim().isEmpty() || 
            txtTelefonoCliente.getText().trim().isEmpty() || 
            txtEmailCliente.getText().trim().isEmpty() || 
            txtDireccionCliente.getText().trim().isEmpty() || 
            txtCiudadCliente.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    // ==================== PANEL DE CATEGORÍAS ====================
    
    private void mostrarPanelCategorias() {
        mainContent.removeAll();
        
        // Header
        JLabel lblHeader = new JLabel("Gestión de Categorías");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblHeader.setForeground(TEXT_DARK);
        lblHeader.setBounds(30, 20, 400, 40);
        mainContent.add(lblHeader);
        
        // Botón Cerrar
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(820, 20, 100, 35);
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setBackground(new Color(239, 68, 68));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> frame.dispose());
        mainContent.add(btnCerrar);
        
        // === LOGO DE TIENDA (lado derecho, arriba) ===
        JPanel panelLogoTienda = new JPanel();
        panelLogoTienda.setBackground(CARD_COLOR);
        panelLogoTienda.setBounds(780, 60, 160, 110);
        panelLogoTienda.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        panelLogoTienda.setLayout(null);
        mainContent.add(panelLogoTienda);
        
        JLabel lblLogoTienda = new JLabel("Imagen", SwingConstants.CENTER);
        lblLogoTienda.setBounds(5, 5, 150, 100);
        lblLogoTienda.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLogoTienda.setForeground(TEXT_LIGHT);
        lblLogoTienda.setBorder(BorderFactory.createDashedBorder(Color.LIGHT_GRAY, 2, 5));
        if (rutaImagenTiendaGuardada != null && !rutaImagenTiendaGuardada.isEmpty()) {
            try {
                ImageIcon iconoOriginal = new ImageIcon(rutaImagenTiendaGuardada);
                Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(140, 90, Image.SCALE_SMOOTH);
                lblLogoTienda.setIcon(new ImageIcon(imagenEscalada));
                lblLogoTienda.setText("");
            } catch (Exception ex) { lblLogoTienda.setText("Error"); }
        }
        panelLogoTienda.add(lblLogoTienda);
        
        // === NOMBRE DE TIENDA ===
        JLabel lblNombreTiendaDisplay = new JLabel("", SwingConstants.CENTER);
        lblNombreTiendaDisplay.setBounds(780, 175, 160, 25);
        lblNombreTiendaDisplay.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNombreTiendaDisplay.setForeground(TEXT_DARK);
        if (nombreTiendaGuardado != null && !nombreTiendaGuardado.isEmpty()) {
            lblNombreTiendaDisplay.setText(nombreTiendaGuardado);
        }
        mainContent.add(lblNombreTiendaDisplay);
        
        // Panel de formulario (reducido para logo)
        JPanel formPanel = new JPanel();
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBounds(30, 80, 680, 180);
        formPanel.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        formPanel.setLayout(null);
        mainContent.add(formPanel);
        
        // Fila 1: ID, Nombre, Descripción
        JLabel lblId = new JLabel("ID Categoría:");
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblId.setBounds(20, 20, 120, 25);
        formPanel.add(lblId);
        
        txtIdCategoriaCat = new JTextField();
        txtIdCategoriaCat.setBounds(20, 45, 120, 35);
        txtIdCategoriaCat.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtIdCategoriaCat.setEditable(false);
        txtIdCategoriaCat.setBackground(new Color(240, 240, 240));
        txtIdCategoriaCat.setText(categoriaIdPreservado); // Restaurar valor preservado
        formPanel.add(txtIdCategoriaCat);
        
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombre.setBounds(160, 20, 100, 25);
        formPanel.add(lblNombre);
        
        txtNombreCategoria = new JTextField();
        txtNombreCategoria.setBounds(160, 45, 200, 35);
        txtNombreCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNombreCategoria.setText(categoriaNombrePreservado); // Restaurar valor preservado
        formPanel.add(txtNombreCategoria);
        
        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDescripcion.setBounds(380, 20, 120, 25);
        formPanel.add(lblDescripcion);
        
        txtDescripcionCategoria = new JTextField();
        txtDescripcionCategoria.setBounds(380, 45, 350, 35);
        txtDescripcionCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDescripcionCategoria.setText(categoriaDescripcionPreservado); // Restaurar valor preservado
        formPanel.add(txtDescripcionCategoria);
        
        // Fila 2: Producto
        JLabel lblProducto = new JLabel("Producto:");
        lblProducto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblProducto.setBounds(20, 95, 100, 25);
        formPanel.add(lblProducto);
        
        txtProductoCategoria = new JTextField();
        txtProductoCategoria.setBounds(20, 120, 710, 35);
        txtProductoCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtProductoCategoria.setText(categoriaProductoPreservado); // Restaurar valor preservado
        formPanel.add(txtProductoCategoria);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BG_COLOR);
        buttonPanel.setBounds(30, 280, 890, 50);
        buttonPanel.setLayout(null);
        mainContent.add(buttonPanel);
        
        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(0, 0, 140, 40);
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAgregar.setBackground(PRIMARY_COLOR);
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFocusPainted(false);
        btnAgregar.addActionListener(e -> agregarCategoria());
        buttonPanel.add(btnAgregar);
        
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(160, 0, 140, 40);
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnActualizar.setBackground(ACCENT_COLOR);
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.addActionListener(e -> actualizarCategoria());
        buttonPanel.add(btnActualizar);
        
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(320, 0, 140, 40);
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEliminar.setBackground(new Color(239, 68, 68));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.addActionListener(e -> eliminarCategoria());
        buttonPanel.add(btnEliminar);
        
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(480, 0, 140, 40);
        btnLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLimpiar.setBackground(new Color(100, 116, 139));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.addActionListener(e -> limpiarCamposCategoria());
        buttonPanel.add(btnLimpiar);
        
        // Campo de búsqueda
        JLabel lblBuscar = new JLabel("🔍 Buscar:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBuscar.setForeground(TEXT_DARK);
        lblBuscar.setBounds(30, 340, 100, 30);
        mainContent.add(lblBuscar);
        
        JTextField txtBuscarCategoria = new JTextField();
        txtBuscarCategoria.setBounds(130, 340, 300, 35);
        txtBuscarCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscarCategoria.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(10, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        mainContent.add(txtBuscarCategoria);
        
        // Tabla de categorías
        modeloCategorias = new DefaultTableModel();
        modeloCategorias.addColumn("ID");
        modeloCategorias.addColumn("Nombre");
        modeloCategorias.addColumn("Descripción");
        modeloCategorias.addColumn("Producto");
        
        tablaCategorias = new JTable(modeloCategorias);
        tablaCategorias.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaCategorias.setRowHeight(30);
        tablaCategorias.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaCategorias.getTableHeader().setBackground(new Color(139, 92, 246));
        tablaCategorias.getTableHeader().setForeground(Color.WHITE);
        
        // Listener para seleccionar fila Y agregar nueva fila automáticamente EN PRODUCTOS
        tablaCategorias.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaCategorias.getSelectedRow();
                if (fila >= 0) {
                    // 💾 GUARDAR ESTADO: índice de fila y valores de campos
                    categoriaFilaSeleccionada = fila;
                    categoriaIdPreservado = tablaCategorias.getValueAt(fila, 0).toString();
                    categoriaNombrePreservado = tablaCategorias.getValueAt(fila, 1).toString();
                    categoriaDescripcionPreservado = tablaCategorias.getValueAt(fila, 2).toString();
                    categoriaProductoPreservado = tablaCategorias.getValueAt(fila, 3).toString();
                    
                    // Cargar datos de la fila seleccionada en los campos
                    txtIdCategoriaCat.setText(categoriaIdPreservado);
                    txtNombreCategoria.setText(categoriaNombrePreservado);
                    txtDescripcionCategoria.setText(categoriaDescripcionPreservado);
                    txtProductoCategoria.setText(categoriaProductoPreservado);
                    
                    // ✨ NUEVA FUNCIONALIDAD: Agregar fila vacía EN LA TABLA DE PRODUCTOS
                    // Para facilitar agregar productos de esta categoría
                    String idCategoria = tablaCategorias.getValueAt(fila, 0).toString();
                    
                    // Verificar si la tabla de productos tiene modelo inicializado
                    if (modeloProductos != null) {
                        // Solo agregar si la última fila no está vacía
                        int ultimaFila = modeloProductos.getRowCount() - 1;
                        boolean ultimaFilaVacia = false;
                        
                        if (ultimaFila >= 0) {
                            Object id = modeloProductos.getValueAt(ultimaFila, 0);
                            Object nombre = modeloProductos.getValueAt(ultimaFila, 1);
                            ultimaFilaVacia = (id == null || id.toString().trim().isEmpty()) && 
                                             (nombre == null || nombre.toString().trim().isEmpty());
                        }
                        
                        // Agregar nueva fila vacía en productos si la última tiene datos
                        if (!ultimaFilaVacia) {
                            Vector<String> nuevaFila = new Vector<>();
                            nuevaFila.add(""); // ID vacío
                            nuevaFila.add(""); // Nombre vacío
                            nuevaFila.add(""); // Precio vacío
                            nuevaFila.add(""); // Stock vacío
                            nuevaFila.add(idCategoria); // ID Categoría pre-llenado
                            nuevaFila.add(""); // Estado vacío
                            modeloProductos.addRow(nuevaFila);
                            
                            System.out.println("✅ Fila agregada en Productos con Categoría ID: " + idCategoria);
                        }
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaCategorias);
        scrollPane.setBounds(30, 385, 890, 275);
        scrollPane.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        mainContent.add(scrollPane);
        
        // Cargar datos guardados
        cargarCategorias();
        
        // ✨ RESTAURAR SELECCIÓN PRESERVADA (con delay para asegurar que la tabla se renderizó)
        SwingUtilities.invokeLater(() -> {
            if (categoriaFilaSeleccionada >= 0 && categoriaFilaSeleccionada < tablaCategorias.getRowCount()) {
                tablaCategorias.setRowSelectionInterval(categoriaFilaSeleccionada, categoriaFilaSeleccionada);
                tablaCategorias.scrollRectToVisible(tablaCategorias.getCellRect(categoriaFilaSeleccionada, 0, true));
                System.out.println("✅ Selección restaurada: fila " + categoriaFilaSeleccionada);
            }
        });
        
        // Configurar filtro de búsqueda en tiempo real
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloCategorias);
        tablaCategorias.setRowSorter(sorter);
        
        txtBuscarCategoria.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filtrarCategorias();
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filtrarCategorias();
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filtrarCategorias();
            }
            
            private void filtrarCategorias() {
                String texto = txtBuscarCategoria.getText().trim();
                if (texto.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    try {
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
                    } catch (java.util.regex.PatternSyntaxException ex) {
                        sorter.setRowFilter(null);
                    }
                }
            }
        });
        
        mainContent.revalidate();
        mainContent.repaint();
    }
    
    // ==================== PANEL DE VENTAS (POS) ====================
    
    private void mostrarPanelVentas() {
        mainContent.removeAll();
        
        // Header
        JLabel lblHeader = new JLabel("Sistema de Ventas - POS");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblHeader.setForeground(TEXT_DARK);
        lblHeader.setBounds(30, 20, 400, 40);
        mainContent.add(lblHeader);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(820, 20, 100, 35);
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setBackground(new Color(239, 68, 68));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> frame.dispose());
        mainContent.add(btnCerrar);
        
        // === LOGO DE TIENDA (esquina superior derecha) ===
        JPanel panelLogoTienda = new JPanel();
        panelLogoTienda.setBackground(CARD_COLOR);
        panelLogoTienda.setBounds(950, 60, 180, 100);
        panelLogoTienda.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        panelLogoTienda.setLayout(null);
        mainContent.add(panelLogoTienda);
        
        JLabel lblLogoTienda = new JLabel("Logo", SwingConstants.CENTER);
        lblLogoTienda.setBounds(5, 5, 170, 90);
        lblLogoTienda.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLogoTienda.setForeground(TEXT_LIGHT);
        if (rutaImagenTiendaGuardada != null && !rutaImagenTiendaGuardada.isEmpty()) {
            try {
                ImageIcon iconoOriginal = new ImageIcon(rutaImagenTiendaGuardada);
                Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(160, 80, Image.SCALE_SMOOTH);
                lblLogoTienda.setIcon(new ImageIcon(imagenEscalada));
                lblLogoTienda.setText("");
            } catch (Exception ex) { lblLogoTienda.setText("Error"); }
        }
        panelLogoTienda.add(lblLogoTienda);
        
        // === NOMBRE DE TIENDA ===
        JLabel lblNombreTiendaDisplay = new JLabel("", SwingConstants.CENTER);
        lblNombreTiendaDisplay.setBounds(950, 165, 180, 25);
        lblNombreTiendaDisplay.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNombreTiendaDisplay.setForeground(TEXT_DARK);
        if (nombreTiendaGuardado != null && !nombreTiendaGuardado.isEmpty()) {
            lblNombreTiendaDisplay.setText(nombreTiendaGuardado);
        }
        mainContent.add(lblNombreTiendaDisplay);
        
        // ========== TABLA DE PRODUCTOS DISPONIBLES (IZQUIERDA) ==========
        JLabel lblProductosDisponibles = new JLabel("Productos Disponibles");
        lblProductosDisponibles.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblProductosDisponibles.setForeground(TEXT_DARK);
        lblProductosDisponibles.setBounds(30, 80, 300, 30);
        mainContent.add(lblProductosDisponibles);
        
        // La tabla usa el modelo que ya inicializamos en el constructor
        tablaProductosDisponibles = new JTable(modeloProductosDisponibles);
        tablaProductosDisponibles.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaProductosDisponibles.setRowHeight(30);
        tablaProductosDisponibles.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaProductosDisponibles.getTableHeader().setBackground(new Color(16, 185, 129));
        tablaProductosDisponibles.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollProductos = new JScrollPane(tablaProductosDisponibles);
        scrollProductos.setBounds(30, 120, 400, 380);
        scrollProductos.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        mainContent.add(scrollProductos);
        
        // ========== BOTONES CENTRALES ==========
        JButton btnAgregar = new JButton("Agregar →");
        btnAgregar.setBounds(450, 250, 140, 45);
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAgregar.setBackground(new Color(59, 130, 246));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFocusPainted(false);
        btnAgregar.addActionListener(e -> agregarProductoAlCarrito());
        mainContent.add(btnAgregar);
        
        JButton btnQuitar = new JButton("← Quitar");
        btnQuitar.setBounds(450, 310, 140, 45);
        btnQuitar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnQuitar.setBackground(new Color(239, 68, 68));
        btnQuitar.setForeground(Color.WHITE);
        btnQuitar.setFocusPainted(false);
        btnQuitar.addActionListener(e -> quitarProductoDelCarrito());
        mainContent.add(btnQuitar);
        
        // ========== TABLA DE CARRITO (DERECHA) ==========
        JLabel lblCarrito = new JLabel("Carrito de Compra");
        lblCarrito.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblCarrito.setForeground(TEXT_DARK);
        lblCarrito.setBounds(610, 80, 300, 30);
        mainContent.add(lblCarrito);
        
        // La tabla usa el modelo que ya inicializamos en el constructor
        tablaCarrito = new JTable(modeloCarrito);
        tablaCarrito.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaCarrito.setRowHeight(30);
        tablaCarrito.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaCarrito.getTableHeader().setBackground(new Color(251, 146, 60));
        tablaCarrito.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollCarrito = new JScrollPane(tablaCarrito);
        scrollCarrito.setBounds(610, 120, 310, 280);
        scrollCarrito.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        mainContent.add(scrollCarrito);
        
        // ========== TOTAL Y BOTONES FINALES ==========
        JLabel lblTotalTexto = new JLabel("TOTAL:");
        lblTotalTexto.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotalTexto.setForeground(TEXT_DARK);
        lblTotalTexto.setBounds(610, 420, 100, 30);
        mainContent.add(lblTotalTexto);
        
        lblTotal = new JLabel("$0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTotal.setForeground(new Color(16, 185, 129));
        lblTotal.setBounds(750, 410, 170, 40);
        mainContent.add(lblTotal);
        
        JButton btnFinalizarVenta = new JButton("Finalizar Venta");
        btnFinalizarVenta.setBounds(610, 470, 310, 50);
        btnFinalizarVenta.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnFinalizarVenta.setBackground(new Color(16, 185, 129));
        btnFinalizarVenta.setForeground(Color.WHITE);
        btnFinalizarVenta.setFocusPainted(false);
        btnFinalizarVenta.addActionListener(e -> finalizarVenta());
        mainContent.add(btnFinalizarVenta);
        
        JButton btnVaciarCarrito = new JButton("Vaciar Carrito");
        btnVaciarCarrito.setBounds(30, 520, 200, 40);
        btnVaciarCarrito.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVaciarCarrito.setBackground(new Color(100, 116, 139));
        btnVaciarCarrito.setForeground(Color.WHITE);
        btnVaciarCarrito.setFocusPainted(false);
        btnVaciarCarrito.addActionListener(e -> vaciarCarrito());
        mainContent.add(btnVaciarCarrito);
        
        mainContent.revalidate();
        mainContent.repaint();
        
        System.out.println("✅ Panel de Ventas cargado con " + modeloProductosDisponibles.getRowCount() + " productos disponibles");
    }
    
    // ==================== MÉTODOS DEL CARRITO DE COMPRAS ====================
    
    private void agregarProductoAlCarrito() {
        int fila = tablaProductosDisponibles.getSelectedRow();
        if (fila >= 0) {
            try {
                String id = modeloProductosDisponibles.getValueAt(fila, 0).toString();
                String nombre = modeloProductosDisponibles.getValueAt(fila, 1).toString();
                double precio = Double.parseDouble(modeloProductosDisponibles.getValueAt(fila, 2).toString());
                int stockDisponible = Integer.parseInt(modeloProductosDisponibles.getValueAt(fila, 3).toString());
                
                if (stockDisponible <= 0) {
                    JOptionPane.showMessageDialog(frame, "Producto sin stock disponible", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Verificar si ya está en el carrito
                boolean existe = false;
                for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
                    if (modeloCarrito.getValueAt(i, 0).toString().equals(id)) {
                        // Incrementar cantidad
                        int cantidadActual = Integer.parseInt(modeloCarrito.getValueAt(i, 3).toString());
                        if (cantidadActual < stockDisponible) {
                            int nuevaCantidad = cantidadActual + 1;
                            double nuevoSubtotal = precio * nuevaCantidad;
                            modeloCarrito.setValueAt(nuevaCantidad, i, 3);
                            modeloCarrito.setValueAt(String.format("%.2f", nuevoSubtotal), i, 4);
                            existe = true;
                        } else {
                            JOptionPane.showMessageDialog(frame, "No hay más stock disponible", "Advertencia", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        break;
                    }
                }
                
                // Si no existe, agregarlo
                if (!existe) {
                    Vector<String> filaCarrito = new Vector<>();
                    filaCarrito.add(id);
                    filaCarrito.add(nombre);
                    filaCarrito.add(String.format("%.2f", precio));
                    filaCarrito.add("1");
                    filaCarrito.add(String.format("%.2f", precio));
                    modeloCarrito.addRow(filaCarrito);
                }
                
                actualizarTotal();
                System.out.println("✅ Producto '" + nombre + "' agregado al carrito");
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error al agregar producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Seleccione un producto de la lista", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void quitarProductoDelCarrito() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila >= 0) {
            String nombre = modeloCarrito.getValueAt(fila, 1).toString();
            modeloCarrito.removeRow(fila);
            actualizarTotal();
            System.out.println("✅ Producto '" + nombre + "' eliminado del carrito");
        } else {
            JOptionPane.showMessageDialog(frame, "Seleccione un producto del carrito", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void vaciarCarrito() {
        modeloCarrito.setRowCount(0);
        actualizarTotal();
        System.out.println("✅ Carrito vaciado");
    }
    
    private void actualizarTotal() {
        totalVenta = 0.0;
        for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
            double subtotal = Double.parseDouble(modeloCarrito.getValueAt(i, 4).toString());
            totalVenta += subtotal;
        }
        lblTotal.setText(String.format("$%.2f", totalVenta));
    }
    
    private void finalizarVenta() {
        if (modeloCarrito.getRowCount() == 0) {
            JOptionPane.showMessageDialog(frame, "El carrito está vacío", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Construir mensaje con detalles del carrito
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("=== RESUMEN DE VENTA ===\n\n");
        mensaje.append("PRODUCTOS:\n");
        for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
            String producto = modeloCarrito.getValueAt(i, 1).toString();
            String precio = modeloCarrito.getValueAt(i, 2).toString();
            String cantidad = modeloCarrito.getValueAt(i, 3).toString();
            String subtotal = modeloCarrito.getValueAt(i, 4).toString();
            mensaje.append(String.format("  • %s x%s = $%s\n", producto, cantidad, subtotal));
        }
        mensaje.append(String.format("\nTOTAL: $%.2f\n", totalVenta));
        
        // Agregar datos del ÚLTIMO cliente agregado
        mensaje.append("\n=== DATOS DEL CLIENTE ===\n");
        if (modeloClientes != null && modeloClientes.getRowCount() > 0) {
            // Obtener el último cliente (última fila de la tabla)
            int ultimaFila = modeloClientes.getRowCount() - 1;
            String id = modeloClientes.getValueAt(ultimaFila, 0).toString();
            String nombre = modeloClientes.getValueAt(ultimaFila, 1).toString();
            String apellido = modeloClientes.getValueAt(ultimaFila, 2).toString();
            String telefono = modeloClientes.getValueAt(ultimaFila, 3).toString();
            String email = modeloClientes.getValueAt(ultimaFila, 4).toString();
            String direccion = modeloClientes.getValueAt(ultimaFila, 5).toString();
            String ciudad = modeloClientes.getValueAt(ultimaFila, 6).toString();
            
            mensaje.append(String.format("  ID: %s\n", id));
            mensaje.append(String.format("  Nombre: %s %s\n", nombre, apellido));
            mensaje.append(String.format("  Teléfono: %s\n", telefono));
            mensaje.append(String.format("  Email: %s\n", email));
            mensaje.append(String.format("  Dirección: %s\n", direccion));
            mensaje.append(String.format("  Ciudad: %s\n", ciudad));
        } else {
            mensaje.append("  No hay clientes registrados\n");
        }
        
        mensaje.append("\n¿Desea finalizar la venta?");
        
        int confirmacion = JOptionPane.showConfirmDialog(frame, 
            mensaje.toString(), 
            "Confirmar Venta", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            // TODO: Guardar venta en base de datos
            JOptionPane.showMessageDialog(frame, 
                "Venta finalizada exitosamente\nTotal: $" + String.format("%.2f", totalVenta), 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
            
            vaciarCarrito();
            System.out.println("✅ Venta finalizada por $" + String.format("%.2f", totalVenta));
        }
    }
    
    private void cargarDatosEjemploCategorias() {
        modeloCategorias.addRow(new Object[]{"1", "Electrónica", "Dispositivos y componentes electrónicos", "Laptops, Tablets"});
        modeloCategorias.addRow(new Object[]{"2", "Accesorios", "Accesorios para computadoras y periféricos", "Mouse, Teclados"});
        modeloCategorias.addRow(new Object[]{"3", "Monitores", "Pantallas y monitores de diferentes tamaños", "Monitores LED, LCD"});
        modeloCategorias.addRow(new Object[]{"4", "Audio", "Equipos de audio y sonido", "Auriculares, Bocinas"});
        modeloCategorias.addRow(new Object[]{"5", "Almacenamiento", "Dispositivos de almacenamiento de datos", "SSD, HDD, USB"});
        nextIdCategoria = 6; // Siguiente ID disponible
    }
    
    private void agregarCategoria() {
        if (validarCamposCategoria()) {
            try {
                String nombre = txtNombreCategoria.getText();
                String descripcion = txtDescripcionCategoria.getText();
                String producto = txtProductoCategoria.getText();
                
                // Insertar en la base de datos
                Connection conn = Conexion.getInstancia().getConnection();
                String sql = "INSERT INTO categorias (nombre, descripcion, producto) VALUES (?, ?, ?)";
                java.sql.PreparedStatement pst = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
                pst.setString(1, nombre);
                pst.setString(2, descripcion);
                pst.setString(3, producto);
                
                int filasAfectadas = pst.executeUpdate();
                
                if (filasAfectadas > 0) {
                    // Obtener el ID generado
                    java.sql.ResultSet rs = pst.getGeneratedKeys();
                    int nuevoId = 0;
                    if (rs.next()) {
                        nuevoId = rs.getInt(1);
                    }
                    
                    // Agregar a la tabla visual
                    Vector<String> fila = new Vector<>();
                    fila.add(String.valueOf(nuevoId));
                    fila.add(nombre);
                    fila.add(descripcion);
                    fila.add(producto);
                    modeloCategorias.addRow(fila);
                    
                    // 🔄 SINCRONIZACIÓN AUTOMÁTICA
                    sincronizarCategoriaEnOtrasTablas(nuevoId, nombre);
                    
                    limpiarCamposCategoria();
                    JOptionPane.showMessageDialog(frame, 
                        "Categoría agregada exitosamente con ID: " + nuevoId + "\n✅ Guardada en base de datos", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
                
                pst.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, 
                    "Error al agregar categoría: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void actualizarCategoria() {
        int fila = tablaCategorias.getSelectedRow();
        if (fila >= 0) {
            if (validarCamposCategoria()) {
                try {
                    int id = Integer.parseInt(txtIdCategoriaCat.getText());
                    String nombre = txtNombreCategoria.getText();
                    String descripcion = txtDescripcionCategoria.getText();
                    String producto = txtProductoCategoria.getText();
                    
                    // Actualizar en la base de datos
                    Connection conn = Conexion.getInstancia().getConnection();
                    String sql = "UPDATE categorias SET nombre=?, descripcion=?, producto=? WHERE id=?";
                    java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, nombre);
                    pst.setString(2, descripcion);
                    pst.setString(3, producto);
                    pst.setInt(4, id);
                    
                    int filasAfectadas = pst.executeUpdate();
                    
                    if (filasAfectadas > 0) {
                        // Actualizar en la tabla visual
                        modeloCategorias.setValueAt(String.valueOf(id), fila, 0);
                        modeloCategorias.setValueAt(nombre, fila, 1);
                        modeloCategorias.setValueAt(descripcion, fila, 2);
                        modeloCategorias.setValueAt(producto, fila, 3);
                        
                        limpiarCamposCategoria();
                        JOptionPane.showMessageDialog(frame, "Categoría actualizada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                    pst.close();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, 
                        "Error al actualizar categoría: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Seleccione una categoría de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void eliminarCategoria() {
        int fila = tablaCategorias.getSelectedRow();
        if (fila >= 0) {
            int confirmacion = JOptionPane.showConfirmDialog(frame, 
                "¿Está seguro de eliminar esta categoría?", 
                "Confirmar eliminación", 
                JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    int id = Integer.parseInt(tablaCategorias.getValueAt(fila, 0).toString());
                    
                    // Eliminar de la base de datos
                    Connection conn = Conexion.getInstancia().getConnection();
                    String sql = "DELETE FROM categorias WHERE id=?";
                    java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setInt(1, id);
                    
                    int filasAfectadas = pst.executeUpdate();
                    
                    if (filasAfectadas > 0) {
                        // Eliminar de la tabla visual
                        modeloCategorias.removeRow(fila);
                        limpiarCamposCategoria();
                        JOptionPane.showMessageDialog(frame, "Categoría eliminada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                    pst.close();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, 
                        "Error al eliminar categoría: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Seleccione una categoría de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void limpiarCamposCategoria() {
        txtIdCategoriaCat.setText("");
        txtNombreCategoria.setText("");
        txtDescripcionCategoria.setText("");
        txtProductoCategoria.setText("");
        tablaCategorias.clearSelection();
    }
    
    private boolean validarCamposCategoria() {
        if (txtNombreCategoria.getText().trim().isEmpty() || 
            txtDescripcionCategoria.getText().trim().isEmpty() || 
            txtProductoCategoria.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }


    private void createStatCard(JPanel parent, String title, String value, String change, int x, int y, Color accentColor) {
        JPanel card = new JPanel();
        card.setBackground(CARD_COLOR);
        card.setBounds(x, y, 210, 150);
        card.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        card.setLayout(null);
        parent.add(card);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(TEXT_LIGHT);
        lblTitle.setBounds(20, 15, 170, 25);
        card.add(lblTitle);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValue.setForeground(TEXT_DARK);
        lblValue.setBounds(20, 45, 170, 45);
        card.add(lblValue);

        JLabel lblChange = new JLabel(change);
        lblChange.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblChange.setForeground(accentColor);
        lblChange.setBounds(20, 100, 170, 25);
        card.add(lblChange);

        JPanel colorBar = new JPanel();
        colorBar.setBackground(accentColor);
        colorBar.setBounds(0, 0, 210, 5);
        card.add(colorBar);
    }

    private JPanel createMenuItem(String text, boolean active) {
        JPanel item = new JPanel();
        item.setBackground(active ? SIDEBAR_HOVER : SIDEBAR_COLOR);
        item.setLayout(null);
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblText = new JLabel(text);
        lblText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblText.setBounds(20, 0, 140, 45);
        lblText.setForeground(Color.WHITE);
        item.add(lblText);
        
        // ATAJOS DE TECLADO REMOVIDOS DEL DISEÑO
        /*
        // Agregar indicador de atajo de teclado
        String shortcut = "";
        switch(text) {
            case "Producto": shortcut = "F3"; break;
            case "Empleado": shortcut = "F4"; break;
            case "Cliente": shortcut = "F5"; break;
            case "Ventas": shortcut = "F6"; break;
            case "Categoría": shortcut = "F7"; break;
        }
        
        if (!shortcut.isEmpty()) {
            JLabel lblShortcut = new JLabel(shortcut);
            lblShortcut.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblShortcut.setBounds(170, 0, 40, 45);
            lblShortcut.setForeground(new Color(148, 163, 184));
            item.add(lblShortcut);
        }
        */


        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBackground(SIDEBAR_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!active) {
                    item.setBackground(SIDEBAR_COLOR);
                }
            }
        });

        return item;
    }

    private JPanel createActivityItem(String text) {
        JPanel item = new JPanel();
        item.setBackground(new Color(248, 250, 252));
        item.setLayout(null);
        item.setBorder(new RoundBorder(10, new Color(226, 232, 240)));

        JLabel dot = new JLabel("●");
        dot.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dot.setForeground(PRIMARY_COLOR);
        dot.setBounds(10, 0, 20, 50);
        item.add(dot);

        JLabel lblText = new JLabel(text);
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblText.setForeground(TEXT_DARK);
        lblText.setBounds(35, 0, 210, 30);
        item.add(lblText);

        JLabel lblTime = new JLabel("Hace 5 min");
        lblTime.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTime.setForeground(TEXT_LIGHT);
        lblTime.setBounds(35, 25, 210, 20);
        item.add(lblTime);

        return item;
    }
    
    // ==================== PANEL DE CONFIGURACIÓN ====================
    
    private void mostrarPanelConfiguracion() {
        mainContent.removeAll();
        
        // Header
        JLabel lblHeader = new JLabel("Configuración");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblHeader.setForeground(TEXT_DARK);
        lblHeader.setBounds(30, 20, 400, 40);
        mainContent.add(lblHeader);
        
        // Botón Cerrar
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(820, 20, 100, 35);
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setBackground(new Color(239, 68, 68));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> frame.dispose());
        mainContent.add(btnCerrar);
        
        // Panel principal de configuración
        JPanel panelConfig = new JPanel();
        panelConfig.setBackground(CARD_COLOR);
        panelConfig.setBounds(30, 100, 890, 500);
        panelConfig.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        panelConfig.setLayout(null);
        mainContent.add(panelConfig);
        
        // Título de sección
        JLabel lblSeccion = new JLabel("Opciones de Configuración");
        lblSeccion.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblSeccion.setForeground(TEXT_DARK);
        lblSeccion.setBounds(30, 20, 300, 30);
        panelConfig.add(lblSeccion);
        
        // Botón Tienda
        JButton btnTienda = new JButton("Tienda");
        btnTienda.setBounds(30, 80, 200, 60);
        btnTienda.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnTienda.setBackground(PRIMARY_COLOR);
        btnTienda.setForeground(Color.WHITE);
        btnTienda.setFocusPainted(false);
        btnTienda.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTienda.addActionListener(e -> {
            // Crear panel principal con BorderLayout
            JPanel panelTienda = new JPanel();
            panelTienda.setLayout(new BorderLayout(10, 10));
            panelTienda.setPreferredSize(new java.awt.Dimension(450, 200));
            
            // Panel de campos de texto (izquierda)
            JPanel panelCampos = new JPanel();
            panelCampos.setLayout(new GridLayout(3, 2, 10, 10));
            
            JLabel lblNombreTienda = new JLabel("Nombre:");
            lblNombreTienda.setFont(new Font("Segoe UI", Font.BOLD, 14));
            JTextField txtNombreTienda = new JTextField();
            txtNombreTienda.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            JLabel lblLugarTienda = new JLabel("Lugar:");
            lblLugarTienda.setFont(new Font("Segoe UI", Font.BOLD, 14));
            JTextField txtLugarTienda = new JTextField();
            txtLugarTienda.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            // Botón para seleccionar imagen
            JLabel lblImagenTienda = new JLabel("Imagen:");
            lblImagenTienda.setFont(new Font("Segoe UI", Font.BOLD, 14));
            JButton btnSeleccionarImagen = new JButton("Seleccionar...");
            btnSeleccionarImagen.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            panelCampos.add(lblNombreTienda);
            panelCampos.add(txtNombreTienda);
            panelCampos.add(lblLugarTienda);
            panelCampos.add(txtLugarTienda);
            panelCampos.add(lblImagenTienda);
            panelCampos.add(btnSeleccionarImagen);
            
            // Panel de vista previa de imagen (derecha)
            JPanel panelPreview = new JPanel();
            panelPreview.setLayout(new BorderLayout());
            panelPreview.setPreferredSize(new java.awt.Dimension(120, 120));
            panelPreview.setBorder(BorderFactory.createTitledBorder("Vista previa"));
            
            JLabel lblPreviewImagen = new JLabel("Sin imagen", SwingConstants.CENTER);
            lblPreviewImagen.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            lblPreviewImagen.setForeground(Color.GRAY);
            panelPreview.add(lblPreviewImagen, BorderLayout.CENTER);
            
            // Variable para guardar la ruta de la imagen seleccionada
            final String[] rutaImagenSeleccionada = {null};
            
            // Acción del botón seleccionar imagen
            btnSeleccionarImagen.addActionListener(ev -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Seleccionar Imagen");
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Imágenes (*.jpg, *.png, *.gif)", "jpg", "jpeg", "png", "gif"));
                
                int resultado = fileChooser.showOpenDialog(frame);
                if (resultado == JFileChooser.APPROVE_OPTION) {
                    java.io.File archivoSeleccionado = fileChooser.getSelectedFile();
                    rutaImagenSeleccionada[0] = archivoSeleccionado.getAbsolutePath();
                    
                    // Mostrar vista previa
                    try {
                        ImageIcon iconoOriginal = new ImageIcon(rutaImagenSeleccionada[0]);
                        Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                        lblPreviewImagen.setIcon(new ImageIcon(imagenEscalada));
                        lblPreviewImagen.setText("");
                    } catch (Exception ex) {
                        lblPreviewImagen.setText("Error");
                    }
                }
            });
            
            panelTienda.add(panelCampos, BorderLayout.CENTER);
            panelTienda.add(panelPreview, BorderLayout.EAST);
            
            int resultado = JOptionPane.showConfirmDialog(frame, panelTienda, 
                "Configuración de Tienda", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (resultado == JOptionPane.OK_OPTION) {
                String nombreTienda = txtNombreTienda.getText().trim();
                String lugarTienda = txtLugarTienda.getText().trim();
                if (!nombreTienda.isEmpty() && !lugarTienda.isEmpty()) {
                    // GUARDAR en variables de clase para usar en panel Productos
                    nombreTiendaGuardado = nombreTienda;
                    if (rutaImagenSeleccionada[0] != null) {
                        rutaImagenTiendaGuardada = rutaImagenSeleccionada[0];
                    }
                    
                    String mensaje = "Tienda guardada:\nNombre: " + nombreTienda + "\nLugar: " + lugarTienda;
                    if (rutaImagenSeleccionada[0] != null) {
                        mensaje += "\nImagen: " + rutaImagenSeleccionada[0];
                    }
                    mensaje += "\n\n✅ Los datos aparecerán en el panel de Productos";
                    JOptionPane.showMessageDialog(frame, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, 
                        "Por favor ingrese nombre y lugar", 
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        panelConfig.add(btnTienda);
        
        mainContent.revalidate();
        mainContent.repaint();
    }

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
    
    // ==================== MÉTODOS DE SINCRONIZACIÓN AUTOMÁTICA ====================
    
    /**
     * Sincroniza un empleado recién agregado en todas las tablas relacionadas
     */
    private void sincronizarEmpleadoEnOtrasTablas(int id, String nombre, String apellido) {
        // No hay sincronización directa necesaria actualmente
        // Los empleados se usan en ventas pero no se agregan automáticamente
        System.out.println("✅ Empleado ID " + id + " sincronizado");
    }
    
    
    /**
     * Actualiza el stock de un producto en todas las tablas cuando se realiza una venta
     */
    private void actualizarStockEnTodasLasTablas(int idProducto, int cantidadVendida) {
        // Actualizar en tabla de productos
        for (int i = 0; i < modeloProductos.getRowCount(); i++) {
            if (modeloProductos.getValueAt(i, 0).toString().equals(String.valueOf(idProducto))) {
                int stockActual = Integer.parseInt(modeloProductos.getValueAt(i, 3).toString());
                int nuevoStock = stockActual - cantidadVendida;
                modeloProductos.setValueAt(String.valueOf(nuevoStock), i, 3);
                break;
            }
        }
        
        // Actualizar en productos disponibles (ventas)
        if (modeloProductosDisponibles != null) {
            for (int i = 0; i < modeloProductosDisponibles.getRowCount(); i++) {
                if (modeloProductosDisponibles.getValueAt(i, 0).toString().equals(String.valueOf(idProducto))) {
                    int stockActual = Integer.parseInt(modeloProductosDisponibles.getValueAt(i, 3).toString());
                    int nuevoStock = stockActual - cantidadVendida;
                    modeloProductosDisponibles.setValueAt(String.valueOf(nuevoStock), i, 3);
                    
                    // Si el stock llega a 0, remover de productos disponibles
                    if (nuevoStock <= 0) {
                        modeloProductosDisponibles.removeRow(i);
                    }
                    break;
                }
            }
        }
        
        System.out.println("✅ Stock del producto ID " + idProducto + " actualizado en todas las tablas");
    }
    
    
    // ==================== MÉTODOS DE CARGA DESDE BASE DE DATOS ====================
    
    /**
     * Carga los datos de empleados desde la base de datos
     */
    private void cargarEmpleados() {
        try {
            Connection conn = Conexion.getInstancia().getConnection();
            String sql = "SELECT id, nombre, apellido, email, cargo, telefono, estado FROM empleados ORDER BY id";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();
            
            modeloUsuarios.setRowCount(0);
            
            while (rs.next()) {
                Vector<String> fila = new Vector<>();
                fila.add(String.valueOf(rs.getInt("id")));
                fila.add(rs.getString("nombre"));
                fila.add(rs.getString("apellido"));
                fila.add(rs.getString("email"));
                fila.add(rs.getString("cargo"));
                fila.add(rs.getString("telefono"));
                fila.add(rs.getString("estado"));
                modeloUsuarios.addRow(fila);
            }
            
            rs.close();
            pst.close();
            System.out.println("✅ Empleados cargados desde la base de datos");
        } catch (Exception e) {
            System.err.println("Error cargando empleados: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Carga los datos de productos desde la base de datos
     */
    private void cargarProductos() {
        try {
            Connection conn = Conexion.getInstancia().getConnection();
            String sql = "SELECT id, nombre, precio, stock, id_categoria, descripcion FROM productos ORDER BY id";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();
            
            modeloProductos.setRowCount(0);
            
            while (rs.next()) {
                Vector<String> fila = new Vector<>();
                fila.add(String.valueOf(rs.getInt("id")));
                fila.add(rs.getString("nombre"));
                fila.add(String.format("%.2f", rs.getDouble("precio")));
                fila.add(String.valueOf(rs.getInt("stock")));
                
                int idCategoria = rs.getInt("id_categoria");
                fila.add(rs.wasNull() ? "" : String.valueOf(idCategoria));
                
                fila.add(rs.getString("descripcion") != null ? rs.getString("descripcion") : "");
                modeloProductos.addRow(fila);
            }
            
            rs.close();
            pst.close();
            System.out.println("✅ Productos cargados desde la base de datos");
        } catch (Exception e) {
            System.err.println("Error cargando productos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Carga los datos de clientes desde la base de datos
     */
    private void cargarClientes() {
        try {
            Connection conn = Conexion.getInstancia().getConnection();
            String sql = "SELECT id, nombre, apellido, telefono, email, direccion, ciudad FROM clientes ORDER BY id";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();
            
            modeloClientes.setRowCount(0);
            
            while (rs.next()) {
                Vector<String> fila = new Vector<>();
                fila.add(String.valueOf(rs.getInt("id")));
                fila.add(rs.getString("nombre"));
                fila.add(rs.getString("apellido"));
                fila.add(rs.getString("telefono"));
                fila.add(rs.getString("email"));
                fila.add(rs.getString("direccion") != null ? rs.getString("direccion") : "");
                fila.add(rs.getString("ciudad") != null ? rs.getString("ciudad") : "");
                modeloClientes.addRow(fila);
            }
            
            rs.close();
            pst.close();
            System.out.println("✅ Clientes cargados desde la base de datos");
        } catch (Exception e) {
            System.err.println("Error cargando clientes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Carga los datos de categorías desde la base de datos
     */
    private void cargarCategorias() {
        try {
            Connection conn = Conexion.getInstancia().getConnection();
            String sql = "SELECT id, nombre, descripcion, producto FROM categorias ORDER BY id";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();
            
            modeloCategorias.setRowCount(0);
            
            while (rs.next()) {
                Vector<String> fila = new Vector<>();
                fila.add(String.valueOf(rs.getInt("id")));
                fila.add(rs.getString("nombre"));
                fila.add(rs.getString("descripcion") != null ? rs.getString("descripcion") : "");
                fila.add(rs.getString("producto") != null ? rs.getString("producto") : "");
                modeloCategorias.addRow(fila);
            }
            
            rs.close();
            pst.close();
            System.out.println("✅ Categorías cargadas desde la base de datos");
        } catch (Exception e) {
            System.err.println("Error cargando categorías: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Sincroniza un producto recién agregado con otras tablas
     * Agrega el producto a la tabla de Ventas Y agrega una fila con datos de ejemplo a Empleados y Clientes
     */
    private void sincronizarProductoEnOtrasTablas(int idProducto, String nombre, double precio, int stock) {
        try {
            // === AGREGAR FILA CON DATOS DE EJEMPLO A LA TABLA DE EMPLEADOS ===
            if (modeloUsuarios != null) {
                // Calcular el siguiente ID basado en el número de filas existentes
                int nuevoIdEmpleado = modeloUsuarios.getRowCount() + 1;
                
                Vector<String> filaEmpleado = new Vector<>();
                filaEmpleado.add(String.valueOf(nuevoIdEmpleado));  // ID auto-incrementado
                filaEmpleado.add("nombre");                          // Nombre de ejemplo
                filaEmpleado.add("apellido");                        // Apellido de ejemplo
                filaEmpleado.add("email");                           // Email de ejemplo
                filaEmpleado.add("cargo");                           // Cargo de ejemplo
                filaEmpleado.add("123");                             // Teléfono de ejemplo
                filaEmpleado.add("1");                               // Estado de ejemplo
                modeloUsuarios.addRow(filaEmpleado);
                System.out.println("✅ Fila de ejemplo agregada a tabla Empleados (ID: " + nuevoIdEmpleado + ")");
            }
            
            // === AGREGAR FILA CON DATOS DE EJEMPLO A LA TABLA DE CLIENTES ===
            if (modeloClientes != null) {
                // Calcular el siguiente ID basado en el número de filas existentes
                int nuevoIdCliente = modeloClientes.getRowCount() + 1;
                
                Vector<String> filaCliente = new Vector<>();
                filaCliente.add(String.valueOf(nuevoIdCliente));  // ID auto-incrementado
                filaCliente.add("nombre");                         // Nombre de ejemplo
                filaCliente.add("apellido");                       // Apellido de ejemplo
                filaCliente.add("1");                              // Teléfono de ejemplo (como en la imagen)
                filaCliente.add("email");                          // Email de ejemplo (como en la imagen)
                filaCliente.add("dicreccion");                     // Dirección de ejemplo (como en la imagen)
                filaCliente.add("mexico");                         // Ciudad de ejemplo (como en la imagen)
                modeloClientes.addRow(filaCliente);
                System.out.println("✅ Fila de ejemplo agregada a tabla Clientes (ID: " + nuevoIdCliente + ")");
            }
            
            // === SINCRONIZAR CON VENTAS ===
            if (modeloProductosDisponibles != null) {
                // Verificar si el producto ya existe en la tabla
                boolean existe = false;
                for (int i = 0; i < modeloProductosDisponibles.getRowCount(); i++) {
                    Object nombreObj = modeloProductosDisponibles.getValueAt(i, 1);
                    if (nombreObj != null && nombreObj.toString().equals(nombre)) {
                        existe = true;
                        break;
                    }
                }
                
                // Solo agregar si no existe
                if (!existe) {
                    Vector<String> filaVenta = new Vector<>();
                    filaVenta.add(String.valueOf(nextIdVentaProducto));
                    filaVenta.add(nombre);
                    filaVenta.add(String.format("%.2f", precio));
                    filaVenta.add(String.valueOf(stock));
                    modeloProductosDisponibles.addRow(filaVenta);
                    
                    System.out.println("✅ Producto '" + nombre + "' sincronizado con Ventas");
                    System.out.println("   → ID en Ventas: " + nextIdVentaProducto + " | ID en Productos: " + idProducto);
                    
                    nextIdVentaProducto++;
                }
            }
        } catch (Exception e) {
            System.err.println("Error sincronizando producto: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Sincroniza una categoría con otras tablas si es necesario
     */
    private void sincronizarCategoriaEnOtrasTablas(int id, String nombre) {
        // Placeholder para futura sincronización de categorías
        System.out.println("✅ Categoría '" + nombre + "' con ID " + id + " registrada");
    }
    
    /**
     * Sincroniza un cliente con otras tablas si es necesario
     */
    private void sincronizarClienteEnOtrasTablas(int id, String nombre, String apellido) {
        // Placeholder para futura sincronización de clientes
        System.out.println("✅ Cliente '" + nombre + " " + apellido + "' con ID " + id + " registrado");
    }
    
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Tod window = new Tod();
                window.setNombreUsuario("John Doe");
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

