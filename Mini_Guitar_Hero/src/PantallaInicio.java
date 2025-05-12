/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import API.*;
import retrofit2.Call;
import retrofit2.Response;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class PantallaInicio  extends JFrame{
    private CardLayout cardLayout;
    private JPanel contenedor;
    private JPanel panelMenu;
    private JPanel panelLogin;
    private ApiService api = RetrofitClient.getApiService();
    private int idNino;
    public PantallaInicio() {
        setTitle("Bienvenido");
        setSize(800, 455); // Tamaño más compacto
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        contenedor = new JPanel(cardLayout);

        crearPanelMenu();
        crearPanelLogin();

        contenedor.add(panelMenu, "menu");
        contenedor.add(panelLogin, "login");

        add(contenedor);
        cardLayout.show(contenedor, "menu");
        setVisible(true);
    }

    private void crearPanelMenu() {
        panelMenu = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image fondo = new ImageIcon(getClass().getResource("/img/Fonfo_MenuTR.png")).getImage();
                g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panelMenu.setLayout(new GridBagLayout());
        panelMenu.setPreferredSize(new Dimension(500, 400));

        JButton btnIniciar = crearBotonEstilo("Iniciar");
        JButton btnSalir = crearBotonEstilo("Salir");

        btnIniciar.addActionListener(e -> cardLayout.show(contenedor, "login"));
        btnSalir.addActionListener(e -> System.exit(0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(115, 0, -110, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelMenu.add(btnIniciar, gbc);
        gbc.gridy++;
        panelMenu.add(btnSalir, gbc);
    }

    private void crearPanelLogin() {
        panelLogin = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image fondo = new ImageIcon(getClass().getResource("/img/Fonfo_MenuTR.png")).getImage();
                g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panelLogin.setLayout(new GridBagLayout());
        panelLogin.setPreferredSize(new Dimension(500, 400));

        JLabel lblCorreo = new JLabel("Correo:");
        lblCorreo.setFont(new Font("Orbitron", Font.BOLD, 18));
        lblCorreo.setForeground(Color.WHITE);

        JTextField txtCorreo = new JTextField(20);
        txtCorreo.setFont(new Font("Orbitron", Font.BOLD, 18));

        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setFont(new Font("Orbitron", Font.BOLD, 18));
        lblPass.setForeground(Color.WHITE);

        JPasswordField txtPass = new JPasswordField(20);
        txtPass.setFont(new Font("Orbitron", Font.BOLD, 18));

        JButton btnEntrar = crearBotonEstilo("Entrar");
        JButton btnVolver = crearBotonEstilo("Volver");

        btnEntrar.addActionListener(e -> {
            String correo = txtCorreo.getText().trim();
            String pass = new String(txtPass.getPassword()).trim();
            if (correo.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, llena todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {

                // 1. Login
                LoginRequest login = new LoginRequest(correo, pass);
                Call<LoginResponse> llamadaLogin = api.login(login);

                Component dialogo = null;
                try {
                    Response<LoginResponse> respuestaLogin = llamadaLogin.execute();
                    if (respuestaLogin.isSuccessful() && respuestaLogin.body() != null && respuestaLogin.body().isSuccess()) {

                        // 2. Buscar niño
                        BuscarNinoRequest buscarRequest = new BuscarNinoRequest(correo);
                        Call<BuscarNinoResponse> llamadaNino = api.buscarNinoId(buscarRequest);
                        Response<BuscarNinoResponse> respuestaNino = llamadaNino.execute();

                        if (respuestaNino.isSuccessful() && respuestaNino.body() != null) {
                            idNino = respuestaNino.body().getId_nino();
                            System.out.println("ID del niño: " + idNino);
                            dispose();
                            lanzarJuego(correo, pass, idNino);
                            
                        } else {
                            JOptionPane.showMessageDialog(dialogo, "No se encontró un niño asociado a este correo.");
                            
                        }

                    } else {
                        JOptionPane.showMessageDialog(dialogo, "Credenciales incorrectas. Intenta nuevamente.");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialogo, "Error al conectar con el servidor.");
                }
            }
        });

        btnVolver.addActionListener(e -> cardLayout.show(contenedor, "menu"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0;
        panelLogin.add(lblCorreo, gbc);
        gbc.gridx = 1; panelLogin.add(txtCorreo, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panelLogin.add(lblPass, gbc);
        gbc.gridx = 1; panelLogin.add(txtPass, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panelLogin.add(btnVolver, gbc);
        gbc.gridx = 1; panelLogin.add(btnEntrar, gbc);
    }

    private JButton crearBotonEstilo(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Orbitron", Font.BOLD, 16));
        boton.setFocusPainted(false);
        boton.setForeground(new Color(60, 60, 60));
        boton.setBackground(new Color(236, 236, 236, 195)); // pastel suave y medio transparente
        boton.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        boton.setPreferredSize(new Dimension(140, 40));
        boton.setContentAreaFilled(true);
        boton.setOpaque(true);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        boton.setUI(new BasicButtonUI());
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)));
        return boton;
    }

    private void lanzarJuego(String correo, String pass, int idNino) {
        JFrame frame = new JFrame("Mini Guitar Hero");
        MiniGuitarHero game = new MiniGuitarHero(correo, pass, idNino);
        frame.add(game);
        frame.setSize(900, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new PantallaInicio();
    }
}
