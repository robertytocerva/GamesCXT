
import java.awt.Color;
import java.awt.Graphics2D;



public class Destello {
    int x, y, vida;
    public Destello(int x, int y) {
        this.x = x;
        this.y = y;
        this.vida = 15; // frames de duración
    }

    public void actualizar() {
        vida--;
    }

    public boolean activo() {
        return vida > 0;
    }

    public void dibujar(Graphics2D g) {
        int alpha = (int) (255 * (vida / 15.0));
        g.setColor(new Color(255, 255, 0, alpha)); // Amarillo con transparencia
        g.fillOval(x - 10, y - 10, 40, 40);
    }
}
