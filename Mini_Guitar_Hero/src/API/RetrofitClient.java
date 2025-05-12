package API;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static  final  String BASE_URL = "   https://abdb-2806-103e-20-2f95-b91c-ad6e-dc99-ea19.ngrok-free.app";
    private static Retrofit retrofit = null;

    private RetrofitClient() {}


    public  static  Retrofit getClient() {
        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}
