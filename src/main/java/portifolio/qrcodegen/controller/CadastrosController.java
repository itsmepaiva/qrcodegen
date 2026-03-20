package portifolio.qrcodegen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import portifolio.qrcodegen.dto.VoucherDashboardDTO;
import portifolio.qrcodegen.service.VoucherCadService;

import java.util.List;

@RestController
@RequestMapping("/cadastros")
public class CadastrosController {

    @Autowired
    public VoucherCadService voucherCadService;

    @GetMapping
    public ResponseEntity<Page<VoucherDashboardDTO>> listarVouchers(@PageableDefault(page = 0, size = 10, sort = "id")Pageable pageable){
        Page<VoucherDashboardDTO> paginaDTO = voucherCadService.listarTodosParaDashboard(pageable);
        return ResponseEntity.ok(paginaDTO);
    }
}
