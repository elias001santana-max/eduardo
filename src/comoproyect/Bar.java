package comoproyect;

import java.awt.*;
import javax.swing.*;

public class Bar {

    public JFrame frame;
    private final JProgressBar progressBar = new JProgressBar();
    private Thread hilo;

    // Colores de tu estilo
    private final Color PRIMARY_COLOR = new Color(230, 81, 0); // Naranja
    private final Color DARK_BG = new Color(33, 33, 33);       // Gris oscuro

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Bar window = new Bar();
                window.startCarga(); // Iniciar barra de carga
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Bar() {
        initialize();
    }

    private void initialize() {

        frame = new JFrame();
        frame.setUndecorated(true);
        frame.setSize(500, 260);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        // Botón cerrar
        JButton btnCerrar = new JButton("X");
        btnCerrar.setBounds(460, 5, 35, 25);
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCerrar.setBackground(new Color(200, 0, 0));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorder(null);
        btnCerrar.addActionListener(e -> frame.dispose());
        frame.add(btnCerrar);

        // Fondo oscuro
        JPanel panelFondo = new JPanel();
        panelFondo.setBackground(DARK_BG);
        panelFondo.setBounds(0, 0, 500, 260);
        panelFondo.setLayout(null);
        frame.add(panelFondo);

        // Tarjeta blanca
        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBounds(60, 40, 380, 170);
        tarjeta.setLayout(null);
        tarjeta.setBorder(new RoundBorder(30));
        panelFondo.add(tarjeta);

        JLabel lblCargando = new JLabel("Cargando...");
        lblCargando.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblCargando.setHorizontalAlignment(SwingConstants.CENTER);
        lblCargando.setBounds(10, 10, 360, 40);
        tarjeta.add(lblCargando);

        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        progressBar.setForeground(PRIMARY_COLOR);
        progressBar.setBackground(Color.WHITE);
        progressBar.setStringPainted(true);
        progressBar.setBounds(30, 70, 320, 40);
        progressBar.setBorder(new RoundBorder(20));
        tarjeta.add(progressBar);
    }

    // Método para iniciar la barra de carga
    public void startCarga() {

        if (hilo != null && hilo.isAlive()) return;

        hilo = new Thread(() -> {
            frame.setVisible(true);

            for (int v = 0; v <= 100; v++) {
                int valor = v;
                SwingUtilities.invokeLater(() -> progressBar.setValue(valor));

                try {
                    Thread.sleep(45);
                } catch (InterruptedException e) {
                    return;
                }
            }

            SwingUtilities.invokeLater(() -> {
                frame.dispose();

                // Aquí podrías abrir tu ventana Inicio o Dashboard
                // new Inicio().frame.setVisible(true);
            });
        });

        hilo.start();
    }

    class RoundBorder extends javax.swing.border.LineBorder {
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
