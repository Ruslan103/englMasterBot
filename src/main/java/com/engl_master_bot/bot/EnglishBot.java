package com.engl_master_bot.bot;

import com.engl_master_bot.enums.UserState;
import com.engl_master_bot.model.UserBot;
import com.engl_master_bot.model.Word;
import com.engl_master_bot.service.LearningService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.logging.Logger;


public class EnglishBot extends TelegramBot {
    private static final Logger logger = Logger.getLogger(EnglishBot.class.getName());

    private final String testingButtonName = "Проверить себя";
    @Autowired
    private LearningService learningService;

    Map<Long, UserBot> sessions = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = extractChatId(update);
        Integer messageId = extractMessageId(update);
        String userText = extractUserText(update);
        UserBot userBot = getUser(update);
        SendMessage botSendMessage = new SendMessage();
        botSendMessage.setChatId(chatId);
        String botText = "Начнем";

        try {
            if (isUserStateTest(userBot)) {
                botText = handleTesting(userBot, userText);
            }
            if (isTestingButton(userText)) {
                botText = handleTestingButton(userBot);
            }
            botSendMessage.setText(botText);
            userBot.setChatId(chatId);
            userBot.getSentMessageIds().add(messageId);
            setButtons(botSendMessage, userBot);
            putSession(chatId, userBot);
            execute(botSendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private Long extractChatId(Update update) {
        return update.hasCallbackQuery()
                ? update.getCallbackQuery().getMessage().getChatId()
                : update.getMessage().getChatId();
    }

    private Integer extractMessageId(Update update) {
        return update.hasCallbackQuery()
                ? update.getCallbackQuery().getMessage().getMessageId()
                : update.getMessage().getMessageId();
    }

    private String extractUserText(Update update) {
        return update.hasCallbackQuery()
                ? update.getCallbackQuery().getData()
                : update.getMessage().getText();
    }

    private boolean isUserStateTest(UserBot userBot) {
        return userBot.getUserState() != null && userBot.getUserState().equals(UserState.TESTING);
    }

    private String handleTesting(UserBot userBot, String userText) {
        Word learningWord = userBot.getLearningWord();
        Word newLearningWord = learningService.getWordForTesting();
        String testingText = learningWord.getIsEnglishWord()
                ? learningWord.getTranslate()
                : learningWord.getEnglishWord();
        String response = getResponse(userText, newLearningWord, testingText, learningWord);
        userBot.setLearningWord(newLearningWord);
        return response;
    }

    private String handleTestingButton(UserBot userBot) {
        Word word = learningService.getWordForTesting();
        userBot.setUserState(UserState.TESTING);
        userBot.setLearningWord(word);
        return word.getIsEnglishWord()
                ? word.getEnglishWord()
                : word.getTranslate();
    }

    @NotNull
    private static String getResponse(String userText, Word newLearningWord, String testingText, Word learningWord) {
        String newTestingText = newLearningWord.getIsEnglishWord()
                ? newLearningWord.getEnglishWord()
                : newLearningWord.getTranslate();
        String rightWord = learningWord.getIsEnglishWord()
                ? learningWord.getEnglishWord()
                : learningWord.getTranslate();

        String response;
        if (testingText.equals(userText)) {
            response = "Верно!\nСледующее слово: \n\n - "
                    + newTestingText;
        } else {
            response = "Не-а! " + rightWord + " - "
                    + testingText
                    + " \nСледующее слово: \n- "
                    + newTestingText;
        }
        return response;
    }

    private boolean isTestingButton(String message) {
        return message.equals(testingButtonName);
    }

    private UserBot getUser(Update update) {
        Long chatId = update.hasCallbackQuery()
                ? update.getCallbackQuery().getMessage().getChatId()
                : update.getMessage().getChatId();
        UserBot userBot = sessions.containsKey(chatId)
                ? sessions.get(chatId)
                : new UserBot();
        User user = update.hasCallbackQuery()
                ? update.getCallbackQuery().getFrom()
                : update.getMessage().getFrom();

        Integer messageId = update.hasCallbackQuery()
                ? update.getCallbackQuery().getMessage().getMessageId()
                : update.getMessage().getMessageId();

        List<Integer> sentMessageIds = userBot.getSentMessageIds();
        sentMessageIds.add(messageId);
        userBot.setSentMessageIds(sentMessageIds);
        userBot.setUser(user);
        return userBot;
    }


    public synchronized void setButtons(SendMessage sendMessage, UserBot userBot) {
        if (userBot.getUserState() != null && userBot.getUserState().equals(UserState.TESTING)) {
            setTestingButtons(sendMessage, userBot);
        } else {
            setLearnButtons(sendMessage);
        }
    }

    public synchronized void setLearnButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(); // Создаем клавиатуру
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>(); // Создаем список строк клавиатуры

        KeyboardRow keyboardSecondRow = new KeyboardRow(); // строчка клавиатуры
        keyboardSecondRow.add(new KeyboardButton(testingButtonName)); // Добавляем кнопки в строчку клавиатуры

        keyboard.add(keyboardSecondRow);
        replyKeyboardMarkup.setKeyboard(keyboard); // и устанавливаем этот список нашей клавиатуре
    }

    public synchronized void setTestingButtons(SendMessage sendMessage, UserBot userBot) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(); // Создаем инлайн клавиатуру
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        Word learningWord = userBot.getLearningWord();

        String textLearnButton = userBot.getLearningWord().getIsEnglishWord()
                ? learningWord.getTranslate()
                : learningWord.getEnglishWord();

        String textRandomButton1 = learningService.getWordForTestingButton(userBot);
        String textRandomButton2 = learningService.getWordForTestingButton(userBot);
        String textRandomButton3 = learningService.getWordForTestingButton(userBot);
        String textRandomButton4 = learningService.getWordForTestingButton(userBot);
        String textRandomButton5 = learningService.getWordForTestingButton(userBot);
        String textRandomButton6 = learningService.getWordForTestingButton(userBot);
        String textRandomButton7 = learningService.getWordForTestingButton(userBot);

        List<InlineKeyboardButton> inlineKeyboardFirstRow = new ArrayList<>(); // Первая строчка инлайн клавиатуры
        inlineKeyboardFirstRow.add(createButton(textLearnButton)); // Добавляем кнопку в первую строчку инлайн клавиатуры

        List<InlineKeyboardButton> inlineKeyboardSecondRow = new ArrayList<>(); // Вторая строчка инлайн клавиатуры
        inlineKeyboardSecondRow.add(createButton(textRandomButton1)); // Добавляем кнопку во вторую строчку инлайн клавиатуры

        List<InlineKeyboardButton> inlineKeyboardThirdRow = new ArrayList<>(); // Третья строчка инлайн клавиатуры
        inlineKeyboardThirdRow.add(createButton(textRandomButton2)); // Добавляем кнопку в третью строчку инлайн клавиатуры

        List<InlineKeyboardButton> inlineKeyboardFourthRow = new ArrayList<>(); // Четвертая строчка инлайн клавиатуры
        inlineKeyboardFourthRow.add(createButton(textRandomButton3)); // Добавляем кнопку в четвертую строчку инлайн клавиатуры

        List<InlineKeyboardButton> inlineKeyboardFifthRow = new ArrayList<>(); // Пятая строчка инлайн клавиатуры
        inlineKeyboardFifthRow.add(createButton(textRandomButton4)); // Добавляем кнопку в пятую строчку инлайн клавиатуры

        List<InlineKeyboardButton> inlineKeyboardSixthRow = new ArrayList<>(); // Шестая строчка инлайн клавиатуры
        inlineKeyboardSixthRow.add(createButton(textRandomButton5)); // Добавляем кнопку в шестую строчку инлайн клавиатуры

        List<InlineKeyboardButton> inlineKeyboardSeventhRow = new ArrayList<>(); // Седьмая строчка инлайн клавиатуры
        inlineKeyboardSeventhRow.add(createButton(textRandomButton6)); // Добавляем кнопку в седьмую строчку инлайн клавиатуры

        List<InlineKeyboardButton> inlineKeyboardEighthRow = new ArrayList<>(); // Восьмая строчка инлайн клавиатуры
        inlineKeyboardEighthRow.add(createButton(textRandomButton7)); // Добавляем кнопку в восьмую строчку инлайн клавиатуры

        // Случайным образом перемешиваем строки клавиатуры
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        keyboardRows.add(inlineKeyboardFirstRow);
        keyboardRows.add(inlineKeyboardSecondRow);
        keyboardRows.add(inlineKeyboardThirdRow);
        keyboardRows.add(inlineKeyboardFourthRow);
        keyboardRows.add(inlineKeyboardFifthRow);
        keyboardRows.add(inlineKeyboardSixthRow);
        keyboardRows.add(inlineKeyboardSeventhRow);
        keyboardRows.add(inlineKeyboardEighthRow);
        Collections.shuffle(keyboardRows);

        inlineKeyboardMarkup.setKeyboard(keyboardRows); // Устанавливаем список строк клавиатуры
    }

    private InlineKeyboardButton createButton(String text) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text); // Устанавливаем текст кнопки
        button.setCallbackData(text); // Устанавливаем данные для коллбэка
        return button;
    }

    private void putSession(Long chatId, UserBot userBot) {
        sessions.put(chatId, userBot);
        logger.info("Put the new session: \nchatId: " + chatId);
        logger.info(userBot.getUser().toString());
        logger.info("Number of sessions = " + sessions.size());
    }
}

