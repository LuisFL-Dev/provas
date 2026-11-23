package br.com.provas.repository;

import br.com.provas.model.Tentativa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TentativaRepository extends JpaRepository<Tentativa, Long> {

    List<Tentativa> findByAlunoIdOrderByRealizadaEmDesc(Long alunoId);
}