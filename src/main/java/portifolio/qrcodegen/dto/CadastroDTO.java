package portifolio.qrcodegen.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CadastroDTO(@NotBlank(message = "O nome é obrigatorio") String nome,
                          @NotBlank(message = "O email é obrigatorio") @Email(message = "Formato de e-mail invalido") String email) {
}
