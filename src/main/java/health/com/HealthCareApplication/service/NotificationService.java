package health.com.HealthCareApplication.service;

import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
@Service
public class NotificationService {


    private final MailSender mailSender;


    public NotificationService(@Autowired(required = false) @Nullable MailSender mailSender) {
        this.mailSender = mailSender;
    }


    // If mailSender is not configured, sending will throw - catch it in caller and fallback to console logging
    public void sendEmail(String to, String subject, String text) {
        if(to==null || to.isBlank()){
            System.out.println("[Notification] no recipient, message: " + text);
            return;
        }
        if (mailSender == null) {
            // fallback: no mail bean configured
            System.out.println("[Notification] (fallback) To:" + to + " Subject:" + subject + " Message:" + text);
            return;
        }

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(text);
            mailSender.send(msg);
            System.out.println("[Notification] Email sent to " + to);
        } catch (Exception e) {
            System.out.println("[Notification] failed to send email — fallback to console. Reason: " + e.getMessage());
            System.out.println("[Notification] (fallback) To:" + to + " Subject:" + subject + " Message:" + text);
        }
    }
}