package com.engl_master_bot.bot;

import com.engl_master_bot.model.UserBot;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
@Setter
public abstract class TelegramBot extends TelegramLongPollingBot {
    private String botUserName;
    private String authToken;
    protected final Map<Long, UserBot> sessions = new HashMap<>();

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return authToken;
    }
}
