package co.uniajc.agrovalle.agrovalleconnect.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transacciones_precio", indexes = {
        @Index(name = "idx_transaccion_producto_fecha", columnList = "producto, registrada_en")
})
public class TransaccionPrecio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String producto;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Municipio municipio;

    @Column(name = "precio_unitario_cop", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitarioCop;

    @Column(name = "registrada_en", nullable = false)
    private Instant registradaEn;

    @ManyToOne(optional = false)
    @JoinColumn(name = "agricultor_id", nullable = false)
    private Agricultor registradaPor;

    @PrePersist
    void asignarFechaRegistro() {
        if (registradaEn == null) {
            registradaEn = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    public BigDecimal getPrecioUnitarioCop() {
        return precioUnitarioCop;
    }

    public void setPrecioUnitarioCop(BigDecimal precioUnitarioCop) {
        this.precioUnitarioCop = precioUnitarioCop;
    }

    public Instant getRegistradaEn() {
        return registradaEn;
    }

    public void setRegistradaEn(Instant registradaEn) {
        this.registradaEn = registradaEn;
    }

    public Agricultor getRegistradaPor() {
        return registradaPor;
    }

    public void setRegistradaPor(Agricultor registradaPor) {
        this.registradaPor = registradaPor;
    }
}
