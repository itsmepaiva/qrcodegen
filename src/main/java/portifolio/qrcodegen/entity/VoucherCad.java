package portifolio.qrcodegen.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherCad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Email
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    private StatusVoucher status;

    @CreationTimestamp
    private Instant dataCriacao;

    private Instant dataResgate;

}
