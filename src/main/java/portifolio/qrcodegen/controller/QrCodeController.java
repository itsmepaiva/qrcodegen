package portifolio.qrcodegen.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import portifolio.qrcodegen.dto.CadastroDTO;
import portifolio.qrcodegen.dto.ResgateQrCodeDTO;
import portifolio.qrcodegen.dto.ResgateResponseDTO;
import portifolio.qrcodegen.service.VoucherCadService;

@RestController
@RequestMapping("/qrcode")
@CrossOrigin(origins = "*")
public class QrCodeController {

    private final VoucherCadService voucherCadService;

    public QrCodeController(VoucherCadService voucherCadService) {
        this.voucherCadService = voucherCadService;
    }

    @PostMapping("/gerar")
    public ResponseEntity<Void> gerarCadastro(@RequestBody @Valid CadastroDTO request) throws Exception{
        voucherCadService.cadastrarEGerarBrinde(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/resgatar")
    public ResponseEntity<ResgateResponseDTO> resgatarQrCode(@RequestBody @Valid ResgateQrCodeDTO qrCodeDTO){
        ResgateResponseDTO resgateResponseDTO = voucherCadService.validarResgate(qrCodeDTO.token());
        return ResponseEntity.ok(resgateResponseDTO);
    }

    @PutMapping("/resgatar/{id}")
    public ResponseEntity<Void> resgateManualAdmin(@PathVariable Long id){
        voucherCadService.validarResgateManual(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarVoucher(@PathVariable Long id){
        voucherCadService.deletarVoucher(id);
        return ResponseEntity.ok().build();
    }
}
