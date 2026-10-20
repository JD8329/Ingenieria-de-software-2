package co.uniajc.agrovalle.agrovalleconnect.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "transacciones")
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lote_id", nullable = false)
    private LoteCosecha lote;

    @Column(nullable = false)
    private Double cantidadKg;

    @Column(nullable = false)
    private Double precioUnitario;

    @Column(nullable = false)
    private Instant fechaHora;

    public Long getId() {
        return id;
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

    public Instant getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Instant fechaHora) {
        this.fechaHora = fechaHora;
    }
}
