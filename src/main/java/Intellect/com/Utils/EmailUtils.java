package Intellect.com.Utils;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;

@Component
public class EmailUtils {

	private JavaMailSender mailSernder;
	
	public boolean sendEmail(String to,String subject,String body) {
		
		//logic 
		boolean isSend=false;
		try {
			
			MimeMessage mimeMessage = mailSernder.createMimeMessage();
			MimeMessageHelper helper=new MimeMessageHelper(mimeMessage);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body, true);
			mailSernder.send(mimeMessage);
			isSend=true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return isSend;
	}
}
