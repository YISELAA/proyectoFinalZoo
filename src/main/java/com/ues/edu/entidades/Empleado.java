package com.ues.edu.entidades;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "empleado")
@AllArgsConstructor
@NoArgsConstructor
public class Empleado {

    @Id
    @Expose
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Expose
    @Column(name = "nombre_empleado", nullable = false, length = 70)
    private String nombre;

    @NotBlank
    @Expose
    @Column(name = "apellido", nullable = false, length = 70)
    private String apellido;

    @NotBlank
    @Pattern(
            regexp = "^\\d{8}-\\d{1}$",
            message = "EL DUI DEBE TENER EL FORMATO ########-#"
    )
    @Column(name = "numero_dui", nullable = false, unique = true, length = 10)
    private String dui;

   @OneToOne(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
private transient Usuario usuario; // 🌟 Evita el bucle de regreso a Usuario

    @ManyToMany(mappedBy = "cuidadores")
    private List<Habitat> habitatsAsignados;

    @OneToMany(mappedBy = "veterinario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialMedico> historiales;
}
