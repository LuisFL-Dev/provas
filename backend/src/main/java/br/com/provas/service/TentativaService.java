package br.com.provas.service;

import br.com.provas.DTO.TentativaDto.*;
import br.com.provas.model.*;
import br.com.provas.repository.TentativaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TentativaService {

    private final TentativaRepository tentativaRepository;
    private final ProvaService provaService;

    public TentativaService(TentativaRepository tentativaRepository, ProvaService provaService) {
        this.tentativaRepository = tentativaRepository;
        this.provaService = provaService;
    }

    // Corrige a prova do aluno e salva a tentativa
    @Transactional
    public ResultadoResponse responder(Long provaId, ResponderRequest request, Usuario aluno) {
        Prova prova = provaService.buscarEntidade(provaId);

        // Monta um "dicionário": id da questão -> id da alternativa escolhida
        Map<Long, Long> escolhas = new HashMap<>();
        for (RespostaItem item : request.respostas()) {
            escolhas.put(item.questaoId(), item.alternativaId());
        }

        Tentativa tentativa = new Tentativa();
        tentativa.setAluno(aluno);
        tentativa.setProva(prova);

        int acertos = 0;
        for (Questao questao : prova.getQuestoes()) {

            // 1. O aluno respondeu esta questão?
            Long alternativaId = escolhas.get(questao.getId());
            if (alternativaId == null) {
                throw new RegraNegocioException("Responda todas as questões antes de enviar");
            }

            // 2. Procura a alternativa escolhida dentro desta questão
            Alternativa escolhida = null;
            for (Alternativa alternativa : questao.getAlternativas()) {
                if (alternativa.getId().equals(alternativaId)) {
                    escolhida = alternativa;
                }
            }
            if (escolhida == null) {
                throw new RegraNegocioException("Alternativa inválida na questão " + questao.getOrdem());
            }

            // 3. Guarda a resposta e conta o acerto
            Resposta resposta = new Resposta();
            resposta.setQuestao(questao);
            resposta.setAlternativa(escolhida);
            resposta.setCorreta(escolhida.isCorreta());
            tentativa.adicionarResposta(resposta);

            if (escolhida.isCorreta()) {
                acertos++;
            }
        }

        // Nota de 0 a 10 com 2 casas decimais: acertos x 10 / total
        int total = prova.getQuestoes().size();
        BigDecimal nota = BigDecimal.valueOf(acertos)
                .multiply(BigDecimal.TEN)
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);

        tentativa.setAcertos(acertos);
        tentativa.setTotal(total);
        tentativa.setNota(nota);
        tentativaRepository.save(tentativa);

        return new ResultadoResponse(tentativa.getId(), prova.getTitulo(), acertos, total, nota);
    }

    // Histórico de notas do aluno logado
    @Transactional(readOnly = true)
    public List<TentativaResumoResponse> minhas(Usuario aluno) {
        List<TentativaResumoResponse> resultado = new ArrayList<>();

        for (Tentativa t : tentativaRepository.findByAlunoIdOrderByRealizadaEmDesc(aluno.getId())) {
            resultado.add(new TentativaResumoResponse(
                    t.getId(),
                    t.getProva().getId(),
                    t.getProva().getTitulo(),
                    t.getAcertos(),
                    t.getTotal(),
                    t.getNota(),
                    t.getRealizadaEm()));
        }
        return resultado;
    }
}