package com.unialfa.bolao.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Envio de e-mails do sistema. O JavaMailSender só é criado quando
 * 'spring.mail.host' está configurado; sem isso, o serviço cai num
 * fallback que registra o conteúdo no log (útil em desenvolvimento).
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.mail.from:nao-responder@bolaocopa.com}")
    private String remetente;

    public EmailService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    public void enviarTokenRecuperacao(String destino, String token) {
        JavaMailSender sender = mailSenderProvider.getIfAvailable();
        if (sender == null) {
            log.info("[E-mail desativado - configure spring.mail.host] Token de recuperacao para {}: {}", destino, token);
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(remetente);
            msg.setTo(destino);
            msg.setSubject("Recuperacao de senha - Bolao Copa 2026");
            msg.setText(
                    "Voce solicitou a redefinicao da sua senha.\n\n" +
                    "Use o codigo abaixo no aplicativo (valido por 2 horas):\n\n" +
                    token + "\n\n" +
                    "Se nao foi voce, ignore este e-mail."
            );
            sender.send(msg);
            log.info("E-mail de recuperacao de senha enviado para {}", destino);
        } catch (Exception e) {
            log.warn("Falha ao enviar e-mail para {} ({}). Token registrado no log como fallback.", destino, e.getMessage());
            log.info("[Fallback] Token de recuperacao para {}: {}", destino, token);
        }
    }
}
