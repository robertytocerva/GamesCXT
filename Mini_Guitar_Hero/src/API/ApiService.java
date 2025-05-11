package API;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("buscarNino")
    Call<BuscarNinoResponse> buscarNinoId(@Body BuscarNinoRequest request);

    @POST("insertarSesion")
    Call<Void> insertarSesion(@Body SesionRequest sesion);

    @POST("actualizarProgreso")
    Call<Void> actualizarProgreso(@Body ProgresoRequest progreso);



}
