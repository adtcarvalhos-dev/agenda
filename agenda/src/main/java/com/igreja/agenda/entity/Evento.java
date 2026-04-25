package com.igreja.agenda.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "eventos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String descricao;

    private LocalDate data;

    private LocalTime hora;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Presenca> presencas;

    @ManyToOne
    @JoinColumn(name = "criado_por")
    private Usuario criadoPor;
}
