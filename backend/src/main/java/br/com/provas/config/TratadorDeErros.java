package br.com.provas.config;

import br.com.provas.service.NaoEncontradoException;
import br.com.provas.service.RegraNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class TratadorDeErros {

    // 404: algo que não existe (ex.: prova 999)
    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> naoEncontrado(NaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", ex.getMessage()));
    }

    // 400: quebrou uma regra (ex.: duas alternativas corretas)
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Map<String, String>> regraNegocio(RegraNegocioException ex) {
        return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
    }

    // 400: campo inválido (ex.: título vazio)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacao(MethodArgumentNotValidException ex) {
        String mensagem = "Dados inválidos";
        FieldError primeiroErro = ex.getBindingResult().getFieldError();
        if (primeiroErro != null) {
            mensagem = primeiroErro.getDefaultMessage();
        }
        return ResponseEntity.badRequest().body(Map.of("erro", mensagem));
    }
}