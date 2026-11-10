package com.sportcenter.sportcenter.service;

import com.sportcenter.sportcenter.dao.UsuarioDAO;
import com.sportcenter.sportcenter.entity.Usuario;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioDAO usuarioRepository;

    public CustomUserDetailsService(UsuarioDAO usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String nombreUsuario)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository
                .findByNombreUsuario(nombreUsuario)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado"
                        )
                );

        return User.builder()
                .username(usuario.getNombreUsuario())
                .password(usuario.getContrasena())
                .roles(usuario.getRol())
                .disabled(!"ACTIVO".equalsIgnoreCase(usuario.getEstado()))
                .build();
    }
}
