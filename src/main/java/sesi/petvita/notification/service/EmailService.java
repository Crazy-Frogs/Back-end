package sesi.petvita.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Pega o e-mail remetente do application.properties
    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Envia um e-mail de forma assíncrona.
     * A anotação @Async faz com que este método rode em uma thread separada,
     * não bloqueando a execução principal da aplicação.
     */
    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            System.out.println("E-mail de lembrete enviado com sucesso para: " + to);
        } catch (MailException e) {
            // Em um projeto real, você usaria um logger (SLF4J)
            System.err.println("Erro ao enviar e-mail para " + to + ": " + e.getMessage());
        }
    }
}