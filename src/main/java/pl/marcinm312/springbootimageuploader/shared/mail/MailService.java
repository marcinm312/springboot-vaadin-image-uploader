package pl.marcinm312.springbootimageuploader.shared.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {

	private final JavaMailSender javaMailSender;

	@Value("${mail.from}")
	private String emailFrom;

	public void sendMail(String to, String subject, String text, boolean isHtmlContent) {

		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			log.info("Sending email: to = {}, subject = {}, isHtmlContent = {}", to, subject, isHtmlContent);
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			mimeMessageHelper.setFrom(emailFrom);
			mimeMessageHelper.setTo(to);
			mimeMessageHelper.setSubject(subject);
			mimeMessageHelper.setText(text, isHtmlContent);
			javaMailSender.send(mimeMessage);
			log.info("Email sent: to = {}, subject = {}, isHtmlContent = {}", to, subject, isHtmlContent);
		} catch (MessagingException e) {
			String errorMessage = String.format("An error occurred while sending the email. [MESSAGE]: %s", e.getMessage());
			log.error(errorMessage, e);
			throw new RuntimeMailException(errorMessage);
		}
	}
}
