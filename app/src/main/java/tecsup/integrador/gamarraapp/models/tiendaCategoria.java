package tecsup.integrador.gamarraapp.models;

public class tiendaCategoria {

    private Integer id;
    private String tienda_id;
    private String categoria_tienda_id;

    public tiendaCategoria(Integer id, String tienda_id, String categoria_tienda_id) {
        this.id = id;
        this.tienda_id = tienda_id;
        this.categoria_tienda_id = categoria_tienda_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTienda_id() {
        return tienda_id;
    }

    public void setTienda_id(String tienda_id) {
        this.tienda_id = tienda_id;
    }

    public String getCategoria_tienda_id() {
        return categoria_tienda_id;
    }

    public void setCategoria_tienda_id(String categoria_tienda_id) {
        this.categoria_tienda_id = categoria_tienda_id;
    }

    @Override
    public String toString() {
        return "tiendaCategoria{" +
                "id=" + id +
                ", tienda_id='" + tienda_id + '\'' +
                ", categoria_tienda_id='" + categoria_tienda_id + '\'' +
                '}';
    }
}
