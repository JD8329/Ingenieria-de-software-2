package co.uniajc.agrovalle.agrovalleconnect.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "carrito_items", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"comerciante_id", "lote_id"})
})
public class CarritoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "comerciante_id", nullable = false)
    private Comerciante comerciante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lote_id", nullable = false)
    private LoteCosecha lote;

    @Column(nullable = false)
    private Double cantidadKg;

    public Long getId() {
        return id;
    }

    public Comerciante getComerciante() {
        return comerciante;
    }

    public void setComerciante(Comerciante comerciante) {
        this.comerciante = comerciante;
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
}
