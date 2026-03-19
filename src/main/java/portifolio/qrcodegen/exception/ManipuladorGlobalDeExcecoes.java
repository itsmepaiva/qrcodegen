package portifolio.qrcodegen.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ManipuladorGlobalDeExcecoes {

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroMensagem> tratarRegraDeNegocio(RegraNegocioException ex, HttpServletRequest request){
        ErroMensagem erroMensagem = new ErroMensagem(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erroMensagem);
    }
}
