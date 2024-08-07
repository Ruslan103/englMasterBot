package com.engl_master_bot.service;

import com.engl_master_bot.bot.TelegramBot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class StarterService {
    public static void start(TelegramBot telegramBot) {
        try {
           DocumentService documentService = DocumentService.getInstance();
           documentService.setFileName("wordsEng.xlsx");
           documentService.readFile();
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
