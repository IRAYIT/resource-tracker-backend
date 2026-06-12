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
                companyName = "I-Ray IT Solutions INC";
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
    public void sendStatusUpdateEmail(String email, String name, String jobTitle, String location, String status) {

        try {
            String companyName = "Sweden".equalsIgnoreCase(location)
                    ? "I-Ray IT Solutions AB"
                    : "I-Ray IT Solutions";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject(getEmailSubject(jobTitle, status));
            helper.setText(buildEmailBody(name, jobTitle, companyName, status), true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new MailSendFailedException("Failed to send status update email for status: " + status);
        }
    }

    private String getEmailSubject(String jobTitle, String status) {
        return switch (status.toUpperCase()) {
            case "APPLIED"     -> "We've received your application for " + jobTitle;
            case "SHORTLISTED" -> "Great news! You've been shortlisted for " + jobTitle;
            case "INTERVIEW"   -> "Interview Invitation – " + jobTitle;
            case "SELECTED"    -> "Congratulations! You've been selected for " + jobTitle;
            case "REJECTED"    -> "Update on your application for " + jobTitle;
            default            -> "Application status update – " + jobTitle;
        };
    }

    private String buildEmailBody(String name, String jobTitle, String companyName, String status) {
        String bodyContent = switch (status.toUpperCase()) {

            case "APPLIED" -> """
                <p>We have successfully received your application for the
                <strong>%s</strong> role at <strong>%s</strong>.</p>
                <p>Our team is currently reviewing applications and we will get back
                to you with an update soon. We appreciate your patience.</p>
                """.formatted(jobTitle, companyName);

            case "SHORTLISTED" -> """
                <p>We are pleased to inform you that after reviewing your profile,
                you have been <strong>shortlisted</strong> for the
                <strong>%s</strong> role at <strong>%s</strong>.</p>
                <p>Our team will be in touch shortly with the next steps in the
                selection process. Please keep an eye on your inbox.</p>
                """.formatted(jobTitle, companyName);

            case "INTERVIEW" -> """
                <p>We are excited to invite you for an <strong>interview</strong>
                for the <strong>%s</strong> role at <strong>%s</strong>.</p>
                <p>Our HR team will reach out to you shortly with the interview
                schedule and further details. Please ensure your contact information
                is up to date.</p>
                """.formatted(jobTitle, companyName);

            case "SELECTED" -> """
                <p>We are thrilled to inform you that you have been
                <strong>selected</strong> for the <strong>%s</strong> role at
                <strong>%s</strong>!</p>
                <p>This is a testament to your skills and experience, and we are
                excited to welcome you to the team. Our HR team will be in touch
                very soon with the next steps, including onboarding details.</p>
                """.formatted(jobTitle, companyName);

            case "REJECTED" -> """
                <p>Thank you for your patience while we reviewed applications for
                the <strong>%s</strong> role.</p>
                <p>We truly appreciate your interest in joining
                <strong>%s</strong> and the time and effort you invested in
                your application.</p>
                <p>After careful consideration, we regret to inform you that we
                will be moving forward with other candidates for this position.
                We encourage you to keep an eye on future opportunities with us
                that match your skills and experience.</p>
                <p>We sincerely wish you all the best in your job search and
                future endeavors.</p>
                """.formatted(jobTitle, companyName);

            default -> """
                <p>There has been an update to your application for the
                <strong>%s</strong> role at <strong>%s</strong>.</p>
                <p>Please contact our HR team for further details.</p>
                """.formatted(jobTitle, companyName);
        };

        return """
            <p>Dear %s,</p>
            %s
            <p>Warm regards,<br><br>
            HR Team<br>
            <strong>%s</strong></p>
            """.formatted(name, bodyContent, companyName);
    }
}
