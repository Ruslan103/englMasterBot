package com.engl_master_bot;

import com.engl_master_bot.bot.EnglishBot;
import com.engl_master_bot.bot.TelegramBot;
import com.engl_master_bot.service.StarterService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashMap;
import java.util.Map;


@SpringBootApplication
public class EnglMasterBotApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(EnglMasterBotApplication.class, args);
        TelegramBot englBot = context.getBean(EnglishBot.class);
       StarterService.start(englBot);

    }

}
