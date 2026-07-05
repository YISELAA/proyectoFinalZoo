package com.ues.edu.entidades;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "rol")
@AllArgsConstructor
@NoArgsConstructor
public class Rol {

    @Id
    @Expose
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Expose
    @Column(name = "nombre_rol", unique = true, nullable = false, length = 50)
    private String nombreRol;

    // Relación muchos a muchos con las opciones del menú
    @Expose
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "permiso", 
        joinColumns = @JoinColumn(name = "idrol"),
        inverseJoinColumns = @JoinColumn(name = "idopcion")
    )
    private List<OpcionMenu> opcionesMenu;
}