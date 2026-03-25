package com.ikonicit.resource.tracker.service;

import com.ikonicit.resource.tracker.exception.MailSendFailedException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendApplicationConfirmation(
            String toEmail,
            String candidateName,
            String jobTitle,
            String trackingLink
    ){

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("Application Received - " + jobTitle);

        message.setText(
                "Dear " + candidateName + ",\n\n" +
                        "Thank you for applying for the position of " + jobTitle + ".\n\n" +

                        "We have successfully received your application.\n\n" +

                        "Track your application here:\n" +
                        trackingLink + "\n\n" +

                        "Best Regards,\nHR Team"
        );

        mailSender.send(message);
    }

    public void sendHrNotificationWithAttachment(
            String hrEmail,
            String candidateName,
            String candidateEmail,
            String phone,
            String jobTitle,
            Double matchPercentage,
            MultipartFile cv
    ) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(hrEmail);
            helper.setSubject("New Candidate Applied - " + jobTitle);

            helper.setText(
                    "New Candidate Application\n\n" +
                            "Name: " + candidateName + "\n" +
                            "Email: " + candidateEmail + "\n" +
                            "Phone: " + phone + "\n" +
                            "Match Score: " + matchPercentage + "%\n" +
                            "Job Role: " + jobTitle + "\n\n" +
                            "Resume is attached."
            );

            // ✅ Attach CV
            helper.addAttachment(
                    cv.getOriginalFilename(),
                    new ByteArrayResource(cv.getBytes())
            );

            mailSender.send(message);

        } catch (Exception e) {
            throw new MailSendFailedException("Failed to send HR email with attachment");
        }
    }

    public void sendNewOpeningNotification(String toEmail, String jobTitle, String jobDescription) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("New Job Opening: " + jobTitle);
        message.setText(
                "Hello,\n\n" +
                        "We are excited to announce a new job opening!\n\n" +
                        "Position : " + jobTitle + "\n" +
                        "Description: " + jobDescription + "\n\n" +
                        "Please reach out to HR for more details.\n\n" +
                        "Best Regards,\nHR Team"
        );
        mailSender.send(message);
    }

    public void sendRejectionEmail(String email, String name, String jobTitle) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(email);
            message.setSubject("Update on your application for " + jobTitle);

            message.setText(
                    "Dear " + name + ",\n\n" +

                            "Thank you for your patience while we reviewed applications for the " + jobTitle + " role.\n\n" +

                            "We truly appreciate your interest in joining our organization. We know that applying takes time and effort, and we are grateful for the opportunity to review your profile.\n\n" +

                            "After careful consideration, we regret to inform you that we will be moving forward with other candidates for this position.\n\n" +

                            "However, we encourage you to keep an eye on future opportunities with us that match your skills and experience.\n\n" +

                            "We sincerely wish you all the best in your job search and future endeavors.\n\n" +

                            "Warm regards,\n" +
                            "HR Team\n" +
                            "I-Ray IT Solutions"
            );

            mailSender.send(message);

        } catch (Exception e) {
            throw new MailSendFailedException("Failed to send rejection email");
        }
    }
}
