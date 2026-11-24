package br.com.provas.controller;

import br.com.provas.DTO.ProvaDto.*;
import br.com.provas.service.ProvaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/provas")
public class ProvaController {

    private final ProvaService provaService;

    public ProvaController(ProvaService provaService) {
        this.provaService = provaService;
    }

    @GetMapping
    public List<ProvaResumoResponse> listar() {
        return provaService.listar();
    }

    @GetMapping("/{id}")
    public ProvaDetalheResponse buscar(@PathVariable Long id) {
        return provaService.buscar(id);
    }

    @PostMapping
    public ResponseEntity<ProvaDetalheResponse> criar(@Valid @RequestBody ProvaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(provaService.criar(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        provaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}