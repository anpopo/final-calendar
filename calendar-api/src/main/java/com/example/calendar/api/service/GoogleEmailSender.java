package com.example.calendar.api.service;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import com.example.calendar.api.dto.EngagementMailDto;
import com.example.calendar.api.dto.SendMailBatchReq;
import com.example.calendar.core.domain.RequestReplyType;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Profile("!test")
@RequiredArgsConstructor
@Component
public class GoogleEmailSender implements EmailSender {

    @Value("${host.url}")
    private String host;

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Async("calendar-api-mail-sender")
    @TransactionalEventListener(
        phase = AFTER_COMMIT,
        classes = EngagementMailDto.class
    )
    @Override
    public void sendEngagement(EngagementMailDto engagementMailDto) {
        Map<String, Object> properties = engagementMailDto.getProperties();
        final String urlFormat = String.format("%s/events/engagements/%d?type=", host, engagementMailDto.getEngagementId());
        properties.put("acceptUrl", urlFormat + RequestReplyType.ACCEPT.name());
        properties.put("rejectUrl", urlFormat + RequestReplyType.REJECT.name());

        javaMailSender.send(mimeMessage -> {
            final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setFrom(engagementMailDto.getFrom());
            helper.setTo(engagementMailDto.getTo());
            helper.setSubject(engagementMailDto.getSubject());
            helper.setText(
                templateEngine.process(
                    "engagement-email",
                    new Context(
                        Locale.KOREAN,
                        properties
                    )
                ), true);
        });
    }

    @Override
    public void sendAlarmMail(SendMailBatchReq req) {

    }
}
