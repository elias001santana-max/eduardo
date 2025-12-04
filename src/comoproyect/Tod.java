package comoproyect;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class Tod {

    public JFrame frame;
    private String nombreUsuario = "Usuario";
    
    // Colores del Dashboard - AZUL THEME
    private final Color SIDEBAR_COLOR = new Color(30, 41, 59);
    private final Color SIDEBAR_HOVER = new Color(51, 65, 85);
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private final Color ACCENT_COLOR = new Color(251, 146, 60);
    private final Color BG_COLOR = new Color(241, 245, 249);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color TEXT_DARK = new Color(30, 41, 59);
    private final Color TEXT_LIGHT = new Color(148, 163, 184);

    public Tod() {
        initialize();
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Dashboard - Todo");
        frame.setSize(1200, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.getContentPane().setBackground(BG_COLOR);

        // SIDEBAR
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setBounds(0, 0, 250, 700);
        sidebar.setLayout(null);
        frame.add(sidebar);

        // Avatar
        JLabel avatarCircle = new JLabel();
        avatarCircle.setBounds(85, 30, 80, 80);
        avatarCircle.setOpaque(true);
        avatarCircle.setBackground(PRIMARY_COLOR);
        avatarCircle.setHorizontalAlignment(SwingConstants.CENTER);
        avatarCircle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        avatarCircle.setForeground(Color.WHITE);
        avatarCircle.setText(nombreUsuario.substring(0, 1).toUpperCase());
        avatarCircle.setBorder(new RoundBorder(40, PRIMARY_COLOR));
        sidebar.add(avatarCircle);

        JLabel lblNombre = new JLabel(nombreUsuario, SwingConstants.CENTER);
        lblNombre.setBounds(20, 115, 210, 25);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNombre.setForeground(Color.WHITE);
        sidebar.add(lblNombre);

        JLabel lblRole = new JLabel("Administrator", SwingConstants.CENTER);
        lblRole.setBounds(20, 140, 210, 20);
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(TEXT_LIGHT);
        sidebar.add(lblRole);

        // Menú
        String[] menuItems = {"Dashboard", "Usuarios", "Productos", "Ventas", "Reportes", "Configuración"};
        String[] menuIcons = {"🏠", "👥", "📦", "💰", "📊", "⚙️"};
        
        int yPos = 190;
        for (int i = 0; i < menuItems.length; i++) {
            JPanel menuItem = createMenuItem(menuIcons[i], menuItems[i], i == 0);
            menuItem.setBounds(15, yPos, 220, 45);
            sidebar.add(menuItem);
            yPos += 50;
        }

        // CONTENIDO PRINCIPAL
        JPanel mainContent = new JPanel();
        mainContent.setBackground(BG_COLOR);
        mainContent.setBounds(250, 0, 950, 700);
        mainContent.setLayout(null);
        frame.add(mainContent);

        JLabel lblHeader = new JLabel("Dashboard");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblHeader.setForeground(TEXT_DARK);
        lblHeader.setBounds(30, 20, 300, 40);
        mainContent.add(lblHeader);

        JLabel lblDate = new JLabel("Diciembre 03, 2025");
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDate.setForeground(TEXT_LIGHT);
        lblDate.setBounds(30, 55, 300, 25);
        mainContent.add(lblDate);

        // TARJETAS
        createStatCard(mainContent, "Total Usuarios", "628", "↑ 12%", 30, 100, PRIMARY_COLOR);
        createStatCard(mainContent, "Ventas Hoy", "2,434", "↑ 8%", 260, 100, ACCENT_COLOR);
        createStatCard(mainContent, "Productos", "1,259", "↓ 3%", 490, 100, new Color(16, 185, 129));
        createStatCard(mainContent, "Rating", "8.5", "↑ 0.5", 720, 100, new Color(139, 92, 246));

        // GRÁFICA
        JPanel graphPanel = new JPanel();
        graphPanel.setBackground(CARD_COLOR);
        graphPanel.setBounds(30, 280, 580, 350);
        graphPanel.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        graphPanel.setLayout(null);
        mainContent.add(graphPanel);

        JLabel lblGraphTitle = new JLabel("Ventas Mensuales");
        lblGraphTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblGraphTitle.setForeground(TEXT_DARK);
        lblGraphTitle.setBounds(20, 15, 300, 30);
        graphPanel.add(lblGraphTitle);

        JPanel barsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int[] heights = {80, 120, 95, 150, 110, 180, 140, 160, 130, 170, 145, 190};
                int barWidth = 35;
                int spacing = 45;
                
                for (int i = 0; i < heights.length; i++) {
                    if (i % 2 == 0) {
                        g2.setColor(PRIMARY_COLOR);
                    } else {
                        g2.setColor(ACCENT_COLOR);
                    }
                    int x = 20 + (i * spacing);
                    int y = 250 - heights[i];
                    g2.fillRoundRect(x, y, barWidth, heights[i], 8, 8);
                }
            }
        };
        barsPanel.setBackground(CARD_COLOR);
        barsPanel.setBounds(20, 60, 540, 270);
        graphPanel.add(barsPanel);

        // ACTIVIDAD
        JPanel activityPanel = new JPanel();
        activityPanel.setBackground(CARD_COLOR);
        activityPanel.setBounds(630, 280, 290, 350);
        activityPanel.setBorder(new RoundBorder(15, Color.LIGHT_GRAY));
        activityPanel.setLayout(null);
        mainContent.add(activityPanel);

        JLabel lblActivityTitle = new JLabel("Actividad Reciente");
        lblActivityTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblActivityTitle.setForeground(TEXT_DARK);
        lblActivityTitle.setBounds(20, 15, 250, 30);
        activityPanel.add(lblActivityTitle);

        String[] activities = {
            "Nueva venta registrada",
            "Usuario agregado",
            "Producto actualizado",
            "Reporte generado",
            "Configuración cambiada"
        };
        
        int actY = 60;
        for (String activity : activities) {
            JPanel actItem = createActivityItem(activity);
            actItem.setBounds(15, actY, 260, 50);
            activityPanel.add(actItem);
            actY += 55;
        }
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

    private JPanel createMenuItem(String icon, String text, boolean active) {
        JPanel item = new JPanel();
        item.setBackground(active ? SIDEBAR_HOVER : SIDEBAR_COLOR);
        item.setLayout(null);
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        lblIcon.setBounds(15, 0, 40, 45);
        lblIcon.setForeground(Color.WHITE);
        item.add(lblIcon);

        JLabel lblText = new JLabel(text);
        lblText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblText.setBounds(55, 0, 150, 45);
        lblText.setForeground(Color.WHITE);
        item.add(lblText);

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