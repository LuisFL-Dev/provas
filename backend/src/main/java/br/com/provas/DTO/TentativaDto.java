package br.com.provas.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TentativaDto {

    public record RespostaItem(
            @NotNull(message = "questaoId é obrigatório") Long questaoId,
            @NotNull(message = "alternativaId é obrigatório") Long alternativaId) {
    }

    
    public record ResponderRequest(
            @NotNull(message = "Envie as respostas") @Valid List<RespostaItem> respostas) {
    }

    public record ResultadoResponse(
            Long tentativaId, String prova, int acertos, int total, BigDecimal nota) {
    }

    public record TentativaResumoResponse(
            Long id, Long provaId, String prova, int acertos, int total,
            BigDecimal nota, LocalDateTime realizadaEm) {
    }

    // Quantas tentativas o aluno já usou e qual é o limite
    public record TentativasInfo(long realizadas, int limite) {
    }
}