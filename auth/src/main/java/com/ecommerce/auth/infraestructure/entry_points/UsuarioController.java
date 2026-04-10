package com.ecommerce.auth.infraestructure.entry_points;

import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.domain.usecase.UsuarioUseCase;
import com.ecommerce.auth.infraestructure.driver_adapters.jpa_repository.UsuarioData;
import com.ecommerce.auth.infraestructure.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ecommerce/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioUseCase usuarioUseCase;
    private final UsuarioMapper usuarioMapper;

    @PostMapping("/guardar")
    public ResponseEntity<?> guardarUsuario(@RequestBody UsuarioData usuarioData) {
        try {
            Usuario usuarioValidadoGuardado = usuarioUseCase.guardarUsuario(usuarioMapper.toUsuario(usuarioData));
            return new ResponseEntity<>(usuarioValidadoGuardado, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/buscar/{cedula}")
    public ResponseEntity<Usuario> buscarUsuario(@PathVariable String cedula) {
        Usuario buscarUsuario = usuarioUseCase.buscarUsuarioPorCedula(cedula);
        return new ResponseEntity<>(buscarUsuario, HttpStatus.OK);
    }

    @PutMapping("/actualizar/{cedula}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable String cedula, @RequestBody UsuarioData usuarioData) {
        try {
            Usuario usuarioActualizado = usuarioUseCase.actualizarUsuario(cedula, usuarioMapper.toUsuario(usuarioData));
            return new ResponseEntity<>(usuarioActualizado, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/eliminar/{cedula}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String cedula) {
        Usuario usuario = usuarioUseCase.buscarUsuarioPorCedula(cedula);
        if (usuario.getCedula() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        usuarioUseCase.eliminarUsuarioPorCedula(cedula);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
