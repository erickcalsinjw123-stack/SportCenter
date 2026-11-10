
package com.sportcenter.sportcenter.config;

import com.sportcenter.sportcenter.dao.UsuarioDAO;
import com.sportcenter.sportcenter.entity.Usuario;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner crearAdmin(
            UsuarioDAO repository,
            PasswordEncoder encoder) {

        return args -> {

            if (repository.findByNombreUsuario("admin").isEmpty()) {

                Usuario admin = new Usuario();

                admin.setNombreUsuario("admin");
                admin.setContrasena(
                        encoder.encode("SportCenter123!")
                );
                admin.setRol("ADMIN");
                admin.setEstado("ACTIVO");
                admin.setFechaCreacion(LocalDateTime.now());

                repository.save(admin);

                System.out.println(
                        "Administrador creado correctamente"
                );

            } else {

                System.out.println(
                        "El administrador ya existe"
                );
            }
        };
    }
}
