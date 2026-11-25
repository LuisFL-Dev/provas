package br.com.provas.controller;

import br.com.provas.DTO.TentativaDto.*;
import br.com.provas.model.Usuario;
import br.com.provas.service.TentativaService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TentativaController {

    private final TentativaService tentativaService;

    public TentativaController(TentativaService tentativaService) {
        this.tentativaService = tentativaService;
    }

    @PostMapping("/provas/{id}/respostas")
    public ResultadoResponse responder(@PathVariable Long id,
                                       @Valid @RequestBody ResponderRequest request,
                                       @AuthenticationPrincipal Usuario aluno) {
        return tentativaService.responder(id, request, aluno);
    }

    @GetMapping("/provas/{id}/minhas-tentativas")
    public TentativasInfo minhasTentativas(@PathVariable Long id,
                                           @AuthenticationPrincipal Usuario aluno) {
        return tentativaService.minhasTentativasNaProva(id, aluno);
    }

    @GetMapping("/tentativas/minhas")
    public List<TentativaResumoResponse> minhas(@AuthenticationPrincipal Usuario aluno) {
        return tentativaService.minhas(aluno);
    }
}