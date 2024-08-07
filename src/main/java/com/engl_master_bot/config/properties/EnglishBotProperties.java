package com.engl_master_bot.config.properties;

import com.engl_master_bot.config.properties.interfaces.BotProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;


@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnglishBotProperties implements BotProperties {
    private String username = "EnglMaster_bot";
    private String token = "7410982815:AAHH0Vun1wbbQ5KvIwKMFovwYh2wcHpMt8E";
}
