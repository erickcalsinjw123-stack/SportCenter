
package com.sportcenter.sportcenter.service;

import com.sportcenter.sportcenter.dao.UsuarioDAO;
import com.sportcenter.sportcenter.dto.LoginRequest;
import com.sportcenter.sportcenter.entity.Usuario;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioDAO usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UsuarioDAO usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario login(LoginRequest request) {

        Usuario usuario = usuarioRepository
                .findByNombreUsuario(request.getNombreUsuario())
                .orElseThrow(() ->
                    new BadCredentialsException(
                        "Usuario o contraseña incorrectos"
                    )
                );

        if (!"ACTIVO".equals(usuario.getEstado())) {
            throw new DisabledException(
                "El usuario se encuentra inactivo"
            );
        }

        boolean coincide = passwordEncoder.matches(
                request.getContrasena(),
                usuario.getContrasena()
        );

        if (!coincide) {
            throw new BadCredentialsException(
                "Usuario o contraseña incorrectos"
            );
        }

        return usuario;
    }
}
