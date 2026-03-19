package portifolio.qrcodegen.dto;

import jakarta.validation.constraints.NotBlank;

public record ResgateQrCodeDTO(@NotBlank(message = "O token do QR Code é obrigatorio") String token) {
}
