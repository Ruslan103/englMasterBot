package com.engl_master_bot.service;

import com.engl_master_bot.model.UserBot;
import com.engl_master_bot.model.Word;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@Setter
public class LearningService {

    private final List<Word> wordsForLearning;

    public LearningService() {
        DocumentService documentService = DocumentService.getInstance();
        this.wordsForLearning = documentService.getWords();
    }

    public Word getWordForTesting() {
        Word word = getRandomWord();
        word.setIsEnglishWord(getRandomNumber(0, 1) == 1);
        return word;
    }

    private Word getRandomWord() {
        return wordsForLearning.get(getRandomNumber(0, wordsForLearning.size()) - 1);
    }

    public static int getRandomNumber(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min должен быть меньше или равен max");
        }
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    public String getWordForTestingButton(UserBot userBot) {
        int randomIndex = getRandomNumber(0, wordsForLearning.size() - 1);
        Word word = wordsForLearning.get(randomIndex);
        String wordForTestingButton = userBot.getLearningWord().getIsEnglishWord()
                ? word.getTranslate()
                : word.getEnglishWord();
        return wordForTestingButton;
    }
}
