package com.ikonicit.resource.tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendApplicationConfirmation(String toEmail, String candidateName, String jobTitle) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("Application Received - " + jobTitle);

        message.setText(
                "Dear " + candidateName + ",\n\n" +
                        "Thank you for applying for the position of " + jobTitle + ".\n\n" +
                        "We have successfully received your application. " +
                        "Our recruitment team will review your profile and contact you if your qualifications match our requirements.\n\n" +
                        "Best Regards,\n" +
                        "HR Team"
        );

        mailSender.send(message);
    }

    public void sendHrNotification(List<String> hrEmails,
                                   String candidateName,
                                   String email,
                                   String phone,
                                   String jobTitle,
                                   Double matchScore) {

        SimpleMailMessage message = new SimpleMailMessage();

        // Convert List → Array
        message.setTo(hrEmails.toArray(new String[0]));

        message.setSubject("New Candidate Applied - " + jobTitle);

        message.setText(
                "A new candidate has applied for the job.\n\n" +
                        "Candidate Details:\n" +
                        "Name: " + candidateName + "\n" +
                        "Email: " + email + "\n" +
                        "Phone: " + phone + "\n" +
                        "Job Title: " + jobTitle + "\n" +
                        "Match Score: " + matchScore + "%\n\n" +
                        "Please login to the system to review the application."
        );

        mailSender.send(message);
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
}
