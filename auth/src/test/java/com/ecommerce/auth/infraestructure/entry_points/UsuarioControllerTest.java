package com.ecommerce.auth.infraestructure.entry_points;

import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.domain.model.geteway.UsuarioGateway;
import com.ecommerce.auth.domain.usecase.UsuarioUseCase;
import com.ecommerce.auth.infraestructure.driver_adapters.jpa_repository.UsuarioData;
import com.ecommerce.auth.infraestructure.mapper.UsuarioMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsuarioControllerTest {

    @Test
    void eliminarUsuario_retornaNotFound_cuandoNoExiste() {
        UsuarioGateway gateway = new FakeUsuarioGateway(false);

        UsuarioController controller = new UsuarioController(new UsuarioUseCase(gateway), new UsuarioMapper());

        ResponseEntity<Void> response = controller.eliminarUsuario("123");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void eliminarUsuario_retornaOk_cuandoExiste() {
        UsuarioGateway gateway = new FakeUsuarioGateway(true);

        UsuarioController controller = new UsuarioController(new UsuarioUseCase(gateway), new UsuarioMapper());

        ResponseEntity<Void> response = controller.eliminarUsuario("123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void guardarUsuario_retornaConflict_cuandoCedulaYaExiste() {
        UsuarioGateway gateway = new FakeUsuarioGateway(true);

        UsuarioController controller = new UsuarioController(new UsuarioUseCase(gateway), new UsuarioMapper());

        ResponseEntity<?> response = controller.guardarUsuario(
                new UsuarioData("123", "Ana", "ana@mail.com", "secret", 30, "1234567890", "USER")
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void actualizarUsuario_retornaOk_yConservaCamposNoEnviados() {
        UsuarioGateway gateway = new FakeUsuarioGateway(true);

        UsuarioController controller = new UsuarioController(new UsuarioUseCase(gateway), new UsuarioMapper());

        ResponseEntity<?> response = controller.actualizarUsuario(
                "123",
                new UsuarioData(null, "Ana Maria", null, null, null, null, null)
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Usuario body = (Usuario) response.getBody();
        assertEquals("123", body.getCedula());
        assertEquals("Ana Maria", body.getNombres());
        assertEquals("ana@mail.com", body.getCorreo());
        assertEquals("secret", body.getPassword());
    }

    private static class FakeUsuarioGateway implements UsuarioGateway {
        private final boolean existeUsuario;

        private FakeUsuarioGateway(boolean existeUsuario) {
            this.existeUsuario = existeUsuario;
        }

        @Override
        public Usuario guardarUsuario(Usuario usuario) {
            return usuario;
        }

        @Override
        public Usuario buscarUsuarioPorCedula(String cedula) {
            if (!existeUsuario) {
                return new Usuario();
            }
            return new Usuario(cedula, "Ana", "ana@mail.com", "secret", 30, "1234567890", "USER");
        }

        @Override
        public Usuario actualizarUsuario(Usuario usuario) {
            return usuario;
        }

        @Override
        public void eliminarUsuarioPorCedula(String cedula) {
        }
    }
}
