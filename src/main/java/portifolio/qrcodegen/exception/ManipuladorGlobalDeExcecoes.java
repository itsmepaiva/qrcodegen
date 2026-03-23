package portifolio.qrcodegen.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
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
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroMensagem> capturarErrosGerais(Exception ex, HttpServletRequest request) {

        log.error("🚨 Erro crítico na rota {}: ", request.getRequestURI(), ex);

        ErroMensagem erro = new ErroMensagem(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocorreu um erro interno no servidor. Tente novamente mais tarde.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}
