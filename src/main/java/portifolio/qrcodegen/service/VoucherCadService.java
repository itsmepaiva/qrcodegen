package portifolio.qrcodegen.service;

import jakarta.transaction.Transactional;
import org.aspectj.weaver.IClassFileProvider;
import org.springframework.stereotype.Service;
import portifolio.qrcodegen.dto.CadastroDTO;
import portifolio.qrcodegen.entity.StatusVoucher;
import portifolio.qrcodegen.entity.VoucherCad;
import portifolio.qrcodegen.exception.RegraNegocioException;
import portifolio.qrcodegen.infra.QrCodeGenerator;
import portifolio.qrcodegen.repository.VoucherCadRepository;

import java.time.Instant;
import java.util.UUID;

@Service
public class VoucherCadService {

    private final VoucherCadRepository voucherCadRepository;

    private final QrCodeGenerator qrCodeGenerator;

    private final EmailService emailService;

    public VoucherCadService(VoucherCadRepository voucherCadRepository, QrCodeGenerator qrCodeGenerator, EmailService emailService) {
        this.voucherCadRepository = voucherCadRepository;
        this.qrCodeGenerator = qrCodeGenerator;
        this.emailService = emailService;
    }

    @Transactional
    public void cadastrarEGerarBrinde(CadastroDTO dto) throws Exception{

        if (voucherCadRepository.existsByEmail(dto.email())) {
            throw new RegraNegocioException("Este e-mail já foi utilizado para gerar um QR Code.");
        }

        String tokenUnico = UUID.randomUUID().toString();

        VoucherCad voucherCad = new VoucherCad();
        voucherCad.setEmail(dto.email());
        voucherCad.setNome(dto.nome());
        voucherCad.setToken(tokenUnico);
        voucherCad.setStatus(StatusVoucher.PENDENTE);
        voucherCadRepository.save(voucherCad);

        byte[] imagemQrCode = qrCodeGenerator.gerarQrCode(tokenUnico);

        emailService.enviarEmailcomQrCode(voucherCad.getEmail(), imagemQrCode);
    }

    @Transactional
    public void validarResgate(String token){
        VoucherCad voucherCad = voucherCadRepository.findByToken(token)
                .orElseThrow( () -> new RuntimeException("Qr Code invalido!"));

        if (voucherCad.getStatus() == StatusVoucher.RESGATADO){
            throw new RuntimeException("Este brinde ja foi resgatado!");
        }

        voucherCad.setStatus(StatusVoucher.RESGATADO);
        voucherCad.setDataResgate(Instant.now());
        voucherCadRepository.save(voucherCad);
    }
}
