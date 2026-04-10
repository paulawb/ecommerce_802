package com.ecommerce.auth.domain.model.geteway;

import com.ecommerce.auth.domain.model.Usuario;

public interface UsuarioGateway {
    Usuario guardarUsuario(Usuario usuario);

   Usuario buscarUsuarioPorCedula(String cedula);

   Usuario actualizarUsuario(Usuario usuario);

   void eliminarUsuarioPorCedula(String cedula);
}
