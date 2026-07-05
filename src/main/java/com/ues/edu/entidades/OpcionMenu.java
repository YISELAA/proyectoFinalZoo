package com.ues.edu.entidades;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "opcion_menu")
@AllArgsConstructor
@NoArgsConstructor
public class OpcionMenu {

    @Id
    @Expose
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Expose
    @Column(name = "nombre", nullable = false, length = 70)
    private String nombre;

    @NotBlank
    @Expose
    @Column(name = "url", nullable = false, length = 150)
    private String url;

    @Expose
    @Column(name = "icono", length = 50)
    private String icono; 
}