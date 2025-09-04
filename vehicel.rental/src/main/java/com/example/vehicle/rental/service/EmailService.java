package com.example.vehicle.rental.service;

import com.example.vehicle.rental.exception.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import lombok.*;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtp(String to, String otp) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Your OTP for Vehicle Rental Registration");
            helper.setText("Your OTP is: " + otp + "\nIt is valid for 10 minutes.");
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendingException("Failed to send OTP email to " + to);
        }
    }

    public void sendPasswordResetOtp(String to, String otp) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Your OTP for Password Reset");
            helper.setText("Use this OTP to reset your password: " + otp + "\nThis OTP is valid for 10 minutes.");
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendingException("Failed to send password reset OTP email to " + to);
        }
    }
}
