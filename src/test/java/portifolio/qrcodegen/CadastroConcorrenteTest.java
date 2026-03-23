package portifolio.qrcodegen;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import portifolio.qrcodegen.dto.CadastroDTO;
import portifolio.qrcodegen.repository.VoucherCadRepository;
import portifolio.qrcodegen.service.EmailService;
import portifolio.qrcodegen.service.VoucherCadService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
public class CadastroConcorrenteTest {
    @Autowired
    private VoucherCadService voucherCadService;

    @MockitoBean
    private EmailService emailService;

    @Autowired
    private VoucherCadRepository voucherCadRepository;

    @Test
    @DisplayName("Deve suportar 100 cadastros simultaneos usando o ThreadPool e o Banco")
    void testarCargaDeCadastroSimultaneos() throws Exception {
        int numeroDeRequisicoes = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(numeroDeRequisicoes);

        CountDownLatch travaDeLargada = new CountDownLatch(1);

        CountDownLatch travaDeChegada = new CountDownLatch(numeroDeRequisicoes);

        for(int i = 0; i < numeroDeRequisicoes; i++){
            final int index = i;
            executorService.execute(() -> {
                try {
                    travaDeLargada.await();
                    System.out.println("▶️ [Thread " + index + "] Iniciou o processo...");

                    CadastroDTO dto = new CadastroDTO(
                            "Cliente teste " + index,
                            "cliente" + index + "@test.com"
                    );
                    voucherCadService.cadastrarEGerarBrinde(dto);
                    System.out.println("✅ [Thread " + index + "] Salvou no banco e enviou e-mail!");
                }catch (Exception e){
                    System.err.println("❌ [Thread " + index + "] ESTOUROU ERRO: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    travaDeChegada.countDown();
                }
            });
        }
        System.out.println("🚀 Iniciando o teste de carga: Disparando 100 requisições simultâneas!");
        travaDeLargada.countDown();

        boolean terminouNoTempo = travaDeChegada.await(10, java.util.concurrent.TimeUnit.SECONDS);

        if(!terminouNoTempo){
            throw new RuntimeException("🚨 DEADLOCK! As threads travaram no meio do caminho. Olhe o console para ver quais pararam.");
        }

        executorService.shutdown();

        verify(emailService, timeout(5000).times(numeroDeRequisicoes))
                .enviarEmailcomQrCode(anyString(), anyString(), any(byte[].class));

        System.out.println("✅ O Thread Pool customizado gerenciou 100 disparos assíncronos com sucesso!");

        long totalNoBanco = voucherCadRepository.count();
        assertEquals(100, totalNoBanco);
        System.out.println("✅ O banco de dados H2 registrou exatamente 100 cadastros concorrentes sem perder dados!");
    }
}
