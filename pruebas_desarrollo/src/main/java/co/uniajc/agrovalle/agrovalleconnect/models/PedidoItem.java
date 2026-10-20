package co.uniajc.agrovalle.agrovalleconnect.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pedido_items")
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lote_id", nullable = false)
    private LoteCosecha lote;

    @Column(nullable = false)
    private Double cantidadKg;

    @Column(nullable = false)
    private Double precioUnitario;

    public Long getId() {
        return id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public LoteCosecha getLote() {
        return lote;
    }

    public void setLote(LoteCosecha lote) {
        this.lote = lote;
    }

    public Double getCantidadKg() {
        return cantidadKg;
    }

    public void setCantidadKg(Double cantidadKg) {
        this.cantidadKg = cantidadKg;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
