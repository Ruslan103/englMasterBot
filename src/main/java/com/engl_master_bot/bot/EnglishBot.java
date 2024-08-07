package com.engl_master_bot.bot;

import com.engl_master_bot.enums.UserState;
import com.engl_master_bot.model.UserBot;
import com.engl_master_bot.model.Word;
import com.engl_master_bot.service.LearningService;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.logging.Logger;


public class EnglishBot extends TelegramBot {
    private static final Logger logger = Logger.getLogger(EnglishBot.class.getName());
    private String learnButtonName = "Выучить слово";
    @Autowired
    private LearningService learningService;

    Map<Long, UserBot> sessions = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();
        Integer messageId = update.getMessage().getMessageId();
        String userText = update.getMessage().getText();
        UserBot userBot = getUser(update);
        SendMessage botSendMessage = new SendMessage();
        botSendMessage.setChatId(chatId);
        String botText = "Начнем";
        botSendMessage.setText(botText);
        try {
            if (isLearnButton(userText)) {
                Word word = learningService.getNewWordForStudy(userBot);
                botText = word.getEnglishWordAndTranslate();
                userBot.setUserState(UserState.LEANING);
                userBot.setLearningWord(word);
                userBot.getStudiedWords().add(word);
                botSendMessage.setText(botText);
            }
            setButtons(botSendMessage,null);
            userBot.setChatId(chatId);
            userBot.getSentMessageIds().add(messageId);
            sessions.put(chatId,userBot);
            execute(botSendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean isLearnButton(String message) {
        return message.equals(learnButtonName);
    }


    private void deleteAllMessages(UserBot userBot) throws TelegramApiException {
        // Удаление всех сохраненных сообщений
        for (Integer messageId : userBot.getSentMessageIds()) {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(userBot.getChatId());
            deleteMessage.setMessageId(messageId);
            execute(deleteMessage);
        }
        // Очистка списка ID сообщений после удаления
        userBot.getSentMessageIds().clear();
    }

    private UserBot getUser(Update update) {
        Long chatId = update.getMessage().getChatId();
        UserBot userBot = sessions.containsKey(chatId)
                ? sessions.get(chatId)
                : new UserBot();
        User user = update.getMessage().getFrom();

        List<Integer> sentMessageIds = userBot.getSentMessageIds();
        sentMessageIds.add(update.getMessage().getMessageId());
        userBot.setSentMessageIds(sentMessageIds);
        userBot.setUser(user);
        return userBot;
    }


    public synchronized void setButtons(SendMessage sendMessage, String userText) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(); // Создаем клавиатуру
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>(); // Создаем список строк клавиатуры

        KeyboardRow keyboardFirstRow = new KeyboardRow(); // Первая строчка клавиатуры
        KeyboardButton learnButton = new KeyboardButton(userText != null ? learningService.getTranslate(userText) : "Выучить слово");
        keyboardFirstRow.add(learnButton); // Добавляем кнопки в первую строчку клавиатуры

        KeyboardRow keyboardSecondRow = new KeyboardRow(); // Вторая строчка клавиатуры
        keyboardSecondRow.add(new KeyboardButton(userText != null ? learningService.getRandom() : "Проверить себя")); // Добавляем кнопки во вторую строчку клавиатуры

        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);

        if (userText != null) {
            KeyboardRow keyboardThirdRow = new KeyboardRow(); // Третья строчка клавиатуры
            keyboardThirdRow.add(new KeyboardButton(learningService.getRandom())); // Добавляем третью кнопку
            keyboard.add(keyboardThirdRow);
            Collections.shuffle(keyboard); // Перемешиваем строки клавиатуры
        }

        replyKeyboardMarkup.setKeyboard(keyboard); // и устанавливаем этот список нашей клавиатуре
    }


    private void putSession(Long chatId) {
        logger.info("Put the new session: chatId = " + chatId);
        logger.info("Number of sessions = " + sessions.size() + 1);
        sessions.put(chatId, new UserBot());
    }
}

