package API;

public class LoginRequest {
    private String correo_electronico;
    private String contrasena;

    public LoginRequest(String correo_electronico, String contrasena) {
        this.correo_electronico = correo_electronico;
        this.contrasena = contrasena;
    }
}
