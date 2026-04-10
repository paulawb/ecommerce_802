package com.ecommerce.auth.infraestructure.mapper;
import org.springframework.stereotype.Component;
import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.infraestructure.driver_adapters.jpa_repository.UsuarioData;

@Component
public class UsuarioMapper {


    public Usuario toUsuario(UsuarioData usuarioData){
        return new Usuario(
                usuarioData.getCedula(),
                usuarioData.getNombres(),
                usuarioData.getCorreo(),
                usuarioData.getPassword(),
                usuarioData.getEdad(),
                usuarioData.getTelefono(),
                usuarioData.getRol()
        );
    }
    public UsuarioData toUsuarioData (Usuario usuario){
        return new UsuarioData(
                usuario.getCedula(),
                usuario.getNombres(),
                usuario.getCorreo(),
                usuario.getPassword(),
                usuario.getEdad(),
                usuario.getTelefono(),
                usuario.getRol()
        );

    }
}
