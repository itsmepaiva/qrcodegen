package portifolio.qrcodegen.exception;

import java.time.Instant;

public record ErroMensagem(
        Instant momento,
        Integer statusHttp,
        String mensagemErro,
        String caminhoDaRequisicao
) {
}
