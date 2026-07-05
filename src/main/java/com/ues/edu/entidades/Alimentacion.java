package com.ues.edu.entidades;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "alimentacion")
@AllArgsConstructor
@NoArgsConstructor
public class Alimentacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(name = "tipo_alimento", nullable = false, length = 70)
    private String tipoAlimento;

    @NotBlank
    @Column(name = "horario", nullable = false, length = 70)
    private String horario;

    @NotNull
    @Column(name = "cantidad", nullable = false)
    private double cantidad;

    @ManyToOne
    @JoinColumn(name = "idanimal")
    private Animal animal;
    
     @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "idCuidador")
    private Empleado cuidador;


}
