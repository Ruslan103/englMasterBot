package com.engl_master_bot.service;

import com.engl_master_bot.model.UserBot;
import com.engl_master_bot.model.Word;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class LearningService {

    private final List<Word> wordsForLearning;

    public LearningService() {
        DocumentService documentService = DocumentService.getInstance();
        this.wordsForLearning = documentService.getWords();
    }

    public Word getNewWordForStudy(UserBot userBot) {
        Set<Word> alreadyStudiedWords = userBot.getStudiedWords();
        for (Word word : wordsForLearning) {
            if (!alreadyStudiedWords.contains(word)) {
                String englishWordAndTranslate = "*******    "
                        + word.getEnglishWord()
                        + " - "
                        + word.getTranslate().toUpperCase()
                        + "    *******";
                word.setEnglishWordAndTranslate(englishWordAndTranslate);
                return word;
            }
        }
        return wordsForLearning.get(0);
    }

    public String getRandom() {
        return wordsForLearning.get(getRandomNumber(0, wordsForLearning.size()) - 1).getTranslate();
    }

    public String getTranslate(String word) {
        for (Word s : wordsForLearning) {
            if (s.getEnglishWord().equals(word)) {
                return s.getTranslate();
            }
        }
        return "Упсс...";
    }

    public static int getRandomNumber(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min должен быть меньше или равен max");
        }
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }
}
