/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author
 */
@Setter
@Getter
@Entity
@Table(name = "visita")
@AllArgsConstructor
@NoArgsConstructor
public class DetalleVisita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Pattern(
            regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$",
            message = "El nombre solo puede contener letras"
    )
    @Column(name = "nombre_visitante", nullable = false)
    private String nombreVisitante;
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(
            regexp = "\\d{8}",
            message = "El teléfono debe contener exactamente 8 dígitos"
    )
    @Column(name = "telefono", nullable = false, length = 8)
    private String telefono;

    @Column(name = "fecha_visita", nullable = false)
    private LocalDate fechaVisita;

    @NotNull
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @NotNull
    @Column(name = "subtotal", nullable = false)
    private double subtotal;

    @ManyToOne
    @JoinColumn(name = "idticket")
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "idempleado")
    private Empleado empleado;

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        if (this.ticket != null) {
            this.subtotal = this.cantidad * this.ticket.getPrecio();
        }
    }
    

    @PrePersist
    public void asignarFecha() {
        this.fechaVisita = LocalDate.now();
    }

}
