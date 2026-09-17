package co.uniajc.agrovalle.agrovalleconnect.models;

import jakarta.persistence.*;

import jakarta.validation.constraints.FutureOrPresent;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Entity

@Table(name = "lotes_cosecha")

public class LoteCosecha {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @NotBlank(message = "El tipo de producto es obligatorio")

    @Column(nullable = false)

    private String tipoProducto;

    @NotNull(message = "La cantidad es obligatoria")

    @Positive(message = "La cantidad debe ser mayor a cero")

    @Column(nullable = false)

    private Double cantidadKg;

    @NotNull(message = "El precio unitario es obligatorio")

    @Positive(message = "El precio debe ser mayor a cero")

    @Column(nullable = false)

    private Double precioUnitario;

    @NotNull(message = "La fecha de cosecha es obligatoria")

    @FutureOrPresent(message = "La fecha de cosecha no puede ser anterior al dia actual")

    @Column(nullable = false)

    private LocalDate fechaCosecha;

    @ManyToOne

    @JoinColumn(name = "agricultor_id", nullable = false)

    private Agricultor agricultor;

    private Boolean activo = true;

    // Constructor vacio (requerido por JPA)

    public LoteCosecha() {

    }

    // Getters y Setters

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
 