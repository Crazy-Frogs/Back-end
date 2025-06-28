package sesi.petvita.notification.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {


    //Teste ainda irei por o springmail.
    public void sendEmail(String to, String subject, String body) {
        System.out.println("========================================================");
        System.out.println("SIMULANDO ENVIO DE E-MAIL");
        System.out.println("PARA: " + to);
        System.out.println("ASSUNTO: " + subject);
        System.out.println("CORPO: " + body);
        System.out.println("========================================================");
    }
}
