package co.uniajc.agrovalle.agrovalleconnect.models;

import jakarta.persistence.*;

import jakarta.validation.constraints.FutureOrPresent;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "lotes_cosecha")
public class LoteCosecha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipoProducto;

    @Size(max = 50, message = "La categoría no puede superar 50 caracteres")
    @Pattern(regexp = ".*\\S.*", message = "La categoría no puede estar vacía")
    @Column(length = 50)
    private String categoria;

    @NotNull(message = "La cantidad es obligatoria")

    @Positive(message = "La cantidad debe ser mayor a cero")

    @Column(nullable = false)
    private CategoriaProducto categoria;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal cantidadKg;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false)
    private LocalDate fechaCosecha;

    @ManyToOne
    @JoinColumn(name = "agricultor_id", nullable = false)
    private Agricultor agricultor;

    @Column(nullable = false)
    private Boolean activo = true;

    public LoteCosecha() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public CategoriaProducto getCategoria() {
        return categoria;
    }

    public String getCategoria() {

        return categoria;

    }

    public void setCategoria(String categoria) {

        this.categoria = categoria;

    }

    public Double getCantidadKg() {

    public BigDecimal getCantidadKg() {
        return cantidadKg;
    }

    public void setCantidadKg(BigDecimal cantidadKg) {
        this.cantidadKg = cantidadKg;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public LocalDate getFechaCosecha() {
        return fechaCosecha;
    }

    public void setFechaCosecha(LocalDate fechaCosecha) {
        this.fechaCosecha = fechaCosecha;
    }

    public Agricultor getAgricultor() {
        return agricultor;
    }

    public void setAgricultor(Agricultor agricultor) {
        this.agricultor = agricultor;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
