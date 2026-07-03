package com.aurionpro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.aurionpro.entity.Ticket;
import com.aurionpro.entity.User;
import com.aurionpro.entity.enums.UserRole;
import com.aurionpro.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendTicketCreatedEmail(Ticket ticket) {
        String subject = "[" + ticket.getTicketNumber() + "] Ticket Received";
        String body = buildEmailBody("Ticket Received",
                "Hi " + ticket.getRaisedBy().getFullName() + ",",
                "Your ticket has been successfully submitted. Our support team will review it shortly.",
                ticket, "Your ticket is currently: <strong>NEW</strong>");
        sendEmail(ticket.getRaisedBy().getEmail(), subject, body);
    }

    @Async
    public void sendNewTicketToSupportDesk(Ticket ticket) {
        List<User> supportDesks = userRepository.findByRoleAndIsActiveTrue(UserRole.SUPPORT_DESK);
        String subject = "[NEW] " + ticket.getTicketNumber() + " - " + ticket.getTitle();
        String body = buildEmailBody("New Ticket Received", "Hi Support Team,",
                "A new ticket has been submitted and requires your review.",
                ticket, "Priority: <strong>" + ticket.getPriority() + "</strong>");
        for (User desk : supportDesks) {
            sendEmail(desk.getEmail(), subject, body);
        }
    }

    @Async
    public void sendTicketAssignedToDeveloper(Ticket ticket) {
        if (ticket.getAssignedTo() == null) return;
        String subject = "[ASSIGNED] " + ticket.getTicketNumber() + " - " + ticket.getTitle();
        String body = buildEmailBody("Ticket Assigned to You",
                "Hi " + ticket.getAssignedTo().getFullName() + ",",
                "A ticket has been assigned to you. Please review and begin working on it.",
                ticket, "Priority: <strong>" + ticket.getPriority() + "</strong> | Severity: <strong>" + ticket.getSeverity() + "</strong>");
        sendEmail(ticket.getAssignedTo().getEmail(), subject, body);

        String clientBody = buildEmailBody("Ticket Assigned",
                "Hi " + ticket.getRaisedBy().getFullName() + ",",
                "Your ticket has been reviewed and assigned to our team. We are now working on it.",
                ticket, "Status: <strong>ASSIGNED</strong>");
        sendEmail(ticket.getRaisedBy().getEmail(),
                "[UPDATE] " + ticket.getTicketNumber() + " - Assigned to team", clientBody);
    }

    @Async
    public void sendCommentAddedEmail(Ticket ticket, User commentedBy) {
        String subject = "[COMMENT] " + ticket.getTicketNumber() + " - New comment added";

        if (commentedBy.getRole() != UserRole.CLIENT) {
            String body = buildEmailBody("New Comment on Your Ticket",
                    "Hi " + ticket.getRaisedBy().getFullName() + ",",
                    commentedBy.getFullName() + " has added a comment on your ticket.",
                    ticket, "Please login to view the comment and respond if needed.");
            sendEmail(ticket.getRaisedBy().getEmail(), subject, body);
        }

        if (commentedBy.getRole() == UserRole.CLIENT && ticket.getAssignedTo() != null) {
            String body = buildEmailBody("Client Replied on Ticket",
                    "Hi " + ticket.getAssignedTo().getFullName() + ",",
                    "The client has added a comment on the ticket assigned to you.",
                    ticket, "Please login to review the client's response.");
            sendEmail(ticket.getAssignedTo().getEmail(), subject, body);
        }

        List<User> supportDesks = userRepository.findByRoleAndIsActiveTrue(UserRole.SUPPORT_DESK);
        for (User desk : supportDesks) {
            if (!desk.getEmail().equals(commentedBy.getEmail())) {
                String body = buildEmailBody("New Activity on Ticket", "Hi Support Team,",
                        commentedBy.getFullName() + " commented on ticket " + ticket.getTicketNumber() + ".",
                        ticket, "Role: <strong>" + commentedBy.getRole() + "</strong>");
                sendEmail(desk.getEmail(), subject, body);
            }
        }
    }

    @Async
    public void sendTicketReadyForQa(Ticket ticket) {
        List<User> qaTeam = userRepository.findByRoleAndIsActiveTrue(UserRole.QA_TESTER);
        String subject = "[QA REQUIRED] " + ticket.getTicketNumber() + " - Ready for testing";
        String body = buildEmailBody("Ticket Ready for QA Testing", "Hi QA Team,",
                "A ticket has been resolved by the developer and is ready for your verification.",
                ticket, "Please test and mark as VERIFIED or return to developer.");
        for (User qa : qaTeam) {
            sendEmail(qa.getEmail(), subject, body);
        }
    }

    @Async
    public void sendQaFailedToDeveloper(Ticket ticket) {
        if (ticket.getAssignedTo() == null) return;
        String subject = "[QA FAILED] " + ticket.getTicketNumber() + " - Returned for rework";
        String body = buildEmailBody("QA Verification Failed",
                "Hi " + ticket.getAssignedTo().getFullName() + ",",
                "The QA team has tested your fix and found it did not resolve the issue. Please revisit.",
                ticket, "Status returned to: <strong>IN_PROGRESS</strong>");
        sendEmail(ticket.getAssignedTo().getEmail(), subject, body);
    }

    @Async
    public void sendTicketVerifiedToClient(Ticket ticket) {
        String subject = "[VERIFIED] " + ticket.getTicketNumber() + " - Fix confirmed by QA";
        String body = buildEmailBody("Your Ticket Has Been Verified",
                "Hi " + ticket.getRaisedBy().getFullName() + ",",
                "Great news! The QA team has verified the fix for your ticket. Please confirm if the issue is resolved from your end.",
                ticket, "If you are satisfied, you can close the ticket. If the issue persists, you may reopen it.");
        sendEmail(ticket.getRaisedBy().getEmail(), subject, body);
    }

    @Async
    public void sendWaitingForClientEmail(Ticket ticket) {
        String subject = "[ACTION REQUIRED] " + ticket.getTicketNumber() + " - Your response needed";
        String body = buildEmailBody("Your Input Is Required",
                "Hi " + ticket.getRaisedBy().getFullName() + ",",
                "The team working on your ticket needs additional information from you to proceed.",
                ticket, "Status: <strong>WAITING FOR CLIENT</strong> — please login and reply.");
        sendEmail(ticket.getRaisedBy().getEmail(), subject, body);
    }

    @Async
    public void sendTicketClosedEmail(Ticket ticket) {
        String subject = "[CLOSED] " + ticket.getTicketNumber() + " - Ticket closed";
        String clientBody = buildEmailBody("Ticket Closed",
                "Hi " + ticket.getRaisedBy().getFullName() + ",",
                "Your ticket has been closed. Thank you for using our support system.",
                ticket, "If you face the same issue again, you may reopen the ticket.");
        sendEmail(ticket.getRaisedBy().getEmail(), subject, clientBody);

        List<User> supportDesks = userRepository.findByRoleAndIsActiveTrue(UserRole.SUPPORT_DESK);
        String deskBody = buildEmailBody("Ticket Closed by Client", "Hi Support Team,",
                "The client has confirmed resolution and closed the ticket.",
                ticket, "No further action required.");
        for (User desk : supportDesks) {
            sendEmail(desk.getEmail(), subject, deskBody);
        }
    }

    @Async
    public void sendTicketReopenedEmail(Ticket ticket) {
        List<User> supportDesks = userRepository.findByRoleAndIsActiveTrue(UserRole.SUPPORT_DESK);
        String subject = "[REOPENED] " + ticket.getTicketNumber() + " - Client reopened ticket";
        String deskBody = buildEmailBody("Ticket Reopened", "Hi Support Team,",
                "The client was not satisfied with the resolution and has reopened the ticket.",
                ticket, "Please review and reassign to the appropriate team member.");
        for (User desk : supportDesks) {
            sendEmail(desk.getEmail(), subject, deskBody);
        }

        String clientBody = buildEmailBody("Ticket Reopened Successfully",
                "Hi " + ticket.getRaisedBy().getFullName() + ",",
                "Your ticket has been reopened. Our support team will review and address the issue again.",
                ticket, "Status: <strong>REOPENED</strong>");
        sendEmail(ticket.getRaisedBy().getEmail(),
                "[REOPENED] " + ticket.getTicketNumber() + " - Under review again", clientBody);
    }

    @Async
    public void sendSlaBreachEmail(Ticket ticket) {
        List<User> supportDesks = userRepository.findByRoleAndIsActiveTrue(UserRole.SUPPORT_DESK);
        String subject = "[SLA BREACH] " + ticket.getTicketNumber() + " - Resolution overdue";
        String deskBody = buildEmailBody("SLA Breach Alert", "Hi Support Team,",
                "The following ticket has exceeded its SLA resolution time and requires immediate attention.",
                ticket, "Priority: <strong>" + ticket.getPriority() + "</strong> — Please escalate immediately.");
        for (User desk : supportDesks) {
            sendEmail(desk.getEmail(), subject, deskBody);
        }

        if (ticket.getAssignedTo() != null) {
            String devBody = buildEmailBody("SLA Breach — Urgent Action Required",
                    "Hi " + ticket.getAssignedTo().getFullName() + ",",
                    "The ticket assigned to you has breached its SLA deadline. Please update the status immediately.",
                    ticket, "Priority: <strong>" + ticket.getPriority() + "</strong>");
            sendEmail(ticket.getAssignedTo().getEmail(), subject, devBody);
        }
    }

    private String buildEmailBody(String heading, String greeting,
                                   String message, Ticket ticket,
                                   String footerNote) {
        return """
                <html>
                <body style="font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: auto;">
                  <div style="background-color: #1a1a2e; padding: 20px; border-radius: 8px 8px 0 0;">
                    <h2 style="color: #ffffff; margin: 0;">SupportHub</h2>
                    <p style="color: #a0a0c0; margin: 4px 0 0;">Ticket Management System</p>
                  </div>
                  <div style="background-color: #f9f9f9; padding: 24px; border: 1px solid #e0e0e0;">
                    <h3 style="color: #1a1a2e;">%s</h3>
                    <p>%s</p>
                    <p>%s</p>
                    <table style="width:100%%; border-collapse: collapse; margin-top: 16px;">
                      <tr style="background-color: #1a1a2e; color: white;">
                        <th style="padding: 10px; text-align: left;">Field</th>
                        <th style="padding: 10px; text-align: left;">Details</th>
                      </tr>
                      <tr style="background-color: #ffffff;">
                        <td style="padding: 8px; border: 1px solid #ddd;">Ticket ID</td>
                        <td style="padding: 8px; border: 1px solid #ddd;"><strong>%s</strong></td>
                      </tr>
                      <tr style="background-color: #f2f2f2;">
                        <td style="padding: 8px; border: 1px solid #ddd;">Title</td>
                        <td style="padding: 8px; border: 1px solid #ddd;">%s</td>
                      </tr>
                      <tr style="background-color: #ffffff;">
                        <td style="padding: 8px; border: 1px solid #ddd;">Category</td>
                        <td style="padding: 8px; border: 1px solid #ddd;">%s</td>
                      </tr>
                      <tr style="background-color: #f2f2f2;">
                        <td style="padding: 8px; border: 1px solid #ddd;">Status</td>
                        <td style="padding: 8px; border: 1px solid #ddd;">%s</td>
                      </tr>
                      <tr style="background-color: #ffffff;">
                        <td style="padding: 8px; border: 1px solid #ddd;">Priority</td>
                        <td style="padding: 8px; border: 1px solid #ddd;">%s</td>
                      </tr>
                    </table>
                    <p style="margin-top: 16px;">%s</p>
                  </div>
                  <div style="background-color: #e8e8f0; padding: 12px; border-radius: 0 0 8px 8px;
                              text-align: center; font-size: 12px; color: #666;">
                    This is an automated email from SupportHub. Please do not reply directly.
                  </div>
                </body>
                </html>
                """.formatted(heading, greeting, message,
                ticket.getTicketNumber(), ticket.getTitle(),
                ticket.getCategory(), ticket.getStatus(),
                ticket.getPriority(), footerNote);
    }

    private void sendEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent to {} | Subject: {}", to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to {} | Error: {}", to, e.getMessage());
        }
    }
}