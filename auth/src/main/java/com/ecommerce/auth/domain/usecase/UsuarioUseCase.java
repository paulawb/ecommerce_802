package com.ecommerce.auth.domain.usecase;

import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.domain.model.geteway.UsuarioGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UsuarioUseCase {
    private final UsuarioGateway usuarioGateway;

    public Usuario guardarUsuario(Usuario usuario) {
        if (usuario.getCorreo() == null || usuario.getPassword() == null) {
            throw new NullPointerException("usuario o contraseÃ±a son incorrectas - guardarUsuario");
        }

        Usuario usuarioExistente = usuarioGateway.buscarUsuarioPorCedula(usuario.getCedula());
        if (usuarioExistente.getCedula() != null) {
            throw new IllegalStateException("Ya existe un usuario con la cedula " + usuario.getCedula());
        }

        return usuarioGateway.guardarUsuario(usuario);
    }

    public Usuario buscarUsuarioPorCedula(String cedula) {
        try {
            return usuarioGateway.buscarUsuarioPorCedula(cedula);
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return new Usuario();
        }
    }

    public void eliminarUsuarioPorCedula(String cedula) {
        usuarioGateway.eliminarUsuarioPorCedula(cedula);
    }

    public Usuario actualizarUsuario(String cedula, Usuario cambios) {
        Usuario usuarioExistente = usuarioGateway.buscarUsuarioPorCedula(cedula);
        if (usuarioExistente.getCedula() == null) {
            throw new IllegalStateException("No existe un usuario con la cedula " + cedula);
        }

        if (cambios.getNombres() != null) {
            usuarioExistente.setNombres(cambios.getNombres());
        }
        if (cambios.getCorreo() != null) {
            usuarioExistente.setCorreo(cambios.getCorreo());
        }
        if (cambios.getPassword() != null) {
            usuarioExistente.setPassword(cambios.getPassword());
        }
        if (cambios.getEdad() != null) {
            usuarioExistente.setEdad(cambios.getEdad());
        }
        if (cambios.getTelefono() != null) {
            usuarioExistente.setTelefono(cambios.getTelefono());
        }
        if (cambios.getRol() != null) {
            usuarioExistente.setRol(cambios.getRol());
        }

        return usuarioGateway.actualizarUsuario(usuarioExistente);
    }
}
