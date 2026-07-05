/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.entidades;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Yiss
 */
@Setter
@Getter
@Entity
@Table(name = "habitat")
@AllArgsConstructor
@NoArgsConstructor
public class Habitat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(name = "tipo_terreno", unique = true,nullable = false, length = 70)
    private String tipoTerreno;

    @NotNull
    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;

    // Quitamos ALL y orphanRemoval para proteger a los animales
@OneToMany(mappedBy = "habitat", cascade = {CascadeType.MERGE, CascadeType.REFRESH})
private List<Animal> listaAnimales;

    @ManyToMany
    @JoinTable(
            name = "habitat_cuidador",
            joinColumns = @JoinColumn(name = "idHabitat"),
            inverseJoinColumns = @JoinColumn(name = "idEmpleado")
    )
    private List<Empleado> cuidadores;
}
