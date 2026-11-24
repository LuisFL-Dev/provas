package br.com.provas.service;

import br.com.provas.DTO.ProvaDto.*;
import br.com.provas.model.Alternativa;
import br.com.provas.model.Prova;
import br.com.provas.model.Questao;
import br.com.provas.repository.ProvaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProvaService {

    private final ProvaRepository provaRepository;

    public ProvaService(ProvaRepository provaRepository) {
        this.provaRepository = provaRepository;
    }

    // Lista todas as provas (as mais novas primeiro)
    @Transactional(readOnly = true)
    public List<ProvaResumoResponse> listar() {
        List<ProvaResumoResponse> resultado = new ArrayList<>();

        for (Prova prova : provaRepository.findAll(Sort.by("id").descending())) {
            resultado.add(new ProvaResumoResponse(
                    prova.getId(),
                    prova.getTitulo(),
                    prova.getDescricao(),
                    prova.getQuestoes().size()));
        }
        return resultado;
    }

    // Busca uma prova com as questões (sem o gabarito)
    @Transactional(readOnly = true)
    public ProvaDetalheResponse buscar(Long id) {
        Prova prova = buscarEntidade(id);
        return paraDetalhe(prova);
    }

    // Cria a prova com todas as questões e alternativas
    @Transactional
    public ProvaDetalheResponse criar(ProvaRequest request) {
        Prova prova = new Prova();
        prova.setTitulo(request.titulo());
        prova.setDescricao(request.descricao());

        int ordem = 1;
        for (QuestaoRequest qr : request.questoes()) {

            // Regra: cada questão precisa ter exatamente 1 alternativa correta
            int corretas = 0;
            for (AlternativaRequest ar : qr.alternativas()) {
                if (ar.correta()) {
                    corretas++;
                }
            }
            if (corretas != 1) {
                throw new RegraNegocioException(
                        "A questão '" + qr.enunciado() + "' precisa ter exatamente 1 alternativa correta");
            }

            Questao questao = new Questao();
            questao.setEnunciado(qr.enunciado());
            questao.setOrdem(ordem);
            ordem++;

            for (AlternativaRequest ar : qr.alternativas()) {
                Alternativa alternativa = new Alternativa();
                alternativa.setTexto(ar.texto());
                alternativa.setCorreta(ar.correta());
                questao.adicionarAlternativa(alternativa);
            }

            prova.adicionarQuestao(questao);
        }

        Prova salva = provaRepository.save(prova);
        return paraDetalhe(salva);
    }

    @Transactional
    public void excluir(Long id) {
        Prova prova = buscarEntidade(id);
        provaRepository.delete(prova);
    }

    // Busca a prova no banco ou lança erro 404
    public Prova buscarEntidade(Long id) {
        Optional<Prova> prova = provaRepository.findById(id);
        if (prova.isEmpty()) {
            throw new NaoEncontradoException("Prova não encontrada");
        }
        return prova.get();
    }

    // Converte a entidade em DTO, deixando de fora o campo "correta"
    private ProvaDetalheResponse paraDetalhe(Prova prova) {
        List<QuestaoResponse> questoes = new ArrayList<>();

        for (Questao questao : prova.getQuestoes()) {
            List<AlternativaResponse> alternativas = new ArrayList<>();

            for (Alternativa alternativa : questao.getAlternativas()) {
                alternativas.add(new AlternativaResponse(alternativa.getId(), alternativa.getTexto()));
            }

            questoes.add(new QuestaoResponse(questao.getId(), questao.getEnunciado(), alternativas));
        }

        return new ProvaDetalheResponse(prova.getId(), prova.getTitulo(), prova.getDescricao(), questoes);
    }
}