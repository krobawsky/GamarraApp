package tecsup.integrador.gamarraapp.servicios;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import tecsup.integrador.gamarraapp.models.Categoria;
import tecsup.integrador.gamarraapp.models.Tienda;
import tecsup.integrador.gamarraapp.models.tiendaCategoria;

public interface ApiService {

    String API_BASE_URL = "https://gamarra-rest-krobawsky.c9users.io";

    @GET("api/v1/tiendas")
    Call<List<Tienda>> getTiendas();

    @GET("api/v1/tiendas/{id}")
    Call<Tienda> showProducto(@Path("id") Integer id);


    @GET("api/v1/categorias_tienda")
    Call<List<Categoria>> getCategoriaTienda();


    @GET("api/v1/tienda_categorias")
    Call<List<tiendaCategoria>> getTiendaHasCategoria();

}
