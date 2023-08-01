package ru.fkjob.delivery.web.dto.mail;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WelcomeDTO {
    private String subject;
    @Value("${message.text}")
    private String text;
}
