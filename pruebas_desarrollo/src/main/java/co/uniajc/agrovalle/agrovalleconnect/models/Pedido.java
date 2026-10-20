package co.uniajc.agrovalle.agrovalleconnect.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "comerciante_id", nullable = false)
    private Comerciante comerciante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "agricultor_id", nullable = false)
    private Agricultor agricultor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false)
    private Instant fechaHora;

    private String transportista;

    private String ruta;

    private LocalDate fechaEstimadaRecepcion;

    private Integer calificacion;

    @Column(length = 2000)
    private String comentariosCalificacion;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoItem> items = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public Comerciante getComerciante() {
        return comerciante;
    }

    public void setComerciante(Comerciante comerciante) {
        this.comerciante = comerciante;
    }

    public Agricultor getAgricultor() {
        return agricultor;
    }

    public void setAgricultor(Agricultor agricultor) {
        this.agricultor = agricultor;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Instant getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Instant fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getTransportista() {
        return transportista;
    }

    public void setTransportista(String transportista) {
        this.transportista = transportista;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public LocalDate getFechaEstimadaRecepcion() {
        return fechaEstimadaRecepcion;
    }

    public void setFechaEstimadaRecepcion(LocalDate fechaEstimadaRecepcion) {
        this.fechaEstimadaRecepcion = fechaEstimadaRecepcion;
    }

    public Integer getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Integer calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentariosCalificacion() {
        return comentariosCalificacion;
    }

    public void setComentariosCalificacion(String comentariosCalificacion) {
        this.comentariosCalificacion = comentariosCalificacion;
    }

    public List<PedidoItem> getItems() {
        return items;
    }

    public void agregarItem(PedidoItem item) {
        items.add(item);
        item.setPedido(this);
    }
}
