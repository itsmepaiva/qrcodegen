package portifolio.qrcodegen.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import portifolio.qrcodegen.dto.CadastroDTO;
import portifolio.qrcodegen.dto.ResgateQrCodeDTO;
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
    public ResponseEntity<Void> resgatarQrCode(@RequestBody @Valid ResgateQrCodeDTO qrCodeDTO){
        voucherCadService.validarResgate(qrCodeDTO.token());
        return ResponseEntity.ok().build();
    }
}
