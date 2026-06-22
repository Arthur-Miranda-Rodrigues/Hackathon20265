package com.unialfa.bolao;

import com.unialfa.bolao.model.PerfilUsuario;
import com.unialfa.bolao.model.Usuario;
import com.unialfa.bolao.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class BolaoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BolaoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDataBase(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!usuarioRepository.existsByEmail("admin@bolao.com")) {
                Usuario admin = new Usuario();
                admin.setNome("Administrador");
                admin.setEmail("admin@bolao.com");
                admin.setSenha(passwordEncoder.encode("admin123"));
                admin.setPerfil(PerfilUsuario.ADMIN);
                admin.setBloqueado(false);
                usuarioRepository.save(admin);
            }
        };
    }
}
