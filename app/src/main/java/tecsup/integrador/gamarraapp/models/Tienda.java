package tecsup.integrador.gamarraapp.models;

public class Tienda {

    private Integer id;
    private String nombre;
    private String telefono;
    private String puesto;
    private String latitud;
    private String longitud;
    private String comerciante_id;

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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getComerciante_id() {
        return comerciante_id;
    }

    public void setComerciante_id(String comerciante_id) {
        this.comerciante_id = comerciante_id;
    }

    @Override
    public String toString() {
        return "Tienda{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", telefono='" + telefono + '\'' +
                ", puesto='" + puesto + '\'' +
                ", latitud='" + latitud + '\'' +
                ", longitud='" + longitud + '\'' +
                ", comerciante_id='" + comerciante_id + '\'' +
                '}';
    }
}
