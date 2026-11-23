package br.com.provas.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tentativas")
@Getter
@Setter
@NoArgsConstructor
public class Tentativa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id")
    private Usuario aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prova_id")
    private Prova prova;

    private Integer acertos;

    private Integer total;

    @Column(precision = 4, scale = 2)
    private BigDecimal nota;

    @Column(name = "realizada_em")
    private LocalDateTime realizadaEm = LocalDateTime.now();

    @OneToMany(mappedBy = "tentativa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resposta> respostas = new ArrayList<>();

    public void adicionarResposta(Resposta resposta) {
        resposta.setTentativa(this);
        respostas.add(resposta);
    }
}