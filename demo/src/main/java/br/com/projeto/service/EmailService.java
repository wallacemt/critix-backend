// Source code is decompiled from a .class file using FernFlower decompiler.
package br.com.projeto.service;

import br.com.projeto.models.email.EmailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String emailRemetente;

    public EmailService() {
    }

    public String enviarEmail(EmailDTO emailDTO) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(this.emailRemetente);
            simpleMailMessage.setTo(emailDTO.destinatario());
            simpleMailMessage.setSubject(emailDTO.titulo());
            simpleMailMessage.setText(emailDTO.mensagem());
            this.javaMailSender.send(simpleMailMessage);
            return "Email enviado";
        } catch (Exception var3) {
            return "Erro ao enviar o email " + var3;
        }
    }
}
