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

    public Recursos( int puntajeFinal, int nivelAlcanzado, LocalDate fecha, LocalTime tiempoJugado) {
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
            "<b>Puntaje:</b> %d<br>" +
            "<b>Nivel alcanzado:</b> %d<br>" +
            "<b>Fecha:</b> %s<br>" +
            "<b>Tiempo Jugado:</b> %s</p></html>",
            puntajeFinal,
            nivelAlcanzado,
            fecha.toString(),
            tiempoJugado.toString()
        );
    }
}
