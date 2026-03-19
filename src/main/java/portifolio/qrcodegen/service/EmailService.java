package portifolio.qrcodegen.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void enviarEmailcomQrCode(String destinatario, byte[] qrCodeBytes) throws Exception{
        MimeMessage mensagem = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensagem, true);

        helper.setTo(destinatario);
        helper.setSubject("Seu QR Code para retirar o brinde chegou!");
        helper.setText("Apresente o QR Code em anexo no nosso estande.");

        helper.addAttachment("qr-code-brinde.png", new ByteArrayResource(qrCodeBytes));

        mailSender.send(mensagem);
    }
}
