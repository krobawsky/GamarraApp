package tecsup.integrador.gamarraapp.servicios;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import tecsup.integrador.gamarraapp.models.Categoria;
import tecsup.integrador.gamarraapp.models.Producto;
import tecsup.integrador.gamarraapp.models.Tienda;
import tecsup.integrador.gamarraapp.models.Usuario;
import tecsup.integrador.gamarraapp.models.tiendaCategoria;

public interface ApiService {

    String API_BASE_URL = "https://gamarra-rest-krobawsky.c9users.io";

    //Usuarios
    @GET("api/v1/user")
    Call<List<Usuario>> getUsers();

    @FormUrlEncoded
    @POST("/api/v1/user")
    Call<ResponseMessage> createUser(@Field("id") String id,
                                     @Field("nombre") String nombre,
                                     @Field("email") String email);


    @GET("api/v1/usuarioall")
    Call<List<Usuario>> getUsuarios();


    @GET("api/v1/usuario/{id}")
    Call<Usuario> showUsuario(@Path("id") Integer id);

    @FormUrlEncoded
    @POST("/api/v1/usuario")
    Call<ResponseMessage> createUsuarioP(@Field("nombre") String nombre,
                                        @Field("dni") String dni,
                                        @Field("email") String email,
                                        @Field("password") String password);

    @Multipart
    @POST("/api/v1/usuario")
    Call<ResponseMessage> createUsuario(@Part("nombre") RequestBody nombre,
                                        @Part("dni") RequestBody dni,
                                        @Part("email") RequestBody email,
                                        @Part("password") RequestBody password,
                                        @Part MultipartBody.Part imagen
                                        );

    //Tiendas
    @GET("api/v1/tiendas")
    Call<List<Tienda>> getTiendas();

    @GET("api/v1/tiendas/{id}")
    Call<Tienda> showTienda(@Path("id") Integer id);

    @FormUrlEncoded
    @POST("/api/v1/tiendas")
    Call<ResponseMessage> createTienda (@Field("nombre") String nombre,
                                        @Field("puesto") String puesto,
                                        @Field("telefono") String telefono,
                                        @Field("latitud") String latitud,
                                        @Field("longitud") String longitud,
                                        @Field("ubicacion") String ubicacion,
                                        @Field("comerciante_id") String comerciante_id);


    //Productos
    @GET("api/v1/productos")
    Call<List<Producto>> getProductos();

    @DELETE("/api/v1/productos/{id}")
    Call<ResponseMessage> destroyProducto(@Path("id") Integer id);

    @Multipart
    @POST("/api/v1/productos")
    Call<ResponseMessage> createProducto(
            @Part("nombre") RequestBody nombre,
            @Part("precio") RequestBody precio,
            @Part("descripcion") RequestBody descripcion,
            @Part("tienda_id") RequestBody tienda_id,
            @Part("categoria_producto_id") RequestBody categoria_producto_id,
            @Part MultipartBody.Part imagen
    );

    //Categorias
    @GET("api/v1/categorias_producto")
    Call<List<Categoria>> getCategoriaProducto();

    @GET("api/v1/categorias_tienda")
    Call<List<Categoria>> getCategoriaTienda();

    @GET("api/v1/tienda_categorias")
    Call<List<tiendaCategoria>> getTiendaHasCategoria();

    //Tienda _ categorias
    @FormUrlEncoded
    @POST("/api/v1/tienda_categorias")
    Call<ResponseMessage> createTiendaHasCategoria(@Field("tienda_id") String tienda_id,
                                                   @Field("categoria_tienda_id") String categoria_tienda_id);

    @DELETE("/api/v1/tienda_categorias/delete/{id}")
    Call<ResponseMessage> destroyTiendaHasCategoria(@Path("id") Integer id);


    //Favoritos
    @GET("api/v1/favoritos")
    Call<List<Categoria>> getFavoritos();

}
