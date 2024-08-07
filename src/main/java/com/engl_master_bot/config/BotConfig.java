package com.engl_master_bot.config;


import com.engl_master_bot.bot.EnglishBot;
import com.engl_master_bot.bot.TelegramBot;
import com.engl_master_bot.config.properties.EnglishBotProperties;
import com.engl_master_bot.config.properties.interfaces.BotProperties;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class BotConfig {

    private BotProperties botProperties;

    @Bean
    public TelegramBot englishBot() {
        botProperties = new EnglishBotProperties();
        TelegramBot naukaBot = new EnglishBot();
        naukaBot.setBotUserName(botProperties.getUsername());
        naukaBot.setAuthToken(botProperties.getToken());
        return naukaBot;
    }
}
