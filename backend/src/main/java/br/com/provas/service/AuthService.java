package br.com.provas.service;

import br.com.provas.DTO.AuthDto.*;
import br.com.provas.model.Perfil;
import br.com.provas.model.Usuario;
import br.com.provas.repository.UsuarioRepository;
import br.com.provas.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException();
        }

        return gerarResposta(usuario);
    }

    // Cadastro aberto é só para alunos
    @Transactional
    public LoginResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new RegraNegocioException("Este e-mail já está cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setPerfil(Perfil.ALUNO);
        usuarioRepository.save(usuario);

        return gerarResposta(usuario);
    }

    private LoginResponse gerarResposta(Usuario usuario) {
        return new LoginResponse(
                jwtService.gerarToken(usuario),
                usuario.getNome(),
                usuario.getPerfil().name());
    }
}