package tecsup.integrador.gamarraapp.models;

public class Producto {

    private Integer id;
    private String nombre;
    private String precio;
    private String descripcion;
    private String imagen;
    private String estado;
    private String tienda_id;
    private String categoria_producto_id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTienda_id() {
        return tienda_id;
    }

    public void setTienda_id(String tienda_id) {
        this.tienda_id = tienda_id;
    }

    public String getCategoria_producto_id() {
        return categoria_producto_id;
    }

    public void setCategoria_producto_id(String categoria_producto_id) {
        this.categoria_producto_id = categoria_producto_id;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precio='" + precio + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", imagen='" + imagen + '\'' +
                ", estado='" + estado + '\'' +
                ", tienda_id='" + tienda_id + '\'' +
                ", categoria_producto_id='" + categoria_producto_id + '\'' +
                '}';
    }
}
