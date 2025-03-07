package br.com.projeto.service;

import br.com.projeto.dto.EmailDTO;
import br.com.projeto.models.emailVerification.EmailVerification;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.repositorio.EmailVerificationRepository;
import br.com.projeto.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
public class EmailVerificationService {
    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    private String generateVerificationCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

    public String sendVerificationCode(String email) {
        // Verifica se o e-mail já pertence a algum usuário
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
        if (usuarioOptional.isPresent()) {
            throw new DuplicateKeyException("Email já utilizado!");
        }

        // Gera novo código
        String verificationCode = generateVerificationCode();

        // Verifica se já existe um código para esse e-mail
        Optional<EmailVerification> verificationOpt = emailVerificationRepository.findByEmail(email);
        EmailVerification emailVerification;

        if (verificationOpt.isPresent()) {
            // Se já existe, apenas atualiza o código e a data
            emailVerification = verificationOpt.get();
            emailVerification.setVerificationCode(verificationCode);
            emailVerification.setCreatedAt(new Date());
        } else {
            // Se não existe, cria um novo registro
            emailVerification = new EmailVerification();
            emailVerification.setEmail(email);
            emailVerification.setVerificationCode(verificationCode);
            emailVerification.setCreatedAt(new Date());
        }

        // Salva no banco de dados
        emailVerificationRepository.saveAndFlush(emailVerification);

        // Envia o e-mail
        EmailDTO emailDTO = new EmailDTO(
                email,
                "Código de Verificação - Critix (" + verificationCode +")",
                "Seu código de verificação é: " + verificationCode
        );

        emailService.enviarEmail(emailDTO);

        return "Código de verificação enviado para " + email;
    }


    public boolean verifyCode(String email, String code) {
        Optional<EmailVerification> verificationOpt = emailVerificationRepository.findByEmail(email);
        if (verificationOpt.isPresent()) {
            EmailVerification verification = verificationOpt.get();

            // Validar código e tempo de expiração (10 minutos)
            long diffMinutes = (new Date().getTime() - verification.getCreatedAt().getTime()) / (60 * 1000);
            if (diffMinutes > 10) {
                return false; // Código expirado
            }

            return verification.getVerificationCode().equals(code);
        }
        return false;
    }

}
