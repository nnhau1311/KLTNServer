package com.example.childrenhabitsserver.service;

import com.example.childrenhabitsserver.base.BaseObjectLoggable;
import com.example.childrenhabitsserver.config.FreeMakerConfig;
import com.example.childrenhabitsserver.model.NotificationModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.URLDataSource;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

@Setter
@Service
//@Component
@Slf4j
public class SendEmailNotificationService extends BaseObjectLoggable {
    private final JavaMailSender javaMailSender;
    private final Configuration freemakerConfig;
    //    private final FreeMakerConfig freemakerConfig;
    private final ApplicationContext context;
    //    @Value("${libra.assets.api:http://assets-service}")
    private String assetsUrl;
    private String fromName = "ChildrenHabits";
    private String fromMail = "thaianhhao322@gmail.com";

    public SendEmailNotificationService(JavaMailSender javaMailSender,
                                        Configuration freemakerConfig,
//                                        FreeMakerConfig freemakerConfig,
                                        ApplicationContext context) {
        this.javaMailSender = javaMailSender;
        this.freemakerConfig = freemakerConfig;
        this.context = context;
    }


    public void sendEmail(NotificationModel model) {
        String[] cc = model.getCc() == null ? new String[]{""} : model.getCc();
        logger.info(">>>>>>>>>>>>>>>>>>start sendmail to: " + model.getTo());
        try {
            String htmlContent;
            if (model.getExternalTemplate() != null && model.getExternalTemplate()) {
                htmlContent = model.getBody();
            } else {
                Resource resource = context.getResource("classpath:templates/mail/" + model.getTemplate() + ".ftlh");
                if (!resource.exists()) {
                    logger.error(">>>>>>>>>>>>>>>>>>template " + model.getTemplate() + " not exist");
                    return;
                }

                Writer output = new StringWriter();
                Template template = new Template(model.getTemplate(), new InputStreamReader(resource.getInputStream()), freemakerConfig);
                template.process(model.getScopes(), output);
                htmlContent = output.toString();
            }
            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setTo(model.getTo());
            if (model.getCc() != null) {
                helper.setCc(cc);
            }
            helper.setSubject(String.format("%s - %s", model.getSubject(), ""));
            helper.setFrom(fromMail, fromName);
            Multipart multipart = new MimeMultipart();
//            if (model.getAttachments() != null) {
//                for (AttachmentModel attachment : model.getAttachments()) {
//                    MimeBodyPart messageBodyPart = new MimeBodyPart();
//                    String url = assetsUrl + "/internal/assets/download/" + attachment.getId();
//                    messageBodyPart.setDataHandler(new DataHandler(new URLDataSource(new URL(url))));
//                    messageBodyPart.setFileName(attachment.getName());
//                    multipart.addBodyPart(messageBodyPart);
//                }
//            }

            MimeBodyPart htmlBodyPart = new MimeBodyPart();
            htmlBodyPart.setContent(htmlContent, "text/html; charset=UTF-8"); //5
            multipart.addBodyPart(htmlBodyPart);

            message.setContent(multipart);
            logger.info(">>>>>>>>>>>>>>>>>>before send to: " + model.getTo());
            javaMailSender.send(message);
            logger.info(">>>>>>>>>>>>>>>>>>sendmail success to: " + model.getTo() + " --- " + htmlContent);
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>>>>>>>> loi gui mail ", e);
        }
    }
}
