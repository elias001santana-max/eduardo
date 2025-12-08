package comoproyect;

/**
 * Clase modelo para Categoría
 */
public class Categoria {
    private int id;
    private String nombre;
    private String descripcion;
    private String producto;
    
    // Constructor vacío
    public Categoria() {
    }
    
    // Constructor con todos los campos
    public Categoria(int id, String nombre, String descripcion, String producto) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.producto = producto;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getProducto() {
        return producto;
    }
    
    public void setProducto(String producto) {
        this.producto = producto;
    }
    
    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", producto='" + producto + '\'' +
                '}';
    }
}
