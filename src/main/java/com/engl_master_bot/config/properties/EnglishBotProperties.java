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
    private String username = "";
    private String token = "";
}
