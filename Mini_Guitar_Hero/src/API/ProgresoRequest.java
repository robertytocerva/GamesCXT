package API;

public class ProgresoRequest {
    private int id_nino;
    private int niveles_de_progreso;
    private int puntos_totales;

    public ProgresoRequest(int id_nino, int niveles_de_progreso, int puntos_totales) {
        this.id_nino = id_nino;
        this.niveles_de_progreso = niveles_de_progreso;
        this.puntos_totales = puntos_totales;
    }
}
