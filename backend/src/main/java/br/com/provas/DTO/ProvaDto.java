package br.com.provas.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class ProvaDto {

   

    public record AlternativaRequest(
            @NotBlank(message = "O texto da alternativa é obrigatório") String texto,
            boolean correta) {
    }

    public record QuestaoRequest(
            @NotBlank(message = "O enunciado é obrigatório") String enunciado,
            @Size(min = 2, message = "Cada questão precisa de pelo menos 2 alternativas")
            @Valid List<AlternativaRequest> alternativas) {
    }

    public record ProvaRequest(
            @NotBlank(message = "O título é obrigatório") String titulo,
            String descricao,
            @NotEmpty(message = "A prova precisa de pelo menos 1 questão")
            @Valid List<QuestaoRequest> questoes) {
    }

    

    public record AlternativaResponse(Long id, String texto) {
    }

    public record QuestaoResponse(Long id, String enunciado, List<AlternativaResponse> alternativas) {
    }

    public record ProvaDetalheResponse(Long id, String titulo, String descricao, List<QuestaoResponse> questoes) {
    }

    public record ProvaResumoResponse(Long id, String titulo, String descricao, int totalQuestoes) {
    }
}