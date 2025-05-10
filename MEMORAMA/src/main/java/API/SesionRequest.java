package API;

public class SesionRequest {
    private int id_nino;
    private int id_juego; // Por ejemplo: 2 para Memorama
    private String tiempo_en_actividad;
    private String nivel_actual;
    private int puntos_por_juego;

    public SesionRequest(int id_nino, int id_juego, String tiempo_en_actividad, String nivel_actual, int puntos_por_juego) {
        this.id_nino = id_nino;
        this.id_juego = id_juego;
        this.tiempo_en_actividad = tiempo_en_actividad;
        this.nivel_actual = nivel_actual;
        this.puntos_por_juego = puntos_por_juego;
    }
}
