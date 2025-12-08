package comoproyect;

import java.util.Date;

/**
 * Clase modelo para Venta
 */
public class Venta {
    private int id;
    private Date fecha;
    private double total;
    private int idUsuario;
    private Integer idCliente; // Puede ser null
    private String estado;
    
    // Constructor vacío
    public Venta() {
    }
    
    // Constructor con todos los campos
    public Venta(int id, Date fecha, double total, int idUsuario, Integer idCliente, String estado) {
        this.id = id;
        this.fecha = fecha;
        this.total = total;
        this.idUsuario = idUsuario;
        this.idCliente = idCliente;
        this.estado = estado;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Date getFecha() {
        return fecha;
    }
    
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    
    public double getTotal() {
        return total;
    }
    
    public void setTotal(double total) {
        this.total = total;
    }
    
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public Integer getIdCliente() {
        return idCliente;
    }
    
    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    @Override
    public String toString() {
        return "Venta{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", total=" + total +
                ", idUsuario=" + idUsuario +
                ", idCliente=" + idCliente +
                ", estado='" + estado + '\'' +
                '}';
    }
}
