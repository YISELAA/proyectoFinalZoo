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
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author yiss
 */
@Setter
@Getter
@Entity
@Table(name = "animal")
@AllArgsConstructor
@NoArgsConstructor
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotBlank
    @Column(name = "especie", nullable = false, length = 50)
    private String especie;

    @NotBlank
    @Column(name = "nombre_animal", nullable = false, length = 70)
    private String nombre;

    @NotBlank
    @Column(name = "sexo", nullable = false, length = 15)
    private String sexo;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_nacimiento")
    private Date fechaNacimiento;

    @Temporal(TemporalType.DATE)
    private Date fechaIngreso;

    @ManyToOne
    @JoinColumn(name = "idhabitat")
    private Habitat habitat;

    @Transient
    public String getEdad() {

        if (fechaNacimiento == null) {
            return "";
        }

        LocalDate nacimiento = new java.sql.Date(
                fechaNacimiento.getTime()
        ).toLocalDate();

        Period edad = Period.between(nacimiento, LocalDate.now());

        if (edad.getYears() > 0) {
            return edad.getYears() == 1
                    ? "1 año"
                    : edad.getYears() + " años";
        }

        if (edad.getMonths() > 0) {
            return edad.getMonths() == 1
                    ? "1 mes"
                    : edad.getMonths() + " meses";
        }

        return edad.getDays() == 1
                ? "1 día"
                : edad.getDays() + " días";
    }
}