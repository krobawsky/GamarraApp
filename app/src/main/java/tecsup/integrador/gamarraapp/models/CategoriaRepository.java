package tecsup.integrador.gamarraapp.models;

import com.orm.SugarRecord;

import java.util.List;

public class CategoriaRepository {

    public static List<Categoria> listCategoriasTienda(){
        List<Categoria> categoriasTienda = SugarRecord.listAll(Categoria.class);
        return categoriasTienda;
    }

    public static void createCategoriasTienda(Long id, String nombre){
        Categoria categoriasTienda = new Categoria(id, nombre);
        SugarRecord.save(categoriasTienda);
    }


    public static List<Categoria2> listCategoriasProducto(){
        List<Categoria2> categoriasProducto = SugarRecord.listAll(Categoria2.class);
        return categoriasProducto;
    }

    public static void createCategoriasProducto(Long id, String nombre){
        Categoria2 categoriasProducto = new Categoria2(id, nombre);
        SugarRecord.save(categoriasProducto);
    }

}
