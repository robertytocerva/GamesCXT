package JUEGO3;

import java.time.LocalDate;
import java.time.LocalTime;

public class Recursos {
    private String correoTutor;
    private String contrasenaTutor;
    private int puntajeFinal;
    private int nivelAlcanzado;
    private LocalDate fecha;
    private LocalTime tiempoJugado;

    public Recursos(String correoTutor, String contrasenaTutor, int puntajeFinal, int nivelAlcanzado, LocalDate fecha, LocalTime tiempoJugado) {
        this.correoTutor = correoTutor;
        this.contrasenaTutor = contrasenaTutor;
        this.puntajeFinal = puntajeFinal;
        this.nivelAlcanzado = nivelAlcanzado;
        this.fecha = fecha;
        this.tiempoJugado = tiempoJugado;
    }

    // Getters
    public String getCorreoTutor() {
        return correoTutor;
    }

    public String getContrasenaTutor() {
        return contrasenaTutor;
    }

    public int getPuntajeFinal() {
        return puntajeFinal;
    }

    public int getNivelAlcanzado() {
        return nivelAlcanzado;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getTiempoJugado() {
        return tiempoJugado;
    }

    public String toHtml() {
        return String.format(
            "<html><h2>=== RESUMEN DEL JUEGO ===</h2>" +
            "<p><b>Correo:</b> %s<br>" +
            "<b>Contraseña:</b> %s<br>" +
            "<b>Puntaje:</b> %d<br>" +
            "<b>Nivel alcanzado:</b> %d<br>" +
            "<b>Fecha:</b> %s<br>" +
            "<b>Tiempo Jugado:</b> %s</p></html>",
            correoTutor,
            contrasenaTutor,
            puntajeFinal,
            nivelAlcanzado,
            fecha.toString(),
            tiempoJugado.toString()
        );
    }
}
