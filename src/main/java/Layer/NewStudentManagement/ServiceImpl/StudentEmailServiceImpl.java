package Layer.NewStudentManagement.ServiceImpl;


import Layer.NewStudentManagement.DTO.EmailRequestDTO;
import Layer.NewStudentManagement.Entity.StudentEmail;
import Layer.NewStudentManagement.Entity.StudentEntity;
import Layer.NewStudentManagement.Repository.StudentEmailRepository;
import Layer.NewStudentManagement.Repository.StudentRepository;
import Layer.NewStudentManagement.Service.StudentEmailService;
import com.google.gson.Gson;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StudentEmailServiceImpl implements StudentEmailService {

    private final JavaMailSender mailSender;
    private final StudentRepository studentRepository;
    private final StudentEmailRepository studentEmailRepository;

    private final StaffService staffService;

    private final TaskScheduler taskScheduler;





    @Override
    public String sendEmailToStudents(String role, String email, EmailRequestDTO dto) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to send email");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        List<Long> studentIds = dto.getStudentIds();

        List<StudentEntity> students = studentRepository.findAllById(studentIds);
        List<String> recipients = students.stream()
                .map(StudentEntity::getEmail)
                .filter(Objects::nonNull)
                .toList();

        StudentEmail record = new StudentEmail();
        record.setSubject(dto.getSubject());
        record.setBody(dto.getBody());
        record.setSentByEmail(dto.getSentByEmail());
        record.setCreatedByEmail(email);
        record.setRole(role);
        record.setBranchCode(branchCode);
        record.setStudentIds(new Gson().toJson(studentIds));
        record.setScheduledAt(dto.getScheduledAt());
        record.setSent(false); // Mark not sent if scheduled

        studentEmailRepository.save(record);

        try {
            scheduleOrSend(recipients, dto, branchCode, record);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process email scheduling/sending", e);
        }

        return dto.getScheduledAt() == null
                ? "Emails sent successfully."
                : "Email scheduled for " + dto.getScheduledAt();
    }

    @Override
    public List<StudentEmail> getAllSentEmails(String role, String email) {
        if (!staffService.hasPermission(role, email, "Post")) {
            throw new RuntimeException("You don't have permission to Get Email");
        }

        String branchCode = staffService.fetchBranchCodeByRole(role, email);
        return studentEmailRepository.getAllEmailByBranchCode(branchCode);
    }

    private void scheduleOrSend(List<String> recipients, EmailRequestDTO request, String branchCode, StudentEmail record) throws Exception {
        if (request.getScheduledAt() != null) {
            LocalDateTime scheduledDateTime = request.getScheduledAt();

            if (scheduledDateTime.isAfter(LocalDateTime.now())) {
                taskScheduler.schedule(() -> {
                    try {
                        sendEmail(recipients, request.getSubject(), request.getBody());

                        record.setSent(true);
                        record.setSentAt(LocalDateTime.now());
                        studentEmailRepository.save(record);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, Date.from(scheduledDateTime.atZone(ZoneId.systemDefault()).toInstant()));
            } else {
                throw new IllegalArgumentException("Scheduled time must be in the future.");
            }
        } else {
            sendEmail(recipients, request.getSubject(), request.getBody());

            record.setSent(true);
            record.setSentAt(LocalDateTime.now());
            studentEmailRepository.save(record);
        }
    }

    private void sendEmail(List<String> recipients, String subject, String body) {
        for (String to : recipients) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(body, true); // true = HTML content

                mailSender.send(message);
            } catch (MessagingException e) {
                throw new RuntimeException("Failed to send email to: " + to, e);
            }
        }
    }

}