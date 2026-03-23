package portifolio.qrcodegen.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Async("taskExecutor")
    public void enviarEmailcomQrCode(String destinatario, String nomeCliente, byte[] qrCodeBytes) throws Exception {
        try {
            Context context = new Context();
            context.setVariable("nome", nomeCliente);
            String htmlFinal = templateEngine.process("email-voucher", context);

            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject("Seu QR Code para retirar o brinde chegou!");
            helper.setText(htmlFinal, true);

            ByteArrayResource qrCodeResource = new ByteArrayResource(qrCodeBytes);
            helper.addInline("qrcode_img", qrCodeResource, "image/png");
            helper.addAttachment("qr-code-brinde.png", qrCodeResource);

            mailSender.send(mensagem);
            System.out.println("✅ E-mail HTML enviado com sucesso para: " + destinatario);

        } catch (MessagingException e) {
            System.err.println("❌ Erro ao montar o e-mail HTML: " + e.getMessage());
        }
    }
}
