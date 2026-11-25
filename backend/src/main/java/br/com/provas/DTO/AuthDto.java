package br.com.provas.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDto {

    public record LoginRequest(
            @NotBlank(message = "Informe o e-mail") @Email(message = "E-mail inválido") String email,
            @NotBlank(message = "Informe a senha") String senha) {
    }

    public record RegistroRequest(
            @NotBlank(message = "Informe o nome") String nome,
            @NotBlank(message = "Informe o e-mail") @Email(message = "E-mail inválido") String email,
            @Size(min = 6, message = "A senha precisa ter pelo menos 6 caracteres") String senha) {
    }

    public record LoginResponse(String token, String nome, String perfil) {
    }
}