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
            String trackingLink,
            String location          // ← new parameter
    ) {
        try {
            // ===============================
            // Resolve Company Name by Location
            // ===============================
            String companyName;

            if ("India".equalsIgnoreCase(location)) {
                companyName = "I-Ray IT Solutions";
            } else if ("Sweden".equalsIgnoreCase(location)) {
                companyName = "I-Ray IT Solutions AB";
            } else {
                companyName = "I-Ray IT Solutions";  // default fallback
            }

            // ===============================
            // Build Email
            // ===============================
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Application Received - " + jobTitle);

            String htmlContent =
                    "<p>Hello " + candidateName + ",</p>" +

                            "<p>Thank you for applying to the <strong>" + jobTitle + "</strong> position at " +
                            "<strong>" + companyName + "</strong>. We've successfully received your application " +
                            "and appreciate the time you took to apply.</p>" +

                            "<p>Our recruitment team is currently reviewing your profile, and we aim to get back " +
                            "to you with an update as soon as possible.</p>" +

                            "<p>In the meantime, feel free to visit our website to learn more about our work and culture:<br>" +
                            "<a href='https://www.irayitsolutions.com/' style='color:#007BFF;'>" + companyName + "</a></p>" +

                            "<p>Thank you once again for your interest in <strong>" + companyName + "</strong>. " +
                            "We look forward to connecting with you.</p>" +

                            "<p>" +
                            "<a href='" + trackingLink + "' " +
                            "style='background-color:#007BFF; color:#ffffff; padding:10px 16px; " +
                            "text-decoration:none; border-radius:4px; display:inline-block;'>" +
                            "View your application status</a>" +
                            "</p>" +

                            "<p>Kind regards,<br><br>" +
                            companyName + " Recruitment Team</p>";

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void sendRejectionEmail(String email, String name, String jobTitle, String location) {

        try {
            String companyName;

            if ("India".equalsIgnoreCase(location)) {
                companyName = "I-Ray IT Solutions";
            } else if ("Sweden".equalsIgnoreCase(location)) {
                companyName = "I-Ray IT Solutions AB";
            } else {
                companyName = "I-Ray IT Solutions";  // default fallback
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Update on your application for " + jobTitle);

            String htmlContent =
                    "<p>Dear " + name + ",</p>" +

                            "<p>Thank you for your patience while we reviewed applications for the " +
                            "<strong>" + jobTitle + "</strong> role.</p>" +

                            "<p>We truly appreciate your interest in joining <strong>" + companyName + "</strong>. " +
                            "We know that applying takes time and effort, and we are grateful for the " +
                            "opportunity to review your profile.</p>" +

                            "<p>After careful consideration, we regret to inform you that we will be moving " +
                            "forward with other candidates for this position.</p>" +

                            "<p>However, we encourage you to keep an eye on future opportunities with us " +
                            "that match your skills and experience.</p>" +

                            "<p>We sincerely wish you all the best in your job search and future endeavors.</p>" +

                            "<p>Warm regards,<br><br>" +
                            "HR Team<br>" +
                            "<strong>" + companyName + "</strong></p>";

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (Exception e) {
            throw new MailSendFailedException("Failed to send rejection email");
        }
    }
}
