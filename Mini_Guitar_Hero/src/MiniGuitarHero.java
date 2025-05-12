/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import API.ApiService;
import API.ProgresoRequest;
import API.RetrofitClient;
import API.SesionRequest;
import retrofit2.Call;
import retrofit2.Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class MiniGuitarHero extends JPanel implements ActionListener, KeyListener {

    private ApiService api = RetrofitClient.getApiService();


    private Timer timer;
    private ArrayList<Note> notes;
    private ArrayList<Destello> destellos;
    private int score = 0;
    private int errores = 0;
    private int nivel = 1;
    private int speed = 5;
    private Random random = new Random();
    private final int NOTE_WIDTH = 50;
    private final int NOTE_HEIGHT = 50;
    private final int COLISION_Y = 600; // Línea de colisión

    private long[] lastNoteTimes = new long[4]; // Para espaciar notas
    private boolean[] keyPressed = new boolean[4]; // Para iluminar zona al presionar

    private Image[] flechas = new Image[4];
    private Image fondo;
    private Image pista;

    private Clip sonidoCorrecto;
    private Clip sonidoError;

    private int puntajeTotal = 0;
    private int nivelSuperado = 0;
    private int tiempoJuegoSegundos = 0;
    private long tiempoInicio = System.currentTimeMillis();
    private boolean juegoTerminado = false;
    private String fechaInicio;

    private String correoUsuario;
    private String contraseñaUsuario;

    private String mensajeNivel = "";
    private int alphaNivel = 0;  // transparencia (0 a 255)
    private int tiempoNivel = 0;
    private final int DURACION_NIVEL = 50; // duración del mensaje

    private int id_juego = 1;
    private int idNino;

    private Color[] noteColors = {
            new Color(76, 255, 37), // A
            new Color(255, 0, 0),   // W
            new Color(255, 251, 0), // S
            new Color(93, 173, 226) // D

    };

    public MiniGuitarHero(String correo, String contraseña, int idNino) {
        this(); // llama al constructor original
        this.correoUsuario = correo;
        this.contraseñaUsuario = contraseña;
        this.idNino = idNino;

        // Capturar la fecha al iniciar
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        this.fechaInicio = LocalDateTime.now().format(formato);
    }

    public MiniGuitarHero() {
        notes = new ArrayList<>();
        destellos = new ArrayList<>();
        timer = new Timer(30, this);
        timer.start();
        setFocusable(true);
        addKeyListener(this);

        // Cargar imágenes desde carpeta img (debe estar en recursos del proyecto)
        flechas[0] = new ImageIcon(getClass().getResource("/img/Izquierda.png")).getImage();
        flechas[1] = new ImageIcon(getClass().getResource("/img/Arriba.png")).getImage();
        flechas[2] = new ImageIcon(getClass().getResource("/img/Abajo.png")).getImage();
        flechas[3] = new ImageIcon(getClass().getResource("/img/Derecha.png")).getImage();
        fondo = new ImageIcon(getClass().getResource("/img/Fondo_Pista2.png")).getImage();
        pista = new ImageIcon(getClass().getResource("/img/Fondo_Pista.jpeg")).getImage();

        cargarSonidos();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.BLACK);
        Graphics2D g2d = (Graphics2D) g;

        // Dibujar fondo general en toda la pantalla
        g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        // g.drawImage(pista, 0, 0, 400, getHeight(), this); // fondo en pista

        int pistaX = 200; // posición horizontal de inicio de la pista (centrada más o menos)
        int pistaAncho = 400;

        // Fondo de pista
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(pistaX, 0, pistaAncho, getHeight());

        // Líneas divisorias de pista
        g.setColor(Color.WHITE);
        for (int i = 0; i <= 4; i++) {
            g.drawLine(pistaX + i * 100, 0, pistaX + i * 100, getHeight());
        }

        // Línea de colisión
        g.drawLine(pistaX, COLISION_Y + NOTE_HEIGHT / 2, pistaX + pistaAncho, COLISION_Y + NOTE_HEIGHT / 2);

        // Flechas en zona de colisión
        for (int i = 0; i < 4; i++) {
            int x = laneToCenterX(i) - (flechas[i].getWidth(null) / 2);
            int y = COLISION_Y + 20;
            g.drawImage(flechas[i], x, y, 55, 55, null);
        }

        // Iluminación sobre flechas al presionar teclas
        for (int i = 0; i < 4; i++) {
            if (keyPressed[i]) {
                int x = laneToCenterX(i) - 27;
                int y = COLISION_Y + 10;
                Composite original = g2d.getComposite();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g2d.setColor(noteColors[i]);
                g2d.fillRect(x, y, 50, 25);
                g2d.setComposite(original);
            }
        }

        // Dibujar notas
        for (Note note : notes) {
            g.setColor(Color.GRAY);
            g.fillOval(note.x - 2, note.y - 2, NOTE_WIDTH + 4, NOTE_HEIGHT + 4);
            g.setColor(note.color);
            g.fillOval(note.x, note.y, NOTE_WIDTH, NOTE_HEIGHT);
        }

        // Dibujar destellos
        for (Destello d : new ArrayList<>(destellos)) {
            d.dibujar(g2d);
            d.actualizar();
            if (!d.activo()) destellos.remove(d);
        }

        // Barra lateral (a la derecha)
        g.setColor(new Color(20, 20, 60));
        // g.fillRect(pistaX + pistaAncho, 0, 200, getHeight());

        g.setColor(new Color(172, 255, 5));
        g.drawRect(pistaX + pistaAncho, -350, 120, getHeight());

        g.setFont(new Font("Orbitron", Font.BOLD, 18));
        g.drawString("SCORE", pistaX + pistaAncho + 10, 50);
        g.drawString(String.valueOf(score), pistaX + pistaAncho + 30, 70);

        g.drawString("Errores:", pistaX + pistaAncho + 10, 120);
        g.drawString(String.valueOf(errores), pistaX + pistaAncho + 30, 140);

        g.drawString("Nivel:", pistaX + pistaAncho + 10, 190);
        g.drawString(String.valueOf(nivel), pistaX + pistaAncho + 30, 210);

        // Mostrar tiempo
        g.setFont(new Font("Orbitron", Font.BOLD, 18));
        g.setColor(new Color(172, 255, 5));
        g.drawString("Tiempo:", pistaX + pistaAncho + 10, 260);
        g.drawString(mostrarTiempo(), pistaX + pistaAncho + 30, 280);

        // Mostrar pantalla de fin si terminó el juego
        if (juegoTerminado) {
            Composite original = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            g2d.setColor(Color.GRAY);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setComposite(original);

            g2d.setFont(new Font("Arial", Font.BOLD, 40));
            g2d.setColor(Color.WHITE);
            g2d.drawString("¡GAME OVER!", getWidth() / 2 - 130, getHeight() / 2 - 60);

            g2d.setFont(new Font("Arial", Font.PLAIN, 24));
            g2d.drawString("Puntaje Total: " + puntajeTotal, getWidth() / 2 - 100, getHeight() / 2);
            g2d.drawString("Nivel Alcanzado: " + nivelSuperado, getWidth() / 2 - 100, getHeight() / 2 + 30);
            g2d.drawString("Tiempo Jugado: " + mostrarTiempo(), getWidth() / 2 - 100, getHeight() / 2 + 60);
        }

        if (!mensajeNivel.isEmpty() && alphaNivel > 0) {
            g2d.setFont(new Font("Agency FB", Font.BOLD, 48));
            int textWidth = g.getFontMetrics().stringWidth(mensajeNivel);
            int x = (getWidth() - textWidth) / 2;
            int y = getHeight() / 2;

            g2d.setColor(new Color(0, 0, 0, alphaNivel));
            g2d.drawString(mensajeNivel, x + 3, y + 3);
            g2d.setColor(new Color(255, 255, 255, alphaNivel));
            g2d.drawString(mensajeNivel, x, y);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Iterator<Note> it = notes.iterator();
        while (it.hasNext()) {
            Note note = it.next();
            note.y += speed;

            if (note.y > getHeight()) {
                it.remove();
                errores++;
                verificarErrores();
            }
        }

        if (random.nextInt(20) == 0) {
            int lane = random.nextInt(4);
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastNoteTimes[lane] > 300) {
                notes.add(new Note(laneToX(lane), 0, laneToColor(lane)));
                lastNoteTimes[lane] = currentTime;
            }
        }

        repaint();

        if (alphaNivel > 0) {
            tiempoNivel++;
            if (tiempoNivel > DURACION_NIVEL) {
                alphaNivel -= 5; // se desvanece poco a poco
                if (alphaNivel < 0) alphaNivel = 0;
            }
        }
        long tiempoActual = System.currentTimeMillis();
        tiempoJuegoSegundos = (int) ((tiempoActual - tiempoInicio) / 1000);
    }

    private int laneToX(int lane) {
        return laneToCenterX(lane) - (NOTE_WIDTH / 2);
    }

    private int laneToCenterX(int lane) {
        return 200 + 50 + lane * 100; // centrado dentro de la pista que inicia en x=200
    }

    private Color laneToColor(int lane) {
        return (lane >= 0 && lane < noteColors.length) ? noteColors[lane] : Color.WHITE;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        char key = Character.toUpperCase(e.getKeyChar());
        int lanePressed = -1;

        if (key == 'A') lanePressed = 0;
        if (key == 'W') lanePressed = 1;
        if (key == 'S') lanePressed = 2;
        if (key == 'D') lanePressed = 3;

        if (lanePressed != -1) {
            keyPressed[lanePressed] = true;
            boolean hit = checkNoteHit(lanePressed);
            if (hit) {
                if (sonidoCorrecto != null) {
                    sonidoCorrecto.setFramePosition(0);
                    sonidoCorrecto.start();
                }
            } else {
                errores++;
                verificarErrores();
                if (sonidoError != null) {
                    sonidoError.setFramePosition(0);
                    sonidoError.start();
                }
            }
            final int finalLane = lanePressed;
            new Timer(100, evt -> {
                keyPressed[finalLane] = false;
                ((Timer) evt.getSource()).stop();
                repaint();
            }).start();
        }
    }

    private boolean checkNoteHit(int lane) {
        Iterator<Note> it = notes.iterator();
        while (it.hasNext()) {
            Note note = it.next();
            if (note.x == laneToX(lane) && Math.abs(note.y - COLISION_Y) < 30) {
                it.remove();
                score += 20;//puntaej de cada nota
                destellos.add(new Destello(laneToCenterX(lane), COLISION_Y + 30, note.color));
                if (score % 1000 == 0) {
                    speed += 2;
                    nivel++;

                    // Activar mensaje visual
                    mensajeNivel = "¡Nivel " + nivel + "!";
                    alphaNivel = 255;
                    tiempoNivel = 0;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    class Note {
        int x, y;
        Color color;

        public Note(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }

    class Destello {
        int x, y, tamaño = 30;
        int duracion = 20;
        int tiempo = 0;
        Color color;

        public Destello(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }

        public void dibujar(Graphics2D g) {
            Composite original = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f - (tiempo / (float) duracion)));
            g.setColor(color);
            g.fillOval(x - tamaño / 2, y - tamaño / 2, tamaño, tamaño);
            g.setComposite(original);
        }

        public void actualizar() {
            tiempo++;
        }

        public boolean activo() {
            return tiempo < duracion;
        }
    }

    private void verificarErrores() {
        int erroresPermitidos = 15 + (nivel - 1) * 5;
        if (errores >= erroresPermitidos) {
            puntajeTotal = score;
            nivelSuperado = nivel;
            juegoTerminado = true;
            timer.stop();
            repaint();

            mostrarResumenEnConsola(); // <--- Aquí se llama la función

            // Retraso opcional para permitir que el usuario vea la pantalla antes de cerrar
            new Timer(5000, evt -> System.exit(0)).start(); // Cierra después de 5 segundos
        }

    }

    private void cargarSonidos() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/sonidos/correcto.wav"));
            sonidoCorrecto = AudioSystem.getClip();
            sonidoCorrecto.open(audioInputStream);

            audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/sonidos/error.wav"));
            sonidoError = AudioSystem.getClip();
            sonidoError.open(audioInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String mostrarTiempo() {
        int minutos = tiempoJuegoSegundos / 60;
        int segundos = tiempoJuegoSegundos % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }

    private void mostrarResumenEnConsola() {
//        System.out.println("===== RESUMEN DE PARTIDA =====");
//        System.out.println("Correo ingresado: " + correoUsuario);
//        System.out.println("Contraseña ingresada: " + contraseñaUsuario);
//        System.out.println("Fecha de inicio: " + fechaInicio);
//        System.out.println("Puntaje total: " + puntajeTotal);
//        System.out.println("Nivel alcanzado: " + nivelSuperado);
//        System.out.println("Tiempo jugado: " + mostrarTiempo());
//        System.out.println("================================");

        String tiempoStr = mostrarTiempo(); // formato HH:mm:ss
        String nivelStr = "Nivel " + nivelSuperado;
        int nivelesDeProgreso = nivelSuperado; // Por ejemplo, sumamos 1 nivel por partida completada
        int puntosTotales = puntajeTotal;

        //datos para registrar la sesión
        SesionRequest sesion = new SesionRequest(
                idNino,
                id_juego,
                tiempoStr,
                nivelStr,
                puntajeTotal
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

        // Datos para progreso (actualiza el registro de progreso
        // si el usuario a un no tiene uno lo crea


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
    }

//    public static void main(String[] args) {
//        JFrame frame = new JFrame("Mini Guitar Hero");
//        MiniGuitarHero game = new MiniGuitarHero();
//        frame.add(game);
//        frame.setSize(900, 700);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setResizable(false);
//        frame.setVisible(true);
//    }
}