package com.ecommerce.auth.infraestructure.driver_adapters.jpa_repository;

import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.domain.model.geteway.UsuarioGateway;
import com.ecommerce.auth.infraestructure.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UsuarioDataGatewayImpl implements UsuarioGateway {

    private final UsuarioDataJpaRepository repository;
    private final UsuarioMapper usuarioMapper;


    @Override
    public Usuario guardarUsuario(Usuario usuario) {
        UsuarioData usuarioData= usuarioMapper.toUsuarioData(usuario);
        return usuarioMapper.toUsuario(repository.save(usuarioData));
    }

    @Override
    public Usuario buscarUsuarioPorCedula(String cedula) {
        return repository.findById(cedula)
                .map(usuarioMapper::toUsuario)
                .orElse(new Usuario());
    }

    @Override
    public Usuario actualizarUsuario(Usuario usuario) {
        UsuarioData usuarioData = usuarioMapper.toUsuarioData(usuario);
        return usuarioMapper.toUsuario(repository.save(usuarioData));
    }

    @Override
    public void eliminarUsuarioPorCedula(String cedula) {
        repository.deleteById(cedula);
    }
}

