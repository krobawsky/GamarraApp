package tecsup.integrador.gamarraapp.models;

import com.orm.SugarRecord;

import java.util.List;

public class CategoriaRepository {

    public static List<Categoria> list(){
        List<Categoria> categorias = SugarRecord.listAll(Categoria.class);
        return categorias;
    }

    public static Categoria read(Long id){
        Categoria categoria = SugarRecord.findById(Categoria.class, id);
        return categoria;
    }

    public static void create(Long id, String nombre){
        Categoria categoria = new Categoria(id, nombre);
        SugarRecord.save(categoria);
    }

    public static void delete(Long id){
        Categoria categoria = SugarRecord.findById(Categoria.class, id);
        SugarRecord.delete(categoria);
    }

}
