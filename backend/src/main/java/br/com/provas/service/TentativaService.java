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

  
    public static final int LIMITE_TENTATIVAS = 2;

    private final TentativaRepository tentativaRepository;
    private final ProvaService provaService;

    public TentativaService(TentativaRepository tentativaRepository, ProvaService provaService) {
        this.tentativaRepository = tentativaRepository;
        this.provaService = provaService;
    }

    
    @Transactional(readOnly = true)
    public TentativasInfo minhasTentativasNaProva(Long provaId, Usuario aluno) {
        provaService.buscarEntidade(provaId); // dá 404 se a prova não existir
        long realizadas = tentativaRepository.countByAlunoIdAndProvaId(aluno.getId(), provaId);
        return new TentativasInfo(realizadas, LIMITE_TENTATIVAS);
    }

   
    @Transactional
    public ResultadoResponse responder(Long provaId, ResponderRequest request, Usuario aluno) {
        Prova prova = provaService.buscarEntidade(provaId);

        
        long realizadas = tentativaRepository.countByAlunoIdAndProvaId(aluno.getId(), provaId);
        if (realizadas >= LIMITE_TENTATIVAS) {
            throw new RegraNegocioException(
                    "Você já usou as " + LIMITE_TENTATIVAS + " tentativas desta prova");
        }

        
        Map<Long, Long> escolhas = new HashMap<>();
        for (RespostaItem item : request.respostas()) {
            escolhas.put(item.questaoId(), item.alternativaId());
        }

        Tentativa tentativa = new Tentativa();
        tentativa.setAluno(aluno);
        tentativa.setProva(prova);

        int acertos = 0;
        for (Questao questao : prova.getQuestoes()) {

            
            Long alternativaId = escolhas.get(questao.getId());
            if (alternativaId == null) {
                continue;
            }

           
            Alternativa escolhida = null;
            for (Alternativa alternativa : questao.getAlternativas()) {
                if (alternativa.getId().equals(alternativaId)) {
                    escolhida = alternativa;
                }
            }
            if (escolhida == null) {
                throw new RegraNegocioException("Alternativa inválida na questão " + questao.getOrdem());
            }

            
            Resposta resposta = new Resposta();
            resposta.setQuestao(questao);
            resposta.setAlternativa(escolhida);
            resposta.setCorreta(escolhida.isCorreta());
            tentativa.adicionarResposta(resposta);

            if (escolhida.isCorreta()) {
                acertos++;
            }
        }

        
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