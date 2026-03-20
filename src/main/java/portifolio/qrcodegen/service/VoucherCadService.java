package portifolio.qrcodegen.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import portifolio.qrcodegen.dto.CadastroDTO;
import portifolio.qrcodegen.dto.VoucherDashboardDTO;
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
                .orElseThrow( () -> new RegraNegocioException("Qr Code invalido!"));

        if (voucherCad.getStatus() == StatusVoucher.RESGATADO){
            throw new RegraNegocioException("Este brinde ja foi resgatado!");
        }

        voucherCad.setStatus(StatusVoucher.RESGATADO);
        voucherCad.setDataResgate(Instant.now());
        voucherCadRepository.save(voucherCad);
    }

    public Page<VoucherDashboardDTO> listarTodosParaDashboard(Pageable pageable){
        Page<VoucherCad> paginaVoucher = voucherCadRepository.findAll(pageable);
        return paginaVoucher.map(voucher -> new VoucherDashboardDTO(
                voucher.getId(),
                voucher.getNome(),
                voucher.getStatus().toString(),
                voucher.getEmail()
        ));
    }

    public void validarResgateManual(Long id){
        VoucherCad voucherCad = voucherCadRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Voucher nao encontrado no sistema"));

        if (voucherCad.getStatus().equals(StatusVoucher.RESGATADO)){
            throw new RegraNegocioException("Atenção: Voucher ja consta como resgatado");
        }

        voucherCad.setStatus(StatusVoucher.RESGATADO);
        voucherCad.setDataResgate(Instant.now());

        voucherCadRepository.save(voucherCad);
    }
}
