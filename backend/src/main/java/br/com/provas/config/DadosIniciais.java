package br.com.provas.config;

import br.com.provas.model.Perfil;
import br.com.provas.model.Usuario;
import br.com.provas.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DadosIniciais implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DadosIniciais(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        criarSeNaoExistir("Professor", "professor@provas.com", Perfil.PROFESSOR);
        criarSeNaoExistir("Aluno Teste", "aluno@provas.com", Perfil.ALUNO);
    }

    private void criarSeNaoExistir(String nome, String email, Perfil perfil) {
        if (usuarioRepository.existsByEmail(email)) {
            return;
        }
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode("123456"));
        usuario.setPerfil(perfil);
        usuarioRepository.save(usuario);
    }
}