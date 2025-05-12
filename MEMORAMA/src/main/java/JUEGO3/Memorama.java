package JUEGO3;

import API.*;
import retrofit2.Call;
import retrofit2.Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Vector;
import javax.swing.Timer;

public class Memorama extends JFrame {
    private JPanel panelJuego;
    private JScrollPane scrollPane;
    private JButton[] botones;
    private ImageIcon[] imagenes;
    private int[] indices;
    private boolean[] encontrada;
    private int paresEncontrados = 0;
    private JButton primero = null;
    private int total;



    private int cursorFila = 0;
    private int cursorColumna = 0;
    private final int columnas = 6;

    private ApiService api = RetrofitClient.getApiService();

    private String correoTutor;
    private String contrasenaTutor;
    private long inicioJuego;
    private int puntajeFinal = 0;
    private int nivelAlcanzado = 1;
    private int nivelActual = 1;
    private int idNino = -1;
    private int id_juego = 2;

    public Memorama() {
        solicitarCredenciales();
        setTitle("Memorama");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        cargarImagenesDesdeCarpetaExterna();
        inicializarInterfaz();
        iniciarJuego(imagenes.length * 2);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                if (key == KeyEvent.VK_W && cursorFila > 0) cursorFila--;
                if (key == KeyEvent.VK_S && cursorFila < (total / columnas) - 1) cursorFila++;
                if (key == KeyEvent.VK_A && cursorColumna > 0) cursorColumna--;
                if (key == KeyEvent.VK_D && cursorColumna < columnas - 1) cursorColumna++;

                if (key == KeyEvent.VK_Q) {
                    int index = cursorFila * columnas + cursorColumna;
                    if (index >= 0 && index < botones.length) {
                        mostrarImagen(index);
                    }
                }

                if (key == KeyEvent.VK_E) {
                    mostrarResumenFinal();
                }

                actualizarCursor();
            }
        });

        setFocusable(true);
        SwingUtilities.invokeLater(() -> requestFocusInWindow());

        ReproductorMusica musica = new ReproductorMusica();
        musica.reproducir("src/main/java/imagenes/sonido.wav");
    }

    private void solicitarCredenciales() {

        JDialog dialogo = new JDialog(this, "MemoMovimiento - CXT", true);
        dialogo.setSize(850, 600);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        Color azulFuerte = new Color(21, 127, 201);
        Color blanco = Color.WHITE;

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(azulFuerte);

        // Imagen lateral
        ImageIcon originalIcon = new ImageIcon("src/main/java/imagenes/fondo2.png");
        Image imagenEscalada = originalIcon.getImage().getScaledInstance(500, 500, Image.SCALE_SMOOTH);
        JLabel imagen = new JLabel(new ImageIcon(imagenEscalada));
        imagen.setPreferredSize(new Dimension(400, 600));
        panelPrincipal.add(imagen, BorderLayout.WEST);

        // Formulario
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panelFormulario.setBackground(azulFuerte);

        JLabel titulo = new JLabel("¡Bienvenido a MenoMovimineto!");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(blanco);
        panelFormulario.add(titulo);
        panelFormulario.add(Box.createVerticalStrut(20));

        // Instrucción previa a los campos
        JLabel textoInicio = new JLabel("Ingresa tu correo y contraseña para comenzar.");
        textoInicio.setAlignmentX(Component.CENTER_ALIGNMENT);
        textoInicio.setFont(new Font("Arial", Font.PLAIN, 14));
        textoInicio.setForeground(blanco);
        panelFormulario.add(textoInicio);
        panelFormulario.add(Box.createVerticalStrut(20));

        JTextField correoField = new JTextField();
        correoField.setMaximumSize(new Dimension(250, 30));
        correoField.setBackground(blanco);
        correoField.setForeground(Color.BLACK);

        JPasswordField contrasenaField = new JPasswordField();
        contrasenaField.setMaximumSize(new Dimension(250, 30));
        contrasenaField.setBackground(blanco);
        contrasenaField.setForeground(Color.BLACK);

        JLabel correoLabel = new JLabel("Correo:");
        correoLabel.setForeground(blanco);
        correoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel contrasenaLabel = new JLabel("Contraseña:");
        contrasenaLabel.setForeground(blanco);
        contrasenaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelFormulario.add(correoLabel);
        panelFormulario.add(correoField);
        panelFormulario.add(Box.createVerticalStrut(10));
        panelFormulario.add(contrasenaLabel);
        panelFormulario.add(contrasenaField);
        panelFormulario.add(Box.createVerticalStrut(100));

        JButton btnContinuar = new JButton("Continuar");
        btnContinuar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnContinuar.setBackground(blanco);
        btnContinuar.setForeground(azulFuerte);
        btnContinuar.setFocusPainted(false);
        panelFormulario.add(btnContinuar);

        // Instrucciones debajo del botón con espacio
        panelFormulario.add(Box.createVerticalStrut(30));
        JLabel instrucciones = new JLabel("<html><div style='text-align:center;'>" +
                "Presiona <b>Q</b> para seleccionar una carta.<br>" +
                "Usa <b>W, A, S, D</b> para moverte.<br>" +
                "Presiona <b>E</b> para finalizar el juego." +
                "</div></html>");
        instrucciones.setAlignmentX(Component.CENTER_ALIGNMENT);
        instrucciones.setFont(new Font("Arial", Font.PLAIN, 14));
        instrucciones.setForeground(Color.WHITE);
        panelFormulario.add(instrucciones);

        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);
        dialogo.add(panelPrincipal);

        Runnable intentarContinuar = () -> {
            correoTutor = correoField.getText().trim();
            contrasenaTutor = new String(contrasenaField.getPassword()).trim();

            if (correoTutor.isEmpty() || contrasenaTutor.isEmpty()) {
                JOptionPane.showMessageDialog(dialogo, "Por favor, llena ambos campos.");
                return;
            }



            // 1. Login
            LoginRequest login = new LoginRequest(correoTutor, contrasenaTutor);
            Call<LoginResponse> llamadaLogin = api.login(login);

            try {
                Response<LoginResponse> respuestaLogin = llamadaLogin.execute();
                if (respuestaLogin.isSuccessful() && respuestaLogin.body() != null && respuestaLogin.body().isSuccess()) {

                    // 2. Buscar niño
                    BuscarNinoRequest buscarRequest = new BuscarNinoRequest(correoTutor);
                    Call<BuscarNinoResponse> llamadaNino = api.buscarNinoId(buscarRequest);
                    Response<BuscarNinoResponse> respuestaNino = llamadaNino.execute();

                    if (respuestaNino.isSuccessful() && respuestaNino.body() != null) {
                        idNino = respuestaNino.body().getId_nino();
                        System.out.println("ID del niño: " + idNino);
                        inicioJuego = System.currentTimeMillis();
                        dialogo.dispose();
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
        };

        btnContinuar.addActionListener(e -> intentarContinuar.run());

        dialogo.setVisible(true);

        if (correoTutor == null || contrasenaTutor == null ||
                correoTutor.isEmpty() || contrasenaTutor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se ingresaron datos. El juego no puede continuar.");
            System.exit(0);
        }
    }


    private void cargarImagenesDesdeCarpetaExterna() {
        String carpeta = "src/main/java/imagenes/nivel" + nivelActual + "/";
        String[] nombres;

        if (nivelActual == 1) {
            nombres = new String[]{
                    "cebra.png", "chango.png", "cinco.png", "elefante.png", "fresa.png", "leon.png",
                    "pera.png", "plata.png", "rino.png", "sandia.png", "tigre.png", "uva.png"
            };
        } else if (nivelActual == 2) {
            nombres = new String[]{
                    "mk.png", "gofy.png", "ariel.png", "mulan.png", "moana.png", "ana.png",
                    "dum.png", "tarzan.png", "villa.png", "hakuna.png", "campanita.png", "simba.png"
            };
        } else {
            nombres = new String[0];
        }

        Vector<ImageIcon> temp = new Vector<>();
        for (String nombre : nombres) {
            String ruta = carpeta + nombre;
            File archivo = new File(ruta);
            if (archivo.exists()) {
                ImageIcon icono = new ImageIcon(ruta);
                icono = new ImageIcon(icono.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                temp.add(icono);
            } else {
                System.out.println("No se encontró: " + archivo.getAbsolutePath());
            }
        }
        imagenes = temp.toArray(new ImageIcon[0]);

        if (imagenes.length == 0) {
            JOptionPane.showMessageDialog(this, "No se cargaron imágenes en el nivel " + nivelActual, "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void inicializarInterfaz() {
        panelJuego = new JPanel();
        panelJuego.setLayout(new GridLayout(0, columnas, 10, 10));
        panelJuego.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        scrollPane = new JScrollPane(panelJuego);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void iniciarJuego(int totalBotones) {
        Color azulClaro = new Color(173, 216, 230);
        this.total = totalBotones;
        botones = new JButton[totalBotones];
        indices = new int[totalBotones];
        encontrada = new boolean[totalBotones];
        paresEncontrados = 0;

        Vector<Integer> base = new Vector<>();
        for (int i = 0; i < totalBotones / 2; i++) {
            base.add(i);
            base.add(i);
        }
        java.util.Collections.shuffle(base);
        for (int i = 0; i < totalBotones; i++) {
            indices[i] = base.get(i);
        }

        for (int i = 0; i < totalBotones; i++) {
            JButton b = new JButton();
            final int idx = i;
            b.setPreferredSize(new Dimension(100, 100));
            b.setFocusable(false);
            b.setBackground(azulClaro); // ← Aquí se establece el color
            b.addActionListener(e -> mostrarImagen(idx));
            botones[i] = b;
            panelJuego.add(b);
        }

        panelJuego.revalidate();
        panelJuego.repaint();
        actualizarCursor();
        requestFocusInWindow();
    }

    private void mostrarImagen(int i) {
        if (encontrada[i] || botones[i] == primero) return;

        botones[i].setIcon(imagenes[indices[i]]);
        if (primero == null) {
            primero = botones[i];
        } else {
            setEnabled(false);
            Timer timer = new Timer(1000, e -> {
                verificarPares(i);
                setEnabled(true);
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void verificarPares(int segundoIndice) {
        int primeroIndice = java.util.Arrays.asList(botones).indexOf(primero);

        if (indices[primeroIndice] == indices[segundoIndice]) {
            encontrada[primeroIndice] = true;
            encontrada[segundoIndice] = true;
            paresEncontrados++;
            puntajeFinal += 10;

            if (paresEncontrados == total / 2) {
                nivelAlcanzado = nivelActual;
                int siguienteNivel = nivelActual + 1;

                File carpetaSiguiente = new File("src/main/java/imagenes/nivel" + siguienteNivel);
                if (carpetaSiguiente.exists() && carpetaSiguiente.isDirectory()) {
                    // Mostrar aviso e iniciar nivel al presionar tecla
                    JDialog dialog = new JDialog(this, "Nivel Completado", true);
                    dialog.setSize(400, 150);
                    dialog.setLocationRelativeTo(this);
                    dialog.setLayout(new BorderLayout());

                    JLabel mensaje = new JLabel("<html><center>¡Nivel " + nivelActual + " completado!<br>" +
                            "Presiona cualquier tecla para continuar al siguiente nivel.</center></html>", SwingConstants.CENTER);
                    mensaje.setFont(new Font("Arial", Font.PLAIN, 16));
                    dialog.add(mensaje, BorderLayout.CENTER);

                    dialog.addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyPressed(KeyEvent e) {
                            int tecla = e.getKeyCode();
                            if (tecla == KeyEvent.VK_Q || tecla == KeyEvent.VK_W || tecla == KeyEvent.VK_E ||
                                    tecla == KeyEvent.VK_A || tecla == KeyEvent.VK_S || tecla == KeyEvent.VK_D) {
                                dialog.dispose();
                            }
                        }
                    });

                    dialog.setFocusable(true);
                    dialog.setVisible(true);

                    // Al cerrar, cargar siguiente nivel
                    nivelActual++;
                    panelJuego.removeAll();
                    cargarImagenesDesdeCarpetaExterna();
                    iniciarJuego(imagenes.length * 2);
                } else {
                    JOptionPane.showMessageDialog(this, "¡Felicidades! Has completado todos los niveles.");
                    mostrarResumenFinal();
                }
            }
        } else {
            botones[primeroIndice].setIcon(null);
            botones[segundoIndice].setIcon(null);
        }
        primero = null;
    }


    private void actualizarCursor() {
        for (int i = 0; i < botones.length; i++) {
            botones[i].setBorder(UIManager.getBorder("Button.border"));
        }
        int index = cursorFila * columnas + cursorColumna;
        if (index >= 0 && index < botones.length) {
            botones[index].setBorder(BorderFactory.createLineBorder(Color.RED, 3));
        }
    }

    private void mostrarResumenFinal() {
        long tiempoFinal = System.currentTimeMillis();
        long tiempoJugadoMs = tiempoFinal - inicioJuego;
        int segundos = (int) (tiempoJugadoMs / 1000);
        LocalTime tiempo = LocalTime.ofSecondOfDay(segundos);

        String tiempoStr = tiempo.toString(); // formato HH:mm:ss
        String nivelStr = "Nivel " + nivelAlcanzado;

        SesionRequest sesion = new SesionRequest(
                idNino,
                id_juego,
                tiempoStr,
                nivelStr,
                puntajeFinal
        );


        Call<Void> llamada = api.insertarSesion(sesion);
        try {
            Response<Void> resp = llamada.execute();
            if (resp.isSuccessful()) {
                System.out.println("Sesión registrada exitosamente.");
            } else {
                System.out.println("Error al registrar sesión: " + resp.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Datos para progreso
        int nivelesDeProgreso = nivelAlcanzado; // Por ejemplo, sumamos 1 nivel por partida completada
        int puntosTotales = puntajeFinal;

        ProgresoRequest progreso = new ProgresoRequest(idNino, nivelesDeProgreso, puntosTotales);


        Call<Void> llamadaProgreso = api.actualizarProgreso(progreso);

        try {
            Response<Void> resp = llamadaProgreso.execute();
            if (resp.isSuccessful()) {
                System.out.println("Progreso actualizado/insertado correctamente.");
            } else {
                System.out.println("Error al actualizar progreso: " + resp.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Recursos resumen = new Recursos(
                puntajeFinal,
                nivelAlcanzado,
                LocalDate.now(),
                tiempo
        );
        System.out.println(tiempo);

        JLabel label = new JLabel(resumen.toHtml());
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        JOptionPane.showMessageDialog(this, label, "Resumen", JOptionPane.INFORMATION_MESSAGE);

        System.exit(0);
    }


    private int mostrarDialogoNivel(int nivel) {
        JDialog dialog = new JDialog(this, "Siguiente Nivel", true); // Aquí se declara correctamente
        dialog.setSize(350, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel mensaje = new JPanel(new GridLayout(2, 1));
        mensaje.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mensaje.add(new JLabel("\u00a1Nivel " + nivel + " completo!", JLabel.CENTER));
        mensaje.add(new JLabel("\u00bfDeseas continuar al siguiente nivel?", JLabel.CENTER));
        dialog.add(mensaje, BorderLayout.CENTER);

        JPanel botones = new JPanel();
        JButton btnSi = new JButton("Sí");
        JButton btnNo = new JButton("No");
        botones.add(btnSi);
        botones.add(btnNo);
        dialog.add(botones, BorderLayout.SOUTH);

        final int[] respuesta = {-1};

        btnSi.addActionListener(e -> {
            respuesta[0] = 0;
            dialog.dispose();
        });

        btnNo.addActionListener(e -> {
            respuesta[0] = 1;
            dialog.dispose();
        });

        dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0), "yes");
        dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0), "no");

        dialog.getRootPane().getActionMap().put("yes", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                respuesta[0] = 0;
                dialog.dispose();
            }
        });

        dialog.getRootPane().getActionMap().put("no", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                respuesta[0] = 1;
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
        return respuesta[0];
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Memorama m = new Memorama();
            m.setVisible(true);
        });


    }
}
