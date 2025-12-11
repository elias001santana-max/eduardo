package comoproyect;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.util.Vector;
import java.awt.EventQueue;

public class Tod {

    // Variables de sesión para Corte de Caja
    private int ventasHoy = 0;
    private int clientesHoy = 0;
    private double gananciasHoy = 0.0; // Total de ganancias del día
    // Mapa para rastrear ventas por categoría en la sesión actual: NombreCategoria -> CantidadVendida
    private java.util.HashMap<String, Integer> ventasPorCategoriaHoy = new java.util.HashMap<>();

    public JFrame frame;
    private String nombreUsuario = "Usuario";
    
    // Componentes de la tabla de usuarios
    private DefaultTableModel modeloUsuarios;
    private JTable tablaUsuarios;
    private JTextField txtIdUsuario, txtNombreUsuario, txtApellidoUsuario, txtEmailUsuario, txtCargoUsuario, txtTelefonoUsuario;
    
    // Componentes de la tabla de productos
    private DefaultTableModel modeloProductos;
    private JTable tablaProductos;
    private JTextField txtIdProducto, txtNombreProducto, txtPrecio, txtStock, txtEstado;
    private JComboBox<String> comboCategoria; // JComboBox para categorías
    
    // Componentes de la tabla de clientes
    private DefaultTableModel modeloClientes;
    private JTable tablaClientes;
    private JTextField txtIdCliente, txtNombreCliente, txtApellidoCliente, txtTelefonoCliente, txtEmailCliente, txtDireccionCliente, txtCiudadCliente;
    
    // Componentes de la tabla de categorías
    private DefaultTableModel modeloCategorias;
    private JTable tablaCategorias;
    private JTextField txtIdCategoriaCat, txtNombreCategoria, txtDescripcionCategoria, txtProductoCategoria, txtStockCategoria;
    
    // Variables para preservar el estado de categorías entre cambios de panel
    private String categoriaIdPreservado = "";
    private String categoriaNombrePreservado = "";
    private String categoriaDescripcionPreservado = "";
    private String categoriaProductoPreservado = "";
    private String categoriaStockPreservado = "";
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
    
    // Variable para el último empleado agregado (se usa en Corte de Caja)
    private String ultimoEmpleadoAgregado = "";
    
    // Variable para la última categoría agregada (se usa en Corte de Caja)
    private String ultimaCategoriaAgregada = "";
    
    // Variable para la hora de la última venta finalizada (se usa en Corte de Caja)
    private String horaUltimaVenta = "";
    
    // Variables para información de pago (se usan en el ticket PDF)
    private double dineroAPagar = 0.0;
    private double dineroDado = 0.0;
    private double cambio = 0.0;


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
        // F3 = Producto, F4 = Empleado, F5 = Cliente, F6 = Ventas, F7 = Categoría, F8 = Configuración
        // F10 = Buscar Productos, F12 = Finalizar Venta
        // Flecha Arriba = Incrementar cantidad en carrito, Flecha Abajo = Decrementar cantidad en carrito
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
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
                        case KeyEvent.VK_F8:
                            mostrarPanelConfiguracion();
                            return true;
                        case KeyEvent.VK_F12:
                            finalizarVenta();
                            return true;
                        case KeyEvent.VK_F10:
                            mostrarDialogoBusqueda();
                            return true;
                        case KeyEvent.VK_UP:
                            incrementarCantidadCarrito();
                            return true;
                        case KeyEvent.VK_DOWN:
                            decrementarCantidadCarrito();
                            return true;
                    }
                }
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
        String[] menuItems = {"Producto (F3)", "Empleado (F4)", "Cliente (F5)", "Ventas (F6)", "Categoría (F7)", "Configuración (F8)"};

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
                    } else if (itemName.startsWith("Producto")) {
                        mostrarPanelProductos();
                    } else if (itemName.startsWith("Empleado")) {
                        mostrarPanelUsuarios();
                    } else if (itemName.startsWith("Cliente")) {
                        mostrarPanelClientes();
                    } else if (itemName.startsWith("Ventas")) {
                        mostrarPanelVentas();
                    } else if (itemName.startsWith("Categoría")) {
                        mostrarPanelCategorias();
                    } else if (itemName.startsWith("Configuración")) {
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
        btnCerrar.addActionListener(e -> frame.dispose());
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
                
                // Insertar en la base de datos
                Connection conn = Conexion.getInstancia().getConnection();
                String sql = "INSERT INTO empleados (nombre, apellido, email, cargo, telefono) VALUES (?, ?, ?, ?, ?)";
                java.sql.PreparedStatement pst = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
                pst.setString(1, nombre);
                pst.setString(2, apellido);
                pst.setString(3, email);
                pst.setString(4, cargo);
                pst.setString(5, telefono);
                
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
                    modeloUsuarios.addRow(fila);
                    
                    // 🔄 SINCRONIZACIÓN AUTOMÁTICA
                    sincronizarEmpleadoEnOtrasTablas(nuevoId, nombre, apellido);
                    
                    // 💾 GUARDAR Último empleado agregado para Corte de Caja
                    ultimoEmpleadoAgregado = nombre + " " + apellido;
                    
                    limpiarCampos();
                    JOptionPane.showMessageDialog(frame, 
                        "Empleado agregado exitosamente con ID: " + nuevoId + "\n✅ Guardado en base de datos\n✅ Este empleado aparecerá en el Corte de Caja", 
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
                    
                    Connection conn = Conexion.getInstancia().getConnection();
                    
                    // Primero verificar si el registro existe en la base de datos
                    String sqlCheck = "SELECT COUNT(*) FROM empleados WHERE id=?";
                    java.sql.PreparedStatement pstCheck = conn.prepareStatement(sqlCheck);
                    pstCheck.setInt(1, id);
                    java.sql.ResultSet rsCheck = pstCheck.executeQuery();
                    rsCheck.next();
                    int count = rsCheck.getInt(1);
                    rsCheck.close();
                    pstCheck.close();
                    
                    int filasAfectadas = 0;
                    
                    if (count > 0) {
                        // El registro existe - ACTUALIZAR
                        String sql = "UPDATE empleados SET nombre=?, apellido=?, email=?, cargo=?, telefono=? WHERE id=?";
                        java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                        pst.setString(1, nombre);
                        pst.setString(2, apellido);
                        pst.setString(3, email);
                        pst.setString(4, cargo);
                        pst.setString(5, telefono);
                        pst.setInt(6, id);
                        filasAfectadas = pst.executeUpdate();
                        pst.close();
                    } else {
                        // El registro NO existe - INSERTAR con el ID específico
                        String sql = "INSERT INTO empleados (id, nombre, apellido, email, cargo, telefono) VALUES (?, ?, ?, ?, ?, ?)";
                        java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                        pst.setInt(1, id);
                        pst.setString(2, nombre);
                        pst.setString(3, apellido);
                        pst.setString(4, email);
                        pst.setString(5, cargo);
                        pst.setString(6, telefono);
                        filasAfectadas = pst.executeUpdate();
                        pst.close();
                    }
                    
                    if (filasAfectadas > 0) {
                        // Actualizar en la tabla visual
                        modeloUsuarios.setValueAt(String.valueOf(id), fila, 0);
                        modeloUsuarios.setValueAt(nombre, fila, 1);
                        modeloUsuarios.setValueAt(apellido, fila, 2);
                        modeloUsuarios.setValueAt(email, fila, 3);
                        modeloUsuarios.setValueAt(cargo, fila, 4);
                        modeloUsuarios.setValueAt(telefono, fila, 5);
                        
                        limpiarCampos();
                        JOptionPane.showMessageDialog(frame, 
                            "Empleado " + (count > 0 ? "actualizado" : "guardado") + " exitosamente en la base de datos", 
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    }
                    
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
        tablaUsuarios.clearSelection();
    }
    
    private boolean validarCampos() {
        if (txtNombreUsuario.getText().trim().isEmpty() || 
            txtApellidoUsuario.getText().trim().isEmpty() || 
            txtEmailUsuario.getText().trim().isEmpty() || 
            txtCargoUsuario.getText().trim().isEmpty() || 
            txtTelefonoUsuario.getText().trim().isEmpty()) {
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
        
        // Fila 2: Categoría, Descripción
        JLabel lblCategoria = new JLabel("Categoría:");
        lblCategoria.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCategoria.setBounds(20, 95, 120, 25);
        formPanel.add(lblCategoria);
        
        // JComboBox para categorías (se carga dinámicamente desde la BD)
        comboCategoria = new JComboBox<>();
        comboCategoria.setBounds(20, 120, 200, 35);
        comboCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Cargar categorías desde la base de datos
        try {
            Connection conn = Conexion.getInstancia().getConnection();
            String sql = "SELECT id, nombre FROM categorias ORDER BY nombre";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();
            
            comboCategoria.addItem("-- Seleccionar --");
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                comboCategoria.addItem(id + " - " + nombre);
            }
            
            rs.close();
            pst.close();
        } catch (Exception e) {
            System.err.println("Error cargando categorías: " + e.getMessage());
            comboCategoria.addItem("Error al cargar");
        }
        
        formPanel.add(comboCategoria);
        
        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDescripcion.setBounds(240, 95, 100, 25);
        formPanel.add(lblDescripcion);
        
        txtEstado = new JTextField();
        txtEstado.setBounds(240, 120, 420, 35);
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
                    
                    // Seleccionar categoría en el JComboBox
                    String idCategoriaStr = tablaProductos.getValueAt(fila, 3).toString();
                    if (idCategoriaStr != null && !idCategoriaStr.isEmpty()) {
                        // Buscar el item que comienza con este ID
                        for (int i = 0; i < comboCategoria.getItemCount(); i++) {
                            String item = comboCategoria.getItemAt(i);
                            if (item.startsWith(idCategoriaStr + " - ")) {
                                comboCategoria.setSelectedIndex(i);
                                break;
                            }
                        }
                    } else {
                        comboCategoria.setSelectedIndex(0); // Seleccionar "-- Seleccionar --"
                    }
                    
                    txtEstado.setText(tablaProductos.getValueAt(fila, 4).toString());
                    
                    // ✨ SINCRONIZAR CON VENTAS: Agregar producto a tabla de ventas
                    if (modeloProductosDisponibles != null) {
                        try {
                            int idProducto = Integer.parseInt(tablaProductos.getValueAt(fila, 0).toString());
                            String nombre = tablaProductos.getValueAt(fila, 1).toString();
                            double precio = Double.parseDouble(tablaProductos.getValueAt(fila, 2).toString());
                            int stock = 100; // Stock por defecto ya que no se muestra en la tabla
                            
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
        modeloProductos.addRow(new Object[]{"1", "Laptop Dell XPS 15", "1299.99", "15", "Laptop de alto rendimiento con pantalla 4K"});
        modeloProductos.addRow(new Object[]{"2", "Mouse Logitech MX Master", "99.99", "50", "Mouse ergonómico inalámbrico"});
        modeloProductos.addRow(new Object[]{"3", "Teclado Mecánico Corsair", "149.99", "30", "Teclado mecánico RGB retroiluminado"});
        modeloProductos.addRow(new Object[]{"4", "Monitor Samsung 27\"", "349.99", "8", "Monitor curvo Full HD 144Hz"});
        modeloProductos.addRow(new Object[]{"5", "Auriculares Sony WH-1000XM4", "279.99", "12", "Auriculares con cancelación de ruido"});
        modeloProductos.addRow(new Object[]{"6", "SSD Samsung 1TB", "89.99", "100", "Disco sólido NVMe de alta velocidad"});
        nextIdProducto = 7; // Siguiente ID disponible
    }
    
    private void agregarProducto() {
        if (validarCamposProducto()) {
            try {
                String nombre = txtNombreProducto.getText();
                double precio = Double.parseDouble(txtPrecio.getText());
                int stock = 100; // Stock por defecto
                
                // Obtener ID de categoría del JComboBox
                String categoriaSeleccionada = (String) comboCategoria.getSelectedItem();
                Integer idCategoria = null;
                String idCategoriaStr = "";
                
                if (categoriaSeleccionada != null && !categoriaSeleccionada.equals("-- Seleccionar --") && !categoriaSeleccionada.equals("Error al cargar")) {
                    // Extraer el ID de la cadena "ID - Nombre"
                    String[] partes = categoriaSeleccionada.split(" - ");
                    if (partes.length > 0) {
                        try {
                            idCategoria = Integer.parseInt(partes[0].trim());
                            idCategoriaStr = String.valueOf(idCategoria);
                        } catch (NumberFormatException e) {
                            // Si no se puede parsear, dejar como null
                        }
                    }
                }
                
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
                    
                    // Agregar a la tabla visual (sin columna Stock)
                    Vector<String> fila = new Vector<>();
                    fila.add(String.valueOf(nuevoId));
                    fila.add(nombre);
                    fila.add(String.format("%.2f", precio));
                    fila.add(idCategoriaStr);
                    fila.add(descripcion);
                    modeloProductos.addRow(fila);
                    
                    // 🔄 SINCRONIZACIÓN AUTOMÁTICA: Agregar a ventas
                    sincronizarProductoEnOtrasTablas(nuevoId, nombre, precio, stock);
                    
                    // 💾 GUARDAR Última categoría agregada para Corte de Caja
                    if (idCategoria != null) {
                        // Obtener el nombre de la categoría desde la base de datos
                        String sqlCat = "SELECT nombre FROM categorias WHERE id = ?";
                        java.sql.PreparedStatement pstCat = conn.prepareStatement(sqlCat);
                        pstCat.setInt(1, idCategoria);
                        java.sql.ResultSet rsCat = pstCat.executeQuery();
                        if (rsCat.next()) {
                            ultimaCategoriaAgregada = rsCat.getString("nombre");
                            System.out.println("✅ Categoría guardada para Corte de Caja: " + ultimaCategoriaAgregada);
                        }
                        rsCat.close();
                        pstCat.close();
                    } else {
                        System.out.println("⚠️ No se seleccionó categoría para el producto");
                    }
                    
                    limpiarCamposProducto();
                    JOptionPane.showMessageDialog(frame, 
                        "Producto agregado exitosamente con ID: " + nuevoId + "\n✅ Guardado en base de datos" + 
                        (ultimaCategoriaAgregada.isEmpty() ? "" : "\n✅ Categoría '" + ultimaCategoriaAgregada + "' aparecerá en el Corte de Caja"), 
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
                    int stock = 100; // Stock por defecto
                    
                    // Obtener ID de categoría del JComboBox
                    String categoriaSeleccionada = (String) comboCategoria.getSelectedItem();
                    Integer idCategoria = null;
                    String idCategoriaStr = "";
                    
                    if (categoriaSeleccionada != null && !categoriaSeleccionada.equals("-- Seleccionar --") && !categoriaSeleccionada.equals("Error al cargar")) {
                        // Extraer el ID de la cadena "ID - Nombre"
                        String[] partes = categoriaSeleccionada.split(" - ");
                        if (partes.length > 0) {
                            try {
                                idCategoria = Integer.parseInt(partes[0].trim());
                                idCategoriaStr = String.valueOf(idCategoria);
                            } catch (NumberFormatException e) {
                                // Si no se puede parsear, dejar como null
                            }
                        }
                    }
                    
                    String descripcion = txtEstado.getText();
                    
                    // Actualizar en la base de datos
                    Connection conn = Conexion.getInstancia().getConnection();
                    String sql = "UPDATE productos SET nombre=?, precio=?, stock=?, id_categoria=?, descripcion=? WHERE id=?";
                    java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, nombre);
                    pst.setDouble(2, precio);
                    pst.setInt(3, stock); // Stock se actualiza con el valor por defecto
                    if (idCategoria != null) {
                        pst.setInt(4, idCategoria);
                    } else {
                        pst.setNull(4, java.sql.Types.INTEGER);
                    }
                    pst.setString(5, descripcion);
                    pst.setInt(6, id);
                    
                    int filasAfectadas = pst.executeUpdate();
                    
                    if (filasAfectadas > 0) {
                        // Actualizar en la tabla visual (sin columna Stock)
                        modeloProductos.setValueAt(String.valueOf(id), fila, 0);
                        modeloProductos.setValueAt(nombre, fila, 1);
                        modeloProductos.setValueAt(String.format("%.2f", precio), fila, 2);
                        modeloProductos.setValueAt(idCategoriaStr, fila, 3);
                        modeloProductos.setValueAt(descripcion, fila, 4);
                        
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
        comboCategoria.setSelectedIndex(0); // Resetear a "-- Seleccionar --"
        txtEstado.setText("");
        tablaProductos.clearSelection();
    }
    
    private boolean validarCamposProducto() {
        if (txtNombreProducto.getText().trim().isEmpty() || 
            txtPrecio.getText().trim().isEmpty() || 
            txtEstado.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Nombre, Precio y Descripción son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validar que se haya seleccionado una categoría
        String categoriaSeleccionada = (String) comboCategoria.getSelectedItem();
        if (categoriaSeleccionada == null || categoriaSeleccionada.equals("-- Seleccionar --") || categoriaSeleccionada.equals("Error al cargar")) {
            JOptionPane.showMessageDialog(frame, "Debe seleccionar una categoría", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validar que precio sea un número decimal
        try {
            Double.parseDouble(txtPrecio.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "El precio debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
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
        clientesHoy++; // Incrementar contador de clientes hoy
        
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
        
        try {
            int id = Integer.parseInt(txtIdCliente.getText());
            String nombre = txtNombreCliente.getText().trim();
            String apellido = txtApellidoCliente.getText().trim();
            String telefono = txtTelefonoCliente.getText().trim();
            String email = txtEmailCliente.getText().trim();
            String direccion = txtDireccionCliente.getText().trim();
            String ciudad = txtCiudadCliente.getText().trim();
            
            Connection conn = Conexion.getInstancia().getConnection();
            
            // Primero verificar si el registro existe en la base de datos
            String sqlCheck = "SELECT COUNT(*) FROM clientes WHERE id=?";
            java.sql.PreparedStatement pstCheck = conn.prepareStatement(sqlCheck);
            pstCheck.setInt(1, id);
            java.sql.ResultSet rsCheck = pstCheck.executeQuery();
            rsCheck.next();
            int count = rsCheck.getInt(1);
            rsCheck.close();
            pstCheck.close();
            
            int filasAfectadas = 0;
            
            if (count > 0) {
                // El registro existe - ACTUALIZAR
                String sql = "UPDATE clientes SET nombre=?, apellido=?, telefono=?, email=?, direccion=?, ciudad=? WHERE id=?";
                java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, nombre);
                pst.setString(2, apellido);
                pst.setString(3, telefono);
                pst.setString(4, email);
                pst.setString(5, direccion);
                pst.setString(6, ciudad);
                pst.setInt(7, id);
                filasAfectadas = pst.executeUpdate();
                pst.close();
            } else {
                // El registro NO existe - INSERTAR con el ID específico
                String sql = "INSERT INTO clientes (id, nombre, apellido, telefono, email, direccion, ciudad) VALUES (?, ?, ?, ?, ?, ?, ?)";
                java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                pst.setInt(1, id);
                pst.setString(2, nombre);
                pst.setString(3, apellido);
                pst.setString(4, telefono);
                pst.setString(5, email);
                pst.setString(6, direccion);
                pst.setString(7, ciudad);
                filasAfectadas = pst.executeUpdate();
                pst.close();
            }
            
            if (filasAfectadas > 0) {
                // Actualizar fila en la tabla visual
                modeloClientes.setValueAt(nombre, filaSeleccionada, 1);
                modeloClientes.setValueAt(apellido, filaSeleccionada, 2);
                modeloClientes.setValueAt(telefono, filaSeleccionada, 3);
                modeloClientes.setValueAt(email, filaSeleccionada, 4);
                modeloClientes.setValueAt(direccion, filaSeleccionada, 5);
                modeloClientes.setValueAt(ciudad, filaSeleccionada, 6);
                
                limpiarCamposCliente();
                JOptionPane.showMessageDialog(frame, 
                    "Cliente " + (count > 0 ? "actualizado" : "guardado") + " exitosamente en la base de datos", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, 
                "Error al actualizar cliente: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
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
        
        tablaProductosDisponibles = new JTable(modeloProductosDisponibles);
        tablaProductosDisponibles.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaProductosDisponibles.setRowHeight(30);
        tablaProductosDisponibles.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaProductosDisponibles.getTableHeader().setBackground(new Color(16, 185, 129));
        tablaProductosDisponibles.getTableHeader().setForeground(Color.WHITE);
        
        // ❌ TABLA DE PRODUCTOS DISPONIBLES OCULTADA
        /*
        JScrollPane scrollProductos = new JScrollPane(tablaProductosDisponibles);
        scrollProductos.setBounds(30, 120, 420, 400);
        scrollProductos.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        mainContent.add(scrollProductos);
        */
        
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
        
        // Cargar categorías automáticamente para validación de stock
        cargarCategorias();
        
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
            
            // Obtener el stock disponible de la categoría y su ID
            int stockCategoria = obtenerStockCategoria(id);
            int idCategoria = obtenerIdCategoria(id);
            
            double precio = Double.parseDouble(precioStr);
            
            // Contar cuántos productos de esta categoría ya hay en el carrito
            int totalCategoriaEnCarrito = contarProductosCategoriaEnCarrito(idCategoria);
            
            System.out.println("🔍 VALIDACIÓN:");
            System.out.println("   - Producto ID: " + id);
            System.out.println("   - Categoría ID: " + idCategoria);
            System.out.println("   - Stock categoría: " + stockCategoria);
            System.out.println("   - Total en carrito: " + totalCategoriaEnCarrito);
            System.out.println("   - ¿Puede agregar? " + (totalCategoriaEnCarrito < stockCategoria));
            
            // Validar ANTES de agregar
            if (totalCategoriaEnCarrito >= stockCategoria) {
                System.out.println("❌ BLOQUEADO: Total en carrito (" + totalCategoriaEnCarrito + ") >= Stock (" + stockCategoria + ")");
                JOptionPane.showMessageDialog(frame, 
                    "Stock de categoría agotado.\nStock máximo: " + stockCategoria + "\nYa tienes: " + totalCategoriaEnCarrito + " productos de esta categoría en el carrito.", 
                    "Sin Stock", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            System.out.println("✅ PERMITIDO: Agregando producto al carrito");
            
            // Verificar si el producto ya está en el carrito
            boolean encontrado = false;
            for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
                if (modeloCarrito.getValueAt(i, 0).toString().equals(id)) {
                    // Incrementar cantidad
                    int cantActual = Integer.parseInt(modeloCarrito.getValueAt(i, 3).toString());
                    int nuevaCant = cantActual + 1;
                    double subtotal = precio * nuevaCant;
                    modeloCarrito.setValueAt(nuevaCant, i, 3);
                    modeloCarrito.setValueAt(String.format("%.2f", subtotal), i, 4);
                    encontrado = true;
                    
                    // 📉 REDUCIR STOCK EN LA TABLA DE CATEGORÍAS (cuando se incrementa cantidad)
                    actualizarStockCategoria(id, 1);
                    break;
                }
            }
            
            if (!encontrado) {
                // Agregar nuevo producto al carrito
                double subtotal = precio * 1;
                modeloCarrito.addRow(new Object[]{id, nombre, precioStr, "1", String.format("%.2f", subtotal)});
                
                // 📉 REDUCIR STOCK EN LA TABLA DE CATEGORÍAS (cuando se agrega nuevo producto)
                actualizarStockCategoria(id, 1);
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
    
    private void calcularTotal() {
        totalVenta = 0.0;
        for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
            double subtotal = Double.parseDouble(modeloCarrito.getValueAt(i, 4).toString());
            totalVenta += subtotal;
        }
        
        // Actualizar el label del total si existe
        if (lblTotal != null) {
            lblTotal.setText("$" + String.format("%.2f", totalVenta));
        }
    }
    
    // Método para incrementar cantidad en el carrito (Flecha Arriba)
    private void incrementarCantidadCarrito() {
        if (tablaCarrito == null || modeloCarrito == null) {
            return; // No hacer nada si no estamos en el panel de ventas
        }
        
        int fila = tablaCarrito.getSelectedRow();
        if (fila >= 0) {
            String id = modeloCarrito.getValueAt(fila, 0).toString();
            int cantidadActual = Integer.parseInt(modeloCarrito.getValueAt(fila, 3).toString());
            double precio = Double.parseDouble(modeloCarrito.getValueAt(fila, 2).toString());
            
            // Verificar stock disponible
            int stockDisponible = 0;
            // Stock siempre disponible (sin columna de stock visible)
            stockDisponible = 999;
            
            // Solo incrementar si hay stock disponible
            if (cantidadActual < stockDisponible) {
                int nuevaCant = cantidadActual + 1;
                double subtotal = precio * nuevaCant;
                modeloCarrito.setValueAt(nuevaCant, fila, 3);
                modeloCarrito.setValueAt(String.format("%.2f", subtotal), fila, 4);
                calcularTotal();
            }
        }
    }
    
    // Método para decrementar cantidad en el carrito (Flecha Abajo)
    private void decrementarCantidadCarrito() {
        if (tablaCarrito == null || modeloCarrito == null) {
            return; // No hacer nada si no estamos en el panel de ventas
        }
        
        int fila = tablaCarrito.getSelectedRow();
        if (fila >= 0) {
            int cantidadActual = Integer.parseInt(modeloCarrito.getValueAt(fila, 3).toString());
            
            if (cantidadActual > 1) {
                // Decrementar cantidad
                int nuevaCant = cantidadActual - 1;
                double precio = Double.parseDouble(modeloCarrito.getValueAt(fila, 2).toString());
                double subtotal = precio * nuevaCant;
                modeloCarrito.setValueAt(nuevaCant, fila, 3);
                modeloCarrito.setValueAt(String.format("%.2f", subtotal), fila, 4);
                calcularTotal();
            } else {
                // Si la cantidad es 1, eliminar del carrito
                modeloCarrito.removeRow(fila);
                calcularTotal();
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
        
        // Fila 2: Producto y Stock
        JLabel lblProducto = new JLabel("Producto:");
        lblProducto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblProducto.setBounds(20, 95, 100, 25);
        formPanel.add(lblProducto);
        
        txtProductoCategoria = new JTextField();
        txtProductoCategoria.setBounds(20, 120, 710, 35);
        txtProductoCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtProductoCategoria.setText(categoriaProductoPreservado); // Restaurar valor preservado
        formPanel.add(txtProductoCategoria);
        
        // Stock field removed from UI but stock logic remains in background
        
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
        modeloCategorias.addColumn("Stock"); // Hidden column for internal use
        
        tablaCategorias = new JTable(modeloCategorias);
        tablaCategorias.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaCategorias.setRowHeight(30);
        tablaCategorias.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaCategorias.getTableHeader().setBackground(new Color(139, 92, 246));
        tablaCategorias.getTableHeader().setForeground(Color.WHITE);
        
        // Hide Stock column (column 4) by setting width to 0
        tablaCategorias.getColumnModel().getColumn(4).setMinWidth(0);
        tablaCategorias.getColumnModel().getColumn(4).setMaxWidth(0);
        tablaCategorias.getColumnModel().getColumn(4).setWidth(0);
        
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
                    // Stock field removed from UI
                    
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
        
        // ========== TABLA DE CARRITO ==========
        JLabel lblCarrito = new JLabel("Carrito de Compra");
        lblCarrito.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblCarrito.setForeground(TEXT_DARK);
        lblCarrito.setBounds(30, 80, 300, 30);
        mainContent.add(lblCarrito);
        
        // La tabla usa el modelo que ya inicializamos en el constructor
        // Hacer que solo la columna de Cantidad sea editable
        tablaCarrito = new JTable(modeloCarrito) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Solo la columna de Cantidad (índice 3) es editable
            }
        };
        
        tablaCarrito.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaCarrito.setRowHeight(30);
        tablaCarrito.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaCarrito.getTableHeader().setBackground(new Color(251, 146, 60));
        tablaCarrito.getTableHeader().setForeground(Color.WHITE);
        
        // Agregar listener para detectar cambios en la cantidad
        modeloCarrito.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                
                // Solo procesar si se editó la columna de Cantidad (columna 3)
                if (column == 3 && row >= 0) {
                    try {
                        String idProducto = modeloCarrito.getValueAt(row, 0).toString();
                        int nuevaCantidad = Integer.parseInt(modeloCarrito.getValueAt(row, 3).toString());
                        double precio = Double.parseDouble(modeloCarrito.getValueAt(row, 2).toString());
                        
                        // Obtener la cantidad anterior (antes de la edición)
                        // Para esto necesitamos calcular cuánto cambió
                        int cantidadAnterior = 0;
                        try {
                            double subtotalAnterior = Double.parseDouble(modeloCarrito.getValueAt(row, 4).toString());
                            cantidadAnterior = (int)(subtotalAnterior / precio);
                        } catch (Exception ex) {
                            cantidadAnterior = 0;
                        }
                        
                        // Calcular la diferencia
                        int diferencia = nuevaCantidad - cantidadAnterior;
                        
                        System.out.println("📝 Cantidad editada:");
                        System.out.println("   - Producto ID: " + idProducto);
                        System.out.println("   - Cantidad anterior: " + cantidadAnterior);
                        System.out.println("   - Nueva cantidad: " + nuevaCantidad);
                        System.out.println("   - Diferencia: " + diferencia);
                        
                        // Actualizar subtotal
                        double nuevoSubtotal = precio * nuevaCantidad;
                        modeloCarrito.setValueAt(String.format("%.2f", nuevoSubtotal), row, 4);
                        
                        // Actualizar stock de categoría según la diferencia
                        if (diferencia > 0) {
                            // Se incrementó la cantidad, reducir stock
                            actualizarStockCategoria(idProducto, diferencia);
                        } else if (diferencia < 0) {
                            // Se redujo la cantidad, aumentar stock
                            actualizarStockCategoria(idProducto, diferencia); // diferencia es negativa
                        }
                        
                        // Actualizar total
                        actualizarTotal();
                        
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, 
                            "La cantidad debe ser un número válido", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        JScrollPane scrollCarrito = new JScrollPane(tablaCarrito);
        scrollCarrito.setBounds(30, 120, 890, 280);
        scrollCarrito.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        mainContent.add(scrollCarrito);

        
        // ========== TOTAL Y BOTONES FINALES ==========
        JLabel lblTotalTexto = new JLabel("TOTAL:");
        lblTotalTexto.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotalTexto.setForeground(TEXT_DARK);
        lblTotalTexto.setBounds(640, 420, 100, 30);
        mainContent.add(lblTotalTexto);
        
        lblTotal = new JLabel("$0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTotal.setForeground(new Color(16, 185, 129));
        lblTotal.setBounds(750, 410, 170, 40);
        mainContent.add(lblTotal);
        
        JButton btnFinalizarVenta = new JButton("Finalizar Venta (F12)");
        btnFinalizarVenta.setBounds(560, 470, 360, 50);
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
        
        // Botón Buscar Productos
        JButton btnBuscarProductos = new JButton("🔍 Buscar (F10)");
        btnBuscarProductos.setBounds(250, 520, 180, 40);
        btnBuscarProductos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBuscarProductos.setBackground(new Color(59, 130, 246));
        btnBuscarProductos.setForeground(Color.WHITE);
        btnBuscarProductos.setFocusPainted(false);
        btnBuscarProductos.addActionListener(e -> mostrarDialogoBusqueda());
        mainContent.add(btnBuscarProductos);
        
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
                
                // Obtener el stock disponible de la categoría y su ID
                int stockCategoria = obtenerStockCategoria(id);
                int idCategoria = obtenerIdCategoria(id);
                
                // Contar cuántos productos de esta categoría ya hay en el carrito
                int totalCategoriaEnCarrito = contarProductosCategoriaEnCarrito(idCategoria);
                
                System.out.println("🔍 VALIDACIÓN:");
                System.out.println("   - Producto ID: " + id);
                System.out.println("   - Categoría ID: " + idCategoria);
                System.out.println("   - Stock categoría: " + stockCategoria);
                System.out.println("   - Total en carrito: " + totalCategoriaEnCarrito);
                System.out.println("   - ¿Puede agregar? " + (totalCategoriaEnCarrito < stockCategoria));
                
                // Validar ANTES de agregar
                if (totalCategoriaEnCarrito >= stockCategoria) {
                    System.out.println("❌ BLOQUEADO: Total en carrito (" + totalCategoriaEnCarrito + ") >= Stock (" + stockCategoria + ")");
                    JOptionPane.showMessageDialog(frame, 
                        "Stock de categoría agotado.\nStock máximo: " + stockCategoria + "\nYa tienes: " + totalCategoriaEnCarrito + " productos de esta categoría en el carrito.", 
                        "Sin Stock", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                System.out.println("✅ PERMITIDO: Agregando producto al carrito");
                
                // Verificar si ya está en el carrito
                boolean existe = false;
                for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
                    if (modeloCarrito.getValueAt(i, 0).toString().equals(id)) {
                        // Incrementar cantidad
                        int cantidadActual = Integer.parseInt(modeloCarrito.getValueAt(i, 3).toString());
                        int nuevaCantidad = cantidadActual + 1;
                        double nuevoSubtotal = precio * nuevaCantidad;
                        modeloCarrito.setValueAt(nuevaCantidad, i, 3);
                        modeloCarrito.setValueAt(String.format("%.2f", nuevoSubtotal), i, 4);
                        existe = true;
                        
                        // Reducir stock en categoría
                        actualizarStockCategoria(id, 1);
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
                    
                    // Reducir stock en categoría
                    actualizarStockCategoria(id, 1);
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
    
    /**
     * Muestra un diálogo de pago antes de finalizar la venta
     * @return true si el usuario hace clic en "Siguiente", false si cancela
     */
    private boolean mostrarDialogoPago() {
        // Crear diálogo personalizado
        JDialog dialogoPago = new JDialog(frame, "Pago de Venta", true);
        dialogoPago.setSize(450, 300);
        dialogoPago.setLocationRelativeTo(frame);
        dialogoPago.setLayout(null);
        dialogoPago.getContentPane().setBackground(BG_COLOR);
        
        // Variable para guardar el resultado
        final boolean[] resultado = {false};
        
        // Título
        JLabel lblTitulo = new JLabel("Información de Pago", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(TEXT_DARK);
        lblTitulo.setBounds(20, 20, 410, 35);
        dialogoPago.add(lblTitulo);
        
        // Panel de contenido
        JPanel panelContenido = new JPanel();
        panelContenido.setBackground(CARD_COLOR);
        panelContenido.setBounds(20, 70, 410, 140);
        panelContenido.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        panelContenido.setLayout(null);
        dialogoPago.add(panelContenido);
        
        // Dinero a pagar
        JLabel lblDineroAPagar = new JLabel("Dinero a pagar:");
        lblDineroAPagar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDineroAPagar.setForeground(TEXT_DARK);
        lblDineroAPagar.setBounds(30, 20, 150, 30);
        panelContenido.add(lblDineroAPagar);
        
        JLabel lblTotalAPagar = new JLabel(String.format("$%.2f", totalVenta));
        lblTotalAPagar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalAPagar.setForeground(PRIMARY_COLOR);
        lblTotalAPagar.setBounds(250, 20, 130, 30);
        panelContenido.add(lblTotalAPagar);
        
        // Dinero dado
        JLabel lblDineroDado = new JLabel("Dinero dado:");
        lblDineroDado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDineroDado.setForeground(TEXT_DARK);
        lblDineroDado.setBounds(30, 60, 150, 30);
        panelContenido.add(lblDineroDado);
        
        JTextField txtDineroDado = new JTextField();
        txtDineroDado.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtDineroDado.setBounds(250, 60, 130, 35);
        txtDineroDado.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelContenido.add(txtDineroDado);
        
        // Cambio
        JLabel lblCambio = new JLabel("Cambio:");
        lblCambio.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCambio.setForeground(TEXT_DARK);
        lblCambio.setBounds(30, 100, 150, 30);
        panelContenido.add(lblCambio);
        
        JLabel lblCambioValor = new JLabel("$0.00");
        lblCambioValor.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblCambioValor.setForeground(new Color(34, 197, 94)); // Verde
        lblCambioValor.setBounds(250, 100, 130, 30);
        panelContenido.add(lblCambioValor);
        
        // Listener para calcular cambio automáticamente
        txtDineroDado.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                calcularCambio();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                calcularCambio();
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                calcularCambio();
            }
            
            private void calcularCambio() {
                try {
                    String texto = txtDineroDado.getText().trim();
                    if (texto.isEmpty()) {
                        lblCambioValor.setText("$0.00");
                        lblCambioValor.setForeground(new Color(34, 197, 94));
                        return;
                    }
                    
                    double dineroDado = Double.parseDouble(texto);
                    double cambio = dineroDado - totalVenta;
                    
                    lblCambioValor.setText(String.format("$%.2f", Math.abs(cambio)));
                    
                    // Cambiar color según si es positivo o negativo
                    if (cambio >= 0) {
                        lblCambioValor.setForeground(new Color(34, 197, 94)); // Verde
                    } else {
                        lblCambioValor.setForeground(new Color(239, 68, 68)); // Rojo
                    }
                } catch (NumberFormatException ex) {
                    lblCambioValor.setText("$0.00");
                    lblCambioValor.setForeground(new Color(34, 197, 94));
                }
            }
        });
        
        // Botón Siguiente
        JButton btnSiguiente = new JButton("Siguiente");
        btnSiguiente.setBounds(250, 225, 180, 40);
        btnSiguiente.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSiguiente.setBackground(PRIMARY_COLOR);
        btnSiguiente.setForeground(Color.WHITE);
        btnSiguiente.setFocusPainted(false);
        btnSiguiente.addActionListener(e -> {
            // Guardar valores de pago
            dineroAPagar = totalVenta;
            try {
                String texto = txtDineroDado.getText().trim();
                if (!texto.isEmpty()) {
                    dineroDado = Double.parseDouble(texto);
                    cambio = dineroDado - totalVenta;
                } else {
                    dineroDado = 0.0;
                    cambio = 0.0;
                }
            } catch (NumberFormatException ex) {
                dineroDado = 0.0;
                cambio = 0.0;
            }
            
            resultado[0] = true;
            dialogoPago.dispose();
        });
        dialogoPago.add(btnSiguiente);
        
        // Botón Cancelar
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(60, 225, 180, 40);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancelar.setBackground(new Color(100, 116, 139));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> {
            resultado[0] = false;
            dialogoPago.dispose();
        });
        dialogoPago.add(btnCancelar);
        
        // Mostrar diálogo
        dialogoPago.setVisible(true);
        
        return resultado[0];
    }
    
    private void finalizarVenta() {
        if (modeloCarrito.getRowCount() == 0) {
            JOptionPane.showMessageDialog(frame, "El carrito está vacío", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // ===== MOSTRAR DIÁLOGO DE PAGO PRIMERO =====
        if (!mostrarDialogoPago()) {
            // Si el usuario cancela el diálogo de pago, no continuar
            System.out.println("⚠️ Venta cancelada por el usuario en el diálogo de pago");
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
        
        // Agregar datos de TODOS los clientes
        mensaje.append("\n=== DATOS DE CLIENTES ===\n");
        if (modeloClientes != null && modeloClientes.getRowCount() > 0) {
            for (int i = 0; i < modeloClientes.getRowCount(); i++) {
                String id = modeloClientes.getValueAt(i, 0).toString();
                String nombre = modeloClientes.getValueAt(i, 1).toString();
                String apellido = modeloClientes.getValueAt(i, 2).toString();
                String telefono = modeloClientes.getValueAt(i, 3).toString();
                String email = modeloClientes.getValueAt(i, 4).toString();
                String direccion = modeloClientes.getValueAt(i, 5).toString();
                String ciudad = modeloClientes.getValueAt(i, 6).toString();
                
                mensaje.append(String.format("\nCliente #%s:\n", id));
                mensaje.append(String.format("  Nombre completo: %s %s\n", nombre, apellido));
                mensaje.append(String.format("  Teléfono: %s\n", telefono));
                mensaje.append(String.format("  Email: %s\n", email));
                mensaje.append(String.format("  Dirección: %s\n", direccion));
                mensaje.append(String.format("  Ciudad: %s\n", ciudad));
            }
        }
        
        // Proceder directamente a finalizar la venta sin confirmación
        try {
            // === GUARDAR EN BASE DE DATOS CON HORA EXACTA ===
            Connection conn = Conexion.getInstancia().getConnection();
            conn.setAutoCommit(false); // Iniciar transacción
            
                try {
                    // 1. Obtener ID de cliente (último cliente registrado)
                    int idCliente = -1;
                    if (modeloClientes != null && modeloClientes.getRowCount() > 0) {
                        int lastRow = modeloClientes.getRowCount() - 1;
                        try {
                            int idClienteTemp = Integer.parseInt(modeloClientes.getValueAt(lastRow, 0).toString());
                            
                            // Validar que el cliente existe en la base de datos
                            String sqlCheckCliente = "SELECT id FROM clientes WHERE id = ?";
                            java.sql.PreparedStatement pstCheckCliente = conn.prepareStatement(sqlCheckCliente);
                            pstCheckCliente.setInt(1, idClienteTemp);
                            java.sql.ResultSet rsCheckCliente = pstCheckCliente.executeQuery();
                            
                            if (rsCheckCliente.next()) {
                                idCliente = idClienteTemp; // Cliente existe
                            } else {
                                System.out.println("⚠️ Cliente ID " + idClienteTemp + " no existe en la base de datos");
                                idCliente = -1; // No usar este ID
                            }
                            
                            rsCheckCliente.close();
                            pstCheckCliente.close();
                        } catch (Exception e) {
                            idCliente = -1;
                        }
                    }
                    
                    // 2. Obtener ID de empleado (basado en usuario logueado o default)
                    Integer idEmpleado = null; // Permitir NULL si no hay empleado
                    String sqlEmp = "SELECT id FROM empleados WHERE nombre = ? LIMIT 1";
                    java.sql.PreparedStatement pstEmp = conn.prepareStatement(sqlEmp);
                    pstEmp.setString(1, nombreUsuario);
                    java.sql.ResultSet rsEmp = pstEmp.executeQuery();
                    if (rsEmp.next()) {
                        idEmpleado = rsEmp.getInt("id");
                    }
                    rsEmp.close();
                    pstEmp.close();
                    
                    // 3. Insertar Venta con la hora EXACTA del momento del clic
                    String sqlVenta = "INSERT INTO ventas (id_cliente, id_empleado, total, fecha_venta, estado) VALUES (?, ?, ?, NOW(), 'Completada')";
                    java.sql.PreparedStatement pstVenta = conn.prepareStatement(sqlVenta, java.sql.Statement.RETURN_GENERATED_KEYS);
                    if (idCliente != -1) {
                        pstVenta.setInt(1, idCliente);
                    } else {
                        pstVenta.setNull(1, java.sql.Types.INTEGER);
                    }
                    
                    // Permitir NULL para id_empleado si no existe
                    if (idEmpleado != null) {
                        pstVenta.setInt(2, idEmpleado);
                    } else {
                        pstVenta.setNull(2, java.sql.Types.INTEGER);
                    }
                    
                    pstVenta.setDouble(3, totalVenta);
                    
                    pstVenta.executeUpdate();
                    
                    // Obtener ID de la venta generada
                    java.sql.ResultSet rsVenta = pstVenta.getGeneratedKeys();
                    int idVenta = 0;
                    if (rsVenta.next()) {
                        idVenta = rsVenta.getInt(1);
                    }
                    rsVenta.close();
                    pstVenta.close();
                    
                    // 4. Insertar Detalle de Venta
                    String sqlDetalle = "INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
                    java.sql.PreparedStatement pstDetalle = conn.prepareStatement(sqlDetalle);
                    
                    for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
                        // ✅ VALIDAR ID DEL PRODUCTO ANTES DE INTENTAR PARSEARLO
                        String idProductoStr = modeloCarrito.getValueAt(i, 0).toString();
                        int idProd;
                        
                        try {
                            idProd = Integer.parseInt(idProductoStr);
                        } catch (NumberFormatException ex) {
                            conn.rollback();
                            String nombreProductoError = modeloCarrito.getValueAt(i, 1).toString();
                            JOptionPane.showMessageDialog(frame, 
                                "❌ ERROR: El producto '" + nombreProductoError + "' tiene un ID inválido: '" + idProductoStr + "'\n\n" +
                                "Esto puede ocurrir si el producto fue agregado incorrectamente al carrito.\n" +
                                "Por favor, vacía el carrito y agrégalo nuevamente usando el botón 'Buscar'.", 
                                "Error de Producto Inválido", 
                                JOptionPane.ERROR_MESSAGE);
                            return; // Salir sin finalizar la venta
                        }
                        
                        // Validar que el producto existe en la base de datos
                        String sqlCheckProd = "SELECT id FROM productos WHERE id = ?";
                        java.sql.PreparedStatement pstCheck = conn.prepareStatement(sqlCheckProd);
                        pstCheck.setInt(1, idProd);
                        java.sql.ResultSet rsCheck = pstCheck.executeQuery();
                        
                        if (rsCheck.next()) {
                            // El producto existe, proceder con la inserción
                            String precioStr = modeloCarrito.getValueAt(i, 2).toString().replace("$", "").replace(",", "");
                            double precio = Double.parseDouble(precioStr);
                            int cantidad = Integer.parseInt(modeloCarrito.getValueAt(i, 3).toString());
                            String subStr = modeloCarrito.getValueAt(i, 4).toString().replace("$", "").replace(",", "");
                            double subtotal = Double.parseDouble(subStr);
                            
                            pstDetalle.setInt(1, idVenta);
                            pstDetalle.setInt(2, idProd);
                            pstDetalle.setInt(3, cantidad);
                            pstDetalle.setDouble(4, precio);
                            pstDetalle.setDouble(5, subtotal);
                            pstDetalle.addBatch();
                            
                            // Actualizar stock
                            actualizarStockEnTodasLasTablas(idProd, cantidad);
                        } else {
                            System.out.println("⚠️ Producto ID " + idProd + " no existe en la base de datos, omitiendo...");
                        }
                        
                        rsCheck.close();
                        pstCheck.close();
                    }
                    pstDetalle.executeBatch();
                    pstDetalle.close();
                    
                    conn.commit(); // Confirmar transacción
                    System.out.println("✅ Venta guardada en BD con ID: " + idVenta + " - Hora registrada en base de datos");
                    
                } catch (Exception ex) {
                    conn.rollback();
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error al guardar en base de datos: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
                } finally {
                    conn.setAutoCommit(true);
                }

                // ✅ PRIMERO: Generar contenido HTML del ticket
                // Crear archivo HTML temporal para vista previa
                String rutaHTMLTemp = System.getProperty("java.io.tmpdir") + "ticket_temp_" + System.currentTimeMillis() + ".html";
                
                // Crear contenido HTML
                StringBuilder html = new StringBuilder();
                html.append("<!DOCTYPE html>\n");
                html.append("<html>\n<head>\n");
                html.append("<meta charset='UTF-8'>\n");
                html.append("<title>Ticket de Venta</title>\n");
                html.append("<style>\n");
                html.append("body { font-family: Arial, sans-serif; margin: 40px; }\n");
                html.append(".store-header { text-align: center; margin-bottom: 30px; }\n");
                html.append(".store-logo { max-width: 200px; max-height: 150px; margin: 0 auto 15px; display: block; }\n");
                html.append(".store-name { font-size: 24px; font-weight: bold; color: #333; margin-bottom: 20px; }\n");
                html.append("h1 { text-align: center; color: #333; }\n");
                html.append("h2 { color: #555; border-bottom: 2px solid #333; padding-bottom: 5px; }\n");
                html.append(".producto { margin: 10px 0; padding-left: 20px; }\n");
                html.append(".total { text-align: right; font-size: 20px; font-weight: bold; margin: 20px 0; }\n");
                html.append(".cliente { margin: 10px 0; padding-left: 20px; }\n");
                html.append("@media print { button { display: none; } }\n");
                html.append("</style>\n");
                html.append("<script>\n");
                html.append("window.onload = function() {\n");
                html.append("  setTimeout(function() { window.print(); }, 500);\n");
                html.append("};\n");
                html.append("</script>\n");
                html.append("</head>\n<body>\n");
                
                // === ENCABEZADO DE LA TIENDA (Imagen y Nombre) ===
                html.append("<div class='store-header'>\n");
                
                // Agregar imagen de la tienda si existe
                if (rutaImagenTiendaGuardada != null && !rutaImagenTiendaGuardada.isEmpty()) {
                    try {
                        // Convertir la ruta de la imagen a formato file:/// para que funcione en el navegador
                        String rutaImagenURL = new java.io.File(rutaImagenTiendaGuardada).toURI().toString();
                        html.append("<img src='" + rutaImagenURL + "' class='store-logo' alt='Logo de la tienda'>\n");
                    } catch (Exception ex) {
                        System.err.println("Error al agregar imagen al ticket: " + ex.getMessage());
                    }
                }
                
                // Agregar nombre de la tienda si existe
                if (nombreTiendaGuardado != null && !nombreTiendaGuardado.isEmpty()) {
                    html.append("<div class='store-name'>" + nombreTiendaGuardado + "</div>\n");
                }
                
                html.append("</div>\n");
                
                // Título
                html.append("<h1>TICKET DE VENTA</h1>\n");
                html.append("<hr>\n");
                
                // Datos de TODOS los clientes
                html.append("<h2>DATOS DE CLIENTES:</h2>\n");
                if (modeloClientes != null && modeloClientes.getRowCount() > 0) {
                    for (int i = 0; i < modeloClientes.getRowCount(); i++) {
                        String id = modeloClientes.getValueAt(i, 0).toString();
                        String nombre = modeloClientes.getValueAt(i, 1).toString();
                        String apellido = modeloClientes.getValueAt(i, 2).toString();
                        String telefono = modeloClientes.getValueAt(i, 3).toString();
                        String email = modeloClientes.getValueAt(i, 4).toString();
                        String direccion = modeloClientes.getValueAt(i, 5).toString();
                        String ciudad = modeloClientes.getValueAt(i, 6).toString();
                        
                        html.append("<div style='margin: 15px 0; padding: 10px; background: #f5f5f5; border-left: 3px solid #333;'>\n");
                        html.append("<div class='cliente'><strong>Cliente #" + id + "</strong></div>\n");
                        html.append("<div class='cliente'><strong>Nombre completo:</strong> " + nombre + " " + apellido + "</div>\n");
                        html.append("<div class='cliente'><strong>Teléfono:</strong> " + telefono + "</div>\n");
                        html.append("<div class='cliente'><strong>Email:</strong> " + email + "</div>\n");
                        html.append("<div class='cliente'><strong>Dirección:</strong> " + direccion + "</div>\n");
                        html.append("<div class='cliente'><strong>Ciudad:</strong> " + ciudad + "</div>\n");
                        html.append("</div>\n");
                    }
                } else {
                    html.append("<div class='cliente'>No hay clientes registrados</div>\n");
                }
                
                html.append("<hr>\n");
                
                // Productos DESPUÉS
                html.append("<h2>PRODUCTOS:</h2>\n");
                for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
                    String producto = modeloCarrito.getValueAt(i, 1).toString();
                    String cantidad = modeloCarrito.getValueAt(i, 3).toString();
                    String subtotal = modeloCarrito.getValueAt(i, 4).toString();
                    html.append("<div class='producto'>• " + producto + " x" + cantidad + " = $" + subtotal + "</div>\n");
                }
                
                // Total
                html.append("<div class='total'>TOTAL: $" + String.format("%.2f", totalVenta) + "</div>\n");
                html.append("<hr>\n");
                
                // === INFORMACIÓN DE PAGO ===
                html.append("<h2>INFORMACIÓN DE PAGO:</h2>\n");
                html.append("<div style='margin: 15px 0; padding: 15px; background: #f0f9ff; border-left: 4px solid #3b82f6;'>\n");
                html.append("<div style='margin: 8px 0; font-size: 16px;'><strong>Dinero a pagar:</strong> <span style='color: #3b82f6; font-weight: bold;'>$" + String.format("%.2f", dineroAPagar) + "</span></div>\n");
                html.append("<div style='margin: 8px 0; font-size: 16px;'><strong>Dinero dado:</strong> <span style='color: #333; font-weight: bold;'>$" + String.format("%.2f", dineroDado) + "</span></div>\n");
                
                // Color del cambio según si es positivo o negativo
                String colorCambio = cambio >= 0 ? "#22c55e" : "#ef4444"; // Verde o Rojo
                html.append("<div style='margin: 8px 0; font-size: 18px;'><strong>Cambio:</strong> <span style='color: " + colorCambio + "; font-weight: bold;'>$" + String.format("%.2f", Math.abs(cambio)) + "</span></div>\n");
                html.append("</div>\n");
                html.append("<hr>\n");
                
                html.append("</body>\n</html>");
                
                // Guardar archivo HTML temporal
                java.io.FileWriter writer = new java.io.FileWriter(rutaHTMLTemp);
                writer.write(html.toString());
                writer.close();
                
                // Incrementar contadores de sesión
                ventasHoy++;
                gananciasHoy += totalVenta;
                
                // 💾 GUARDAR HORA DE LA VENTA para Corte de Caja
                java.time.LocalTime horaActual = java.time.LocalTime.now();
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");
                horaUltimaVenta = horaActual.format(formatter);
                System.out.println("✅ Hora de venta guardada para Corte de Caja: " + horaUltimaVenta);
                
                // Limpiar carrito
                vaciarCarrito();
                
                // ✅ MOSTRAR VISTA PREVIA PRIMERO (botón Imprimir abrirá el diálogo de guardar)
                mostrarVistaPreviaTicket(html.toString(), rutaHTMLTemp, totalVenta);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, 
                    "Error al generar ticket: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
    }
    
    /**
 * Muestra una vista previa del ticket en un diálogo con botón de impresión
 */
private void mostrarVistaPreviaTicket(String htmlContent, String rutaHTMLTemp, double total) {
    // Crear JDialog modal
    JDialog dialog = new JDialog(frame, "Ticket de Venta", true);
    dialog.setSize(600, 700);
    dialog.setLocationRelativeTo(frame);
    dialog.setLayout(new BorderLayout());
    dialog.getContentPane().setBackground(Color.WHITE);
    
    // Panel superior con información
    JPanel headerPanel = new JPanel();
    headerPanel.setBackground(new Color(59, 130, 246));
    headerPanel.setPreferredSize(new Dimension(600, 80));
    headerPanel.setLayout(null);
    dialog.add(headerPanel, BorderLayout.NORTH);
    
    JLabel lblTitulo = new JLabel("✅ Venta Finalizada");
    lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
    lblTitulo.setForeground(Color.WHITE);
    lblTitulo.setBounds(20, 15, 300, 30);
    headerPanel.add(lblTitulo);
    
    JLabel lblTotal = new JLabel("Total: $" + String.format("%.2f", total));
    lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
    lblTotal.setForeground(Color.WHITE);
    lblTotal.setBounds(20, 45, 300, 25);
    headerPanel.add(lblTotal);
    
    // Panel central con vista previa del ticket (usando JEditorPane para HTML)
    JEditorPane editorPane = new JEditorPane();
    editorPane.setContentType("text/html");
    editorPane.setText(htmlContent);
    editorPane.setEditable(false);
    editorPane.setCaretPosition(0);
    
    JScrollPane scrollPane = new JScrollPane(editorPane);
    scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    dialog.add(scrollPane, BorderLayout.CENTER);
    
    // Panel inferior con botones
    JPanel buttonPanel = new JPanel();
    buttonPanel.setBackground(Color.WHITE);
    buttonPanel.setPreferredSize(new Dimension(600, 80));
    buttonPanel.setLayout(null);
    buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
    dialog.add(buttonPanel, BorderLayout.SOUTH);
    
    // Botón Imprimir → ABRE EL FILEDIAL OG PARA GUARDAR
    JButton btnImprimir = new JButton("🖨️ Imprimir");
    btnImprimir.setBounds(50, 20, 200, 45);
    btnImprimir.setFont(new Font("Segoe UI", Font.BOLD, 16));
    btnImprimir.setBackground(new Color(34, 197, 94));
    btnImprimir.setForeground(Color.WHITE);
    btnImprimir.setFocusPainted(false);
    btnImprimir.setBorder(new RoundBorder(10, new Color(34, 197, 94)));
    btnImprimir.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btnImprimir.addActionListener(e -> {
        try {
            // ✅ AHORA: Abrir FileDialog para que el usuario elija dónde guardar
            java.awt.FileDialog fileDialog = new java.awt.FileDialog(frame, "Guardar Ticket de Venta", java.awt.FileDialog.SAVE);
            fileDialog.setFile("Ticket_Venta_" + System.currentTimeMillis() + ".pdf");
            fileDialog.setVisible(true);
            
            String directorio = fileDialog.getDirectory();
            String archivo = fileDialog.getFile();
            
            if (directorio != null && archivo != null) {
                String rutaPDF = directorio + archivo;
                
                // Asegurar que tenga extensión .pdf
                if (!rutaPDF.toLowerCase().endsWith(".pdf")) {
                    rutaPDF += ".pdf";
                }
                
                // Abrir el HTML en el navegador para imprimir/guardar como PDF
                java.awt.Desktop.getDesktop().browse(new java.io.File(rutaHTMLTemp).toURI());
                
                // Mostrar mensaje con instrucciones
                JOptionPane.showMessageDialog(dialog,
                    "Se abrirá el navegador para imprimir.\n\nEn la ventana de impresión:\n1. Selecciona 'Guardar como PDF'\n2. Guarda en: " + rutaPDF,
                    "Imprimir Ticket",
                    JOptionPane.INFORMATION_MESSAGE);
                    
                System.out.println("📄 PDF se guardará en: " + rutaPDF);
            } else {
                System.out.println("⚠️ Usuario canceló el guardado del ticket");
            }
                
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog,
                "Error al abrir el navegador: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    });
    buttonPanel.add(btnImprimir);
    
    // Botón Cerrar
    JButton btnCerrar = new JButton("Cerrar");
    btnCerrar.setBounds(350, 20, 200, 45);
    btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 16));
    btnCerrar.setBackground(new Color(148, 163, 184));
    btnCerrar.setForeground(Color.WHITE);
    btnCerrar.setFocusPainted(false);
    btnCerrar.setBorder(new RoundBorder(10, new Color(148, 163, 184)));
    btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btnCerrar.addActionListener(e -> dialog.dispose());
    buttonPanel.add(btnCerrar);
    
    // Mostrar diálogo
    dialog.setVisible(true);
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
                int stock = 100; // Default stock value (UI field removed)
                
                // Insertar en la base de datos
                Connection conn = Conexion.getInstancia().getConnection();
                String sql = "INSERT INTO categorias (nombre, descripcion, producto, stock) VALUES (?, ?, ?, ?)";
                java.sql.PreparedStatement pst = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
                pst.setString(1, nombre);
                pst.setString(2, descripcion);
                pst.setString(3, producto);
                pst.setInt(4, stock);
                
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
                    fila.add(String.valueOf(stock)); // Agregar stock a la tabla visual
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
                int stock = 100; // Default stock value (UI field removed)
                
                // Actualizar en la base de datos
                Connection conn = Conexion.getInstancia().getConnection();
                String sql = "UPDATE categorias SET nombre=?, descripcion=?, producto=?, stock=? WHERE id=?";
                java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, nombre);
                pst.setString(2, descripcion);
                pst.setString(3, producto);
                pst.setInt(4, stock); // Actualizar stock en la base de datos
                pst.setInt(5, id);
                    
                    int filasAfectadas = pst.executeUpdate();
                    
                    if (filasAfectadas > 0) {
                        // Actualizar en la tabla visual
                        modeloCategorias.setValueAt(String.valueOf(id), fila, 0);
                        modeloCategorias.setValueAt(nombre, fila, 1);
                        modeloCategorias.setValueAt(descripcion, fila, 2);
                        modeloCategorias.setValueAt(producto, fila, 3);
                        modeloCategorias.setValueAt(String.valueOf(stock), fila, 4); // Actualizar stock en la tabla
                        
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
        // txtStockCategoria removed from UI
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
    
    // ==================== VENTANA DE CORTE DE CAJA ====================
    
    private void mostrarVentanaCorteCaja() {
        // Crear JDialog modal
        JDialog dialog = new JDialog(frame, "Corte de Caja - Reporte del Día", true);
        dialog.setSize(1000, 650);
        dialog.setLocationRelativeTo(frame);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BG_COLOR);
        
        // Panel Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setPreferredSize(new Dimension(1000, 70));
        headerPanel.setLayout(null);
        dialog.add(headerPanel, BorderLayout.NORTH);
        
        JLabel lblHeader = new JLabel("Corte de Caja - Reporte del Día");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeader.setForeground(TEXT_DARK);
        lblHeader.setBounds(30, 20, 500, 30);
        headerPanel.add(lblHeader);
        
        // Tabla con columnas según requerimiento del usuario
        DefaultTableModel modeloCorte = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer tabla no editable
            }
        };
        modeloCorte.addColumn("Clientes del día de hoy");
        modeloCorte.addColumn("Nombre del cliente");
        modeloCorte.addColumn("Hora");
        modeloCorte.addColumn("Empleado");
        modeloCorte.addColumn("Categoría");
        modeloCorte.addColumn("Nombre de producto");
        modeloCorte.addColumn("Stock restante del día de hoy");
        
        JTable tablaCorte = new JTable(modeloCorte);
        tablaCorte.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaCorte.setRowHeight(35);
        tablaCorte.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaCorte.getTableHeader().setBackground(PRIMARY_COLOR);
        tablaCorte.getTableHeader().setForeground(Color.WHITE);
        
        // Ajustar ancho de columnas
        tablaCorte.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablaCorte.getColumnModel().getColumn(1).setPreferredWidth(180);
        tablaCorte.getColumnModel().getColumn(2).setPreferredWidth(80);
        tablaCorte.getColumnModel().getColumn(3).setPreferredWidth(150);
        tablaCorte.getColumnModel().getColumn(4).setPreferredWidth(120);
        tablaCorte.getColumnModel().getColumn(5).setPreferredWidth(180);
        tablaCorte.getColumnModel().getColumn(6).setPreferredWidth(140);
        
        JScrollPane scrollCorte = new JScrollPane(tablaCorte);
        scrollCorte.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        scrollCorte.getViewport().setBackground(BG_COLOR);
        dialog.add(scrollCorte, BorderLayout.CENTER);
        
        // Cargar datos de clientes del día
        cargarDatosClientesDelDia(modeloCorte);
        
        // Panel Footer con Total Vendido y Botón Cerrar
        JPanel footerPanel = new JPanel();
        footerPanel.setPreferredSize(new Dimension(1000, 100));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setLayout(null);
        
        // Línea separadora
        JPanel separador = new JPanel();
        separador.setBackground(new Color(226, 232, 240));
        separador.setBounds(30, 10, 940, 2);
        footerPanel.add(separador);
        
        // Total vendido del día
        JLabel lblTotalTexto = new JLabel("Total vendido el día de hoy:");
        lblTotalTexto.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotalTexto.setForeground(TEXT_DARK);
        lblTotalTexto.setBounds(30, 30, 350, 40);
        footerPanel.add(lblTotalTexto);
        
        JLabel lblTotalValor = new JLabel(String.format("$%.2f", gananciasHoy));
        lblTotalValor.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTotalValor.setForeground(new Color(16, 185, 129)); // Verde
        lblTotalValor.setBounds(400, 25, 350, 50);
        footerPanel.add(lblTotalValor);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(840, 30, 130, 45);
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setBackground(new Color(239, 68, 68));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> dialog.dispose());
        footerPanel.add(btnCerrar);
        
        dialog.add(footerPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    
    /**
     * Carga los datos de clientes del día para el reporte de Corte de Caja
     * Toma los datos directamente de la tabla modeloClientes para actualizarse en tiempo real
     */
    private void cargarDatosClientesDelDia(DefaultTableModel model) {
        try {
            // Calcular stock total restante del día
            int stockTotalRestante = 0;
            if (modeloProductos != null) {
                for (int i = 0; i < modeloProductos.getRowCount(); i++) {
                    try {
                        Object stockObj = modeloProductos.getValueAt(i, 3);
                        if (stockObj != null) {
                            stockTotalRestante += Integer.parseInt(stockObj.toString());
                        }
                    } catch (Exception e) {
                        // Ignorar errores de conversión
                    }
                }
            }
            
            // Obtener categorías de productos desde la base de datos
            String categoriasStr = "N/A";
            
            // Usar la última categoría agregada si existe
            if (!ultimaCategoriaAgregada.isEmpty()) {
                categoriasStr = ultimaCategoriaAgregada;
            }
            
            // Llenar la tabla con datos de clientes desde modeloClientes
            if (modeloClientes != null && modeloClientes.getRowCount() > 0) {
                Connection conn = Conexion.getInstancia().getConnection();
                
                for (int i = 0; i < modeloClientes.getRowCount(); i++) {
                    // Obtener datos de la tabla de clientes
                    String idCliente = modeloClientes.getValueAt(i, 0).toString();  // ID
                    String nombre = modeloClientes.getValueAt(i, 1).toString();     // Nombre
                    String apellido = modeloClientes.getValueAt(i, 2).toString();   // Apellido
                    String nombreCompleto = nombre + " " + apellido;
                    
                    // Obtener la hora de la VENTA del cliente desde la base de datos
                    String horaVenta = "-";
                    String nombreEmpleado = "-";
                    
                    // Usar el último empleado agregado
                    if (!ultimoEmpleadoAgregado.isEmpty()) {
                        nombreEmpleado = ultimoEmpleadoAgregado;
                    }
                    
                    // Usar la hora de la última venta finalizada
                    if (!horaUltimaVenta.isEmpty()) {
                        horaVenta = horaUltimaVenta;
                        System.out.println("✅ Cliente ID " + idCliente + " - Hora: " + horaVenta + " - Empleado: " + nombreEmpleado);
                    } else {
                        System.out.println("⚠️ Cliente ID " + idCliente + " - No hay hora de venta registrada");
                    }
                    
                    
                    // Obtener nombres de productos de la tabla de productos
                    String nombresProductos = "";
                    if (modeloProductos != null && modeloProductos.getRowCount() > 0) {
                        StringBuilder productosBuilder = new StringBuilder();
                        for (int j = 0; j < modeloProductos.getRowCount(); j++) {
                            String nombreProducto = modeloProductos.getValueAt(j, 1).toString(); // Columna 1 es el nombre
                            if (j > 0) {
                                productosBuilder.append(", ");
                            }
                            productosBuilder.append(nombreProducto);
                        }
                        nombresProductos = productosBuilder.toString();
                    }
                    
                    model.addRow(new Object[]{
                        idCliente,                        // ID del cliente (Clientes del día de hoy)
                        nombreCompleto,                   // Nombre del cliente
                        horaVenta,                        // Hora de la VENTA
                        nombreEmpleado,                   // Empleado que procesó la venta
                        categoriasStr,                    // Categorías de productos
                        nombresProductos,                 // Nombres de productos
                        stockTotalRestante                // Stock restante del día de hoy
                    });
                }
                
                System.out.println("✅ Datos de Corte de Caja cargados: " + modeloClientes.getRowCount() + " clientes");
            } else {
                // Si no hay clientes, mostrar mensaje con el empleado
                String empleadoMostrar = ultimoEmpleadoAgregado.isEmpty() ? "-" : ultimoEmpleadoAgregado;
                
                // Obtener nombres de productos para mostrar aunque no haya clientes
                String nombresProductos = "";
                if (modeloProductos != null && modeloProductos.getRowCount() > 0) {
                    StringBuilder productosBuilder = new StringBuilder();
                    for (int j = 0; j < modeloProductos.getRowCount(); j++) {
                        String nombreProducto = modeloProductos.getValueAt(j, 1).toString();
                        if (j > 0) {
                            productosBuilder.append(", ");
                        }
                        productosBuilder.append(nombreProducto);
                    }
                    nombresProductos = productosBuilder.toString();
                }
                
                model.addRow(new Object[]{
                    "Sin clientes",
                    "No hay clientes registrados hoy",
                    "-",
                    empleadoMostrar,  // Mostrar empleado aunque no haya clientes
                    categoriasStr,
                    nombresProductos,
                    stockTotalRestante
                });
            }
            
        } catch (Exception e) {
            System.err.println("Error cargando datos de Corte de Caja: " + e.getMessage());
            e.printStackTrace();
            
            // Mostrar mensaje de error en la tabla
            model.addRow(new Object[]{
                "Error",
                "No se pudieron cargar los datos",
                "-",
                "-",
                "Error: " + e.getMessage(),
                "-",
                0
            });
        }
    }

    private void mostrarDialogoStock() {
        // Crear JDialog modal
        JDialog dialog = new JDialog(frame, "Gestión de Stock por Categoría", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(frame);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BG_COLOR);
        
        // Panel Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setPreferredSize(new Dimension(800, 70));
        headerPanel.setLayout(null);
        dialog.add(headerPanel, BorderLayout.NORTH);
        
        JLabel lblTitulo = new JLabel("Stock de Categorías");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(TEXT_DARK);
        lblTitulo.setBounds(30, 20, 400, 30);
        headerPanel.add(lblTitulo);
        
        // Tabla de stock
        DefaultTableModel modelStock = new DefaultTableModel();
        modelStock.addColumn("ID");
        modelStock.addColumn("Categoría");
        modelStock.addColumn("Descripción");
        modelStock.addColumn("Stock Actual");
        
        JTable tablaStock = new JTable(modelStock) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Solo la columna de Stock es editable
            }
        };
        tablaStock.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaStock.setRowHeight(35);
        tablaStock.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaStock.getTableHeader().setBackground(new Color(16, 185, 129));
        tablaStock.getTableHeader().setForeground(Color.WHITE);
        
        // Cargar datos desde la base de datos
        try {
            Connection conn = Conexion.getInstancia().getConnection();
            String sql = "SELECT id, nombre, descripcion, stock FROM categorias ORDER BY nombre";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                modelStock.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getInt("stock")
                });
            }
            
            rs.close();
            pst.close();
        } catch (Exception e) {
            System.err.println("Error cargando stock: " + e.getMessage());
            e.printStackTrace();
        }
        
        JScrollPane scrollPane = new JScrollPane(tablaStock);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        // Panel Footer con botones
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setPreferredSize(new Dimension(800, 80));
        footerPanel.setLayout(null);
        dialog.add(footerPanel, BorderLayout.SOUTH);
        
        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setBounds(30, 20, 180, 40);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setBackground(PRIMARY_COLOR);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> {
            // 🔧 DETENER EDICIÓN DE CELDA antes de guardar
            if (tablaStock.isEditing()) {
                tablaStock.getCellEditor().stopCellEditing();
            }
            
            // Guardar cambios en la base de datos
            try {
                Connection conn = Conexion.getInstancia().getConnection();
                
                // 🔧 DESACTIVAR autocommit para hacer commit manual
                boolean autoCommitOriginal = conn.getAutoCommit();
                conn.setAutoCommit(false);
                
                int cambiosGuardados = 0;
                
                for (int i = 0; i < modelStock.getRowCount(); i++) {
                    int id = (int) modelStock.getValueAt(i, 0);
                    int nuevoStock = Integer.parseInt(modelStock.getValueAt(i, 3).toString());
                    
                    String sql = "UPDATE categorias SET stock = ? WHERE id = ?";
                    java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setInt(1, nuevoStock);
                    pst.setInt(2, id);
                    int filasActualizadas = pst.executeUpdate();
                    pst.close();
                    
                    if (filasActualizadas > 0) {
                        cambiosGuardados++;
                        System.out.println("✅ Stock actualizado - ID: " + id + ", Nuevo Stock: " + nuevoStock);
                    }
                    
                    // Actualizar también en la tabla visual de categorías si está cargada
                    if (modeloCategorias != null) {
                        for (int j = 0; j < modeloCategorias.getRowCount(); j++) {
                            if (modeloCategorias.getValueAt(j, 0).toString().equals(String.valueOf(id))) {
                                modeloCategorias.setValueAt(String.valueOf(nuevoStock), j, 4);
                                break;
                            }
                        }
                    }
                }
                
                // 🔧 COMMIT EXPLÍCITO para asegurar que se guarden los cambios
                conn.commit();
                
                // 🔧 RESTAURAR autocommit original
                conn.setAutoCommit(autoCommitOriginal);
                
                JOptionPane.showMessageDialog(dialog, 
                    "Stock actualizado exitosamente\n" + 
                    cambiosGuardados + " categoría(s) actualizada(s) en la base de datos", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                System.out.println("✅ " + cambiosGuardados + " cambios guardados y confirmados en la base de datos");
                // NO cerrar el diálogo para que se pueda seguir usando
            } catch (Exception ex) {
                // 🔧 ROLLBACK en caso de error
                try {
                    Connection conn = Conexion.getInstancia().getConnection();
                    conn.rollback();
                    System.err.println("❌ Error al guardar, cambios revertidos");
                } catch (Exception rollbackEx) {
                    System.err.println("❌ Error al hacer rollback: " + rollbackEx.getMessage());
                }
                
                JOptionPane.showMessageDialog(dialog, 
                    "Error al guardar: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        footerPanel.add(btnGuardar);
        
        // Botón Actualizar (recargar datos)
        JButton btnActualizar = new JButton("🔄 Actualizar");
        btnActualizar.setBounds(230, 20, 180, 40);
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnActualizar.setBackground(new Color(59, 130, 246));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.addActionListener(e -> {
            // Recargar datos desde la base de datos
            modelStock.setRowCount(0);
            try {
                Connection conn = Conexion.getInstancia().getConnection();
                String sql = "SELECT id, nombre, descripcion, stock FROM categorias ORDER BY nombre";
                java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                java.sql.ResultSet rs = pst.executeQuery();
                
                while (rs.next()) {
                    modelStock.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getInt("stock")
                    });
                }
                
                rs.close();
                pst.close();
                System.out.println("✅ Stock actualizado desde la base de datos");
            } catch (Exception ex) {
                System.err.println("Error recargando stock: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        footerPanel.add(btnActualizar);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(620, 20, 150, 40);
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setBackground(new Color(100, 116, 139));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> dialog.dispose());
        footerPanel.add(btnCerrar);
        
        dialog.setVisible(true);
    }

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
        
        // Botón Corte de Caja
        JButton btnCorteCaja = new JButton("Corte de Caja");
        btnCorteCaja.setBounds(250, 80, 200, 60); // Positioned next to Tienda button
        btnCorteCaja.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnCorteCaja.setBackground(new Color(59, 130, 246)); // PRIMARY_COLOR
        btnCorteCaja.setForeground(Color.WHITE);
        btnCorteCaja.setFocusPainted(false);
        btnCorteCaja.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCorteCaja.addActionListener(e -> {
            mostrarVentanaCorteCaja();
        });
        panelConfig.add(btnCorteCaja);
        
        // Botón Stock
        JButton btnStock = new JButton("Stock");
        btnStock.setBounds(470, 80, 200, 60); // Positioned next to Corte de Caja button
        btnStock.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnStock.setBackground(new Color(16, 185, 129)); // Green color
        btnStock.setForeground(Color.WHITE);
        btnStock.setFocusPainted(false);
        btnStock.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnStock.addActionListener(e -> {
            mostrarDialogoStock();
        });
        panelConfig.add(btnStock);
        
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
        // Stock column removed - no update needed
        // Products remain visible in sales panel regardless of stock
        
        System.out.println("✅ Stock del producto ID " + idProducto + " actualizado en todas las tablas");
    }
    
    
    // ==================== MÉTODOS DE CARGA DESDE BASE DE DATOS ====================
    
    /**
     * Carga los datos de empleados desde la base de datos
     */
    private void cargarEmpleados() {
        try {
            Connection conn = Conexion.getInstancia().getConnection();
            String sql = "SELECT id, nombre, apellido, email, cargo, telefono FROM empleados ORDER BY id";
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
        // JOIN con la tabla categorias para obtener el nombre de la categoría
        String sql = "SELECT p.id, p.nombre, p.precio, p.stock, p.id_categoria, c.nombre AS nombre_categoria, p.descripcion " +
                     "FROM productos p " +
                     "LEFT JOIN categorias c ON p.id_categoria = c.id " +
                     "ORDER BY p.id";
        java.sql.PreparedStatement pst = conn.prepareStatement(sql);
        java.sql.ResultSet rs = pst.executeQuery();
        
        modeloProductos.setRowCount(0);
        
        while (rs.next()) {
            Vector<String> fila = new Vector<>();
            fila.add(String.valueOf(rs.getInt("id")));
            fila.add(rs.getString("nombre"));
            fila.add(String.format("%.2f", rs.getDouble("precio")));
            // No agregar stock a la tabla visual
            
            // Agregar el NOMBRE de la categoría en lugar del ID
            String nombreCategoria = rs.getString("nombre_categoria");
            fila.add(nombreCategoria != null ? nombreCategoria : "");
            
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
            String sql = "SELECT id, nombre, descripcion, producto, stock FROM categorias ORDER BY id";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();
            
            modeloCategorias.setRowCount(0);
            
            while (rs.next()) {
                Vector<String> fila = new Vector<>();
                fila.add(String.valueOf(rs.getInt("id")));
                fila.add(rs.getString("nombre"));
                fila.add(rs.getString("descripcion") != null ? rs.getString("descripcion") : "");
                fila.add(rs.getString("producto") != null ? rs.getString("producto") : "");
                fila.add(String.valueOf(rs.getInt("stock"))); // Cargar stock desde la base de datos
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
                int filaExistente = -1;
                for (int i = 0; i < modeloProductosDisponibles.getRowCount(); i++) {
                    Object nombreObj = modeloProductosDisponibles.getValueAt(i, 1);
                    if (nombreObj != null && nombreObj.toString().equals(nombre)) {
                        existe = true;
                        filaExistente = i;
                        break;
                    }
                }
                
                // Solo agregar si no existe
                if (!existe) {
                    Vector<String> filaVenta = new Vector<>();
                    filaVenta.add(String.valueOf(idProducto));  // ✅ USAR ID REAL DE LA BASE DE DATOS
                    filaVenta.add(nombre);
                    filaVenta.add(String.format("%.2f", precio));
                    modeloProductosDisponibles.addRow(filaVenta);
                    
                    // ✅ SELECCIONAR AUTOMÁTICAMENTE EL PRODUCTO RECIÉN AGREGADO
                    final int filaAgregada = modeloProductosDisponibles.getRowCount() - 1;
                    if (tablaProductosDisponibles != null) {
                        SwingUtilities.invokeLater(() -> {
                            try {
                                tablaProductosDisponibles.setRowSelectionInterval(filaAgregada, filaAgregada);
                                tablaProductosDisponibles.scrollRectToVisible(
                                    tablaProductosDisponibles.getCellRect(filaAgregada, 0, true)
                                );
                                System.out.println("✅ Producto '" + nombre + "' seleccionado automáticamente en Ventas (fila " + filaAgregada + ")");
                            } catch (Exception ex) {
                                System.err.println("⚠️ No se pudo seleccionar automáticamente: " + ex.getMessage());
                            }
                        });
                    }
                    
                    System.out.println("✅ Producto '" + nombre + "' sincronizado con Ventas");
                    System.out.println("   → ID en Ventas: " + idProducto + " | ID en Productos: " + idProducto);
                } else {
                    // Si ya existe, seleccionarlo también
                    final int filaSeleccionar = filaExistente;
                    if (tablaProductosDisponibles != null) {
                        SwingUtilities.invokeLater(() -> {
                            try {
                                tablaProductosDisponibles.setRowSelectionInterval(filaSeleccionar, filaSeleccionar);
                                tablaProductosDisponibles.scrollRectToVisible(
                                    tablaProductosDisponibles.getCellRect(filaSeleccionar, 0, true)
                                );
                                System.out.println("✅ Producto '" + nombre + "' ya existe - seleccionado en Ventas (fila " + filaSeleccionar + ")");
                            } catch (Exception ex) {
                                System.err.println("⚠️ No se pudo seleccionar automáticamente: " + ex.getMessage());
                            }
                        });
                    }
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
    
    /**
     * Obtiene el ID de categoría de un producto
     */
    private int obtenerIdCategoria(String idProducto) {
        try {
            Connection conn = Conexion.getInstancia().getConnection();
            String sql = "SELECT id_categoria FROM productos WHERE id = ?";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(idProducto));
            java.sql.ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                int idCategoria = rs.getInt("id_categoria");
                rs.close();
                pst.close();
                return idCategoria;
            }
            
            rs.close();
            pst.close();
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo ID de categoría: " + e.getMessage());
        }
        
        return -1;
    }
    
    /**
     * Calcula la cantidad total de productos de una categoría que ya están en el carrito
     */
    private int contarProductosCategoriaEnCarrito(int idCategoria) {
        int totalEnCarrito = 0;
        
        try {
            Connection conn = Conexion.getInstancia().getConnection();
            
            // Recorrer todos los productos en el carrito
            for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
                String idProducto = modeloCarrito.getValueAt(i, 0).toString();
                int cantidad = Integer.parseInt(modeloCarrito.getValueAt(i, 3).toString());
                
                // Verificar si este producto pertenece a la categoría
                String sqlCategoria = "SELECT id_categoria FROM productos WHERE id = ?";
                java.sql.PreparedStatement pst = conn.prepareStatement(sqlCategoria);
                pst.setInt(1, Integer.parseInt(idProducto));
                java.sql.ResultSet rs = pst.executeQuery();
                
                if (rs.next()) {
                    int categoriaProducto = rs.getInt("id_categoria");
                    if (categoriaProducto == idCategoria) {
                        totalEnCarrito += cantidad;
                    }
                }
                
                rs.close();
                pst.close();
            }
            
            System.out.println("📊 Total de productos de categoría " + idCategoria + " en carrito: " + totalEnCarrito);
        } catch (Exception e) {
            System.err.println("❌ Error contando productos en carrito: " + e.getMessage());
        }
        
        return totalEnCarrito;
    }
    
    /**
     * Obtiene el stock disponible de la categoría de un producto
     */
    private int obtenerStockCategoria(String idProducto) {
        try {
            // Obtener la categoría del producto desde la base de datos
            Connection conn = Conexion.getInstancia().getConnection();
            String sqlProducto = "SELECT id_categoria FROM productos WHERE id = ?";
            java.sql.PreparedStatement pstProducto = conn.prepareStatement(sqlProducto);
            pstProducto.setInt(1, Integer.parseInt(idProducto));
            java.sql.ResultSet rsProducto = pstProducto.executeQuery();
            
            if (rsProducto.next()) {
                int idCategoria = rsProducto.getInt("id_categoria");
                rsProducto.close();
                pstProducto.close();
                
                // Obtener el stock ORIGINAL de la categoría desde la BASE DE DATOS
                String sqlStock = "SELECT stock FROM categorias WHERE id = ?";
                java.sql.PreparedStatement pstStock = conn.prepareStatement(sqlStock);
                pstStock.setInt(1, idCategoria);
                java.sql.ResultSet rsStock = pstStock.executeQuery();
                
                if (rsStock.next()) {
                    int stockOriginal = rsStock.getInt("stock");
                    System.out.println("📊 Stock ORIGINAL de categoría ID " + idCategoria + ": " + stockOriginal);
                    rsStock.close();
                    pstStock.close();
                    return stockOriginal;
                }
                
                rsStock.close();
                pstStock.close();
            } else {
                rsProducto.close();
                pstProducto.close();
            }
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo stock de categoría: " + e.getMessage());
        }
        
        // Si no se encuentra, retornar stock ilimitado
        return 999;
    }
    
    /**
 * Actualiza el stock de la categoría cuando se agrega un producto al carrito
 */
private void actualizarStockCategoria(String idProducto, int cantidadReducir) {
    System.out.println("🔍 Iniciando actualizarStockCategoria para producto ID: " + idProducto);
    try {
        // Obtener la categoría del producto desde la base de datos
        Connection conn = Conexion.getInstancia().getConnection();
        String sqlProducto = "SELECT id_categoria FROM productos WHERE id = ?";
        java.sql.PreparedStatement pstProducto = conn.prepareStatement(sqlProducto);
        pstProducto.setInt(1, Integer.parseInt(idProducto));
        java.sql.ResultSet rsProducto = pstProducto.executeQuery();
        
        if (rsProducto.next()) {
            int idCategoria = rsProducto.getInt("id_categoria");
            System.out.println("🔍 Producto pertenece a categoría ID: " + idCategoria);
            
            // Obtener stock actual de la base de datos
            String sqlStock = "SELECT stock FROM categorias WHERE id = ?";
            java.sql.PreparedStatement pstStock = conn.prepareStatement(sqlStock);
            pstStock.setInt(1, idCategoria);
            java.sql.ResultSet rsStock = pstStock.executeQuery();
            
            if (rsStock.next()) {
                int stockActual = rsStock.getInt("stock");
                int nuevoStock = stockActual - cantidadReducir;
                
                System.out.println("📊 Stock actual: " + stockActual + " → Nuevo stock: " + nuevoStock);
                
                // 💾 ACTUALIZAR EN LA BASE DE DATOS
                String sqlUpdate = "UPDATE categorias SET stock = ? WHERE id = ?";
                java.sql.PreparedStatement pstUpdate = conn.prepareStatement(sqlUpdate);
                pstUpdate.setInt(1, nuevoStock);
                pstUpdate.setInt(2, idCategoria);
                pstUpdate.executeUpdate();
                pstUpdate.close();
                
                System.out.println("✅ Stock actualizado en BASE DE DATOS para categoría ID " + idCategoria);
                
                // Actualizar también en la tabla visual si está cargada
                if (modeloCategorias != null) {
                    for (int i = 0; i < modeloCategorias.getRowCount(); i++) {
                        String idCategoriaTabla = modeloCategorias.getValueAt(i, 0).toString();
                        if (idCategoriaTabla.equals(String.valueOf(idCategoria))) {
                            modeloCategorias.setValueAt(String.valueOf(nuevoStock), i, 4);
                            System.out.println("✅ Stock actualizado en TABLA VISUAL");
                            break;
                        }
                    }
                }
            }
            
            rsStock.close();
            pstStock.close();
        } else {
            System.err.println("❌ Producto ID " + idProducto + " no tiene categoría asignada");
        }
        
        rsProducto.close();
        pstProducto.close();
    } catch (Exception e) {
        System.err.println("❌ Error actualizando stock de categoría: " + e.getMessage());
        e.printStackTrace();
    }
    }
    
    /**
     * Muestra un diálogo de búsqueda de productos con filtros
     */
    private void mostrarDialogoBusqueda() {
        // Crear diálogo personalizado
        JDialog dialogoBusqueda = new JDialog(frame, "Buscar Productos", true);
        dialogoBusqueda.setSize(900, 600);
        dialogoBusqueda.setLocationRelativeTo(frame);
        dialogoBusqueda.setLayout(null);
        dialogoBusqueda.getContentPane().setBackground(BG_COLOR);
        
        // Título
        JLabel lblTitulo = new JLabel("Buscar Productos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(TEXT_DARK);
        lblTitulo.setBounds(30, 20, 300, 35);
        dialogoBusqueda.add(lblTitulo);
        
        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel();
        panelBusqueda.setBackground(CARD_COLOR);
        panelBusqueda.setBounds(30, 70, 840, 80);
        panelBusqueda.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        panelBusqueda.setLayout(null);
        dialogoBusqueda.add(panelBusqueda);
        
        JLabel lblBuscar = new JLabel("Nombre del producto:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBuscar.setBounds(20, 15, 200, 25);
        panelBusqueda.add(lblBuscar);
        
        JTextField txtBuscar = new JTextField();
        txtBuscar.setBounds(20, 40, 500, 30);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelBusqueda.add(txtBuscar);
        
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(540, 40, 120, 30);
        btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBuscar.setBackground(PRIMARY_COLOR);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        panelBusqueda.add(btnBuscar);
        
        JButton btnMostrarTodos = new JButton("Mostrar Todos");
        btnMostrarTodos.setBounds(680, 40, 140, 30);
        btnMostrarTodos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnMostrarTodos.setBackground(new Color(100, 116, 139));
        btnMostrarTodos.setForeground(Color.WHITE);
        btnMostrarTodos.setFocusPainted(false);
        panelBusqueda.add(btnMostrarTodos);
        
        // Tabla de resultados
        DefaultTableModel modeloResultados = new DefaultTableModel();
        modeloResultados.addColumn("ID");
        modeloResultados.addColumn("Nombre");
        modeloResultados.addColumn("Precio");
        modeloResultados.addColumn("Categoría");
        modeloResultados.addColumn("Descripción");
        
        JTable tablaResultados = new JTable(modeloResultados);
        tablaResultados.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaResultados.setRowHeight(30);
        tablaResultados.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaResultados.getTableHeader().setBackground(new Color(16, 185, 129));
        tablaResultados.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollResultados = new JScrollPane(tablaResultados);
        scrollResultados.setBounds(30, 170, 840, 320);
        scrollResultados.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        dialogoBusqueda.add(scrollResultados);
        
        // Listener para doble clic en la tabla
        tablaResultados.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tablaResultados.getSelectedRow();
                    if (fila >= 0) {
                        // Agregar producto al carrito
                        String id = modeloResultados.getValueAt(fila, 0).toString();
                        String nombre = modeloResultados.getValueAt(fila, 1).toString();
                        String precio = modeloResultados.getValueAt(fila, 2).toString();
                        
                        // Verificar si el producto ya está en el carrito
                        boolean encontrado = false;
                        for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
                            if (modeloCarrito.getValueAt(i, 0).toString().equals(id)) {
                                // Incrementar cantidad
                                int cantidadActual = Integer.parseInt(modeloCarrito.getValueAt(i, 3).toString());
                                int nuevaCantidad = cantidadActual + 1;
                                double precioUnitario = Double.parseDouble(precio);
                                double nuevoSubtotal = precioUnitario * nuevaCantidad;
                                
                                modeloCarrito.setValueAt(nuevaCantidad, i, 3);
                                modeloCarrito.setValueAt(String.format("%.2f", nuevoSubtotal), i, 4);
                                encontrado = true;
                                break;
                            }
                        }
                        
                        if (!encontrado) {
                            // Agregar nuevo producto
                            double precioUnitario = Double.parseDouble(precio);
                            modeloCarrito.addRow(new Object[]{
                                id, nombre, precio, "1", String.format("%.2f", precioUnitario)
                            });
                        }
                        
                        actualizarTotal();
                        JOptionPane.showMessageDialog(dialogoBusqueda, 
                            "Producto '" + nombre + "' agregado al carrito", 
                            "Éxito", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
        
        // Botón buscar
        btnBuscar.addActionListener(e -> {
            String criterio = txtBuscar.getText().trim();
            buscarProductosPorFiltro(criterio, modeloResultados);
        });
        
        // Agregar listener para búsqueda automática mientras se escribe
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                buscarAutomaticamente();
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                buscarAutomaticamente();
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                buscarAutomaticamente();
            }
            
            private void buscarAutomaticamente() {
                String criterio = txtBuscar.getText().trim();
                buscarProductosPorFiltro(criterio, modeloResultados);
            }
        });
        
        // Botón mostrar todos
        btnMostrarTodos.addActionListener(e -> {
            txtBuscar.setText("");
            buscarProductosPorFiltro("", modeloResultados);
        });
        
        // Botón agregar seleccionado
        JButton btnAgregarSeleccionado = new JButton("Agregar al Carrito");
        btnAgregarSeleccionado.setBounds(550, 510, 180, 40);
        btnAgregarSeleccionado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAgregarSeleccionado.setBackground(new Color(16, 185, 129));
        btnAgregarSeleccionado.setForeground(Color.WHITE);
        btnAgregarSeleccionado.setFocusPainted(false);
        btnAgregarSeleccionado.addActionListener(e -> {
            int fila = tablaResultados.getSelectedRow();
            if (fila >= 0) {
                String id = modeloResultados.getValueAt(fila, 0).toString();
                String nombre = modeloResultados.getValueAt(fila, 1).toString();
                String precio = modeloResultados.getValueAt(fila, 2).toString();
                
                // Verificar si el producto ya está en el carrito
                boolean encontrado = false;
                for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
                    if (modeloCarrito.getValueAt(i, 0).toString().equals(id)) {
                        // Incrementar cantidad
                        int cantidadActual = Integer.parseInt(modeloCarrito.getValueAt(i, 3).toString());
                        int nuevaCantidad = cantidadActual + 1;
                        double precioUnitario = Double.parseDouble(precio);
                        double nuevoSubtotal = precioUnitario * nuevaCantidad;
                        
                        modeloCarrito.setValueAt(nuevaCantidad, i, 3);
                        modeloCarrito.setValueAt(String.format("%.2f", nuevoSubtotal), i, 4);
                        encontrado = true;
                        break;
                    }
                }
                
                if (!encontrado) {
                    // Agregar nuevo producto
                    double precioUnitario = Double.parseDouble(precio);
                    modeloCarrito.addRow(new Object[]{
                        id, nombre, precio, "1", String.format("%.2f", precioUnitario)
                    });
                }
                
                actualizarTotal();
                JOptionPane.showMessageDialog(dialogoBusqueda, 
                    "Producto '" + nombre + "' agregado al carrito", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dialogoBusqueda, 
                    "Seleccione un producto de la lista", 
                    "Advertencia", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        dialogoBusqueda.add(btnAgregarSeleccionado);
        
        // Botón cerrar
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(750, 510, 120, 40);
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setBackground(new Color(239, 68, 68));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> dialogoBusqueda.dispose());
        dialogoBusqueda.add(btnCerrar);
        
        // Cargar todos los productos al abrir
        buscarProductosPorFiltro("", modeloResultados);
        
        dialogoBusqueda.setVisible(true);
    }
    
    /**
     * Busca productos en la base de datos según el criterio de búsqueda
     * @param criterio Texto a buscar en el nombre del producto
     * @param modelo Modelo de tabla donde se mostrarán los resultados
     */
    private void buscarProductosPorFiltro(String criterio, DefaultTableModel modelo) {
        try {
            Connection conn = Conexion.getInstancia().getConnection();
            String sql;
            java.sql.PreparedStatement pst;
            
            if (criterio == null || criterio.trim().isEmpty()) {
                // Mostrar todos los productos
                sql = "SELECT p.id, p.nombre, p.precio, c.nombre AS categoria, p.descripcion " +
                      "FROM productos p " +
                      "LEFT JOIN categorias c ON p.id_categoria = c.id " +
                      "ORDER BY p.nombre";
                pst = conn.prepareStatement(sql);
            } else {
                // Buscar productos con nombre similar
                sql = "SELECT p.id, p.nombre, p.precio, c.nombre AS categoria, p.descripcion " +
                      "FROM productos p " +
                      "LEFT JOIN categorias c ON p.id_categoria = c.id " +
                      "WHERE p.nombre LIKE ? " +
                      "ORDER BY p.nombre";
                pst = conn.prepareStatement(sql);
                pst.setString(1, "%" + criterio + "%");
            }
            
            java.sql.ResultSet rs = pst.executeQuery();
            
            // Limpiar tabla
            modelo.setRowCount(0);
            
            int contador = 0;
            while (rs.next()) {
                Vector<String> fila = new Vector<>();
                fila.add(String.valueOf(rs.getInt("id")));
                fila.add(rs.getString("nombre"));
                fila.add(String.format("%.2f", rs.getDouble("precio")));
                fila.add(rs.getString("categoria") != null ? rs.getString("categoria") : "Sin categoría");
                fila.add(rs.getString("descripcion") != null ? rs.getString("descripcion") : "");
                modelo.addRow(fila);
                contador++;
            }
            
            rs.close();
            pst.close();
            
            System.out.println("✅ Búsqueda completada: " + contador + " productos encontrados");
            
            if (contador == 0) {
                JOptionPane.showMessageDialog(frame, 
                    "No se encontraron productos con el criterio: " + criterio, 
                    "Sin resultados", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error buscando productos: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, 
                "Error al buscar productos: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
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

