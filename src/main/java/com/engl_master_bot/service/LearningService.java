package com.engl_master_bot.service;

import com.engl_master_bot.model.UserBot;
import com.engl_master_bot.model.Word;
import org.springframework.stereotype.Service;

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
        int randomIndex = getRandomNumber(1, wordsForLearning.size()-1);
        Word word = !alreadyStudiedWords.contains(wordsForLearning.get(randomIndex))
                ? wordsForLearning.get(randomIndex)
                : wordsForLearning.get(1);
        String englishWordAndTranslate = "*******    "
                + word.getEnglishWord()
                + " - "
                + word.getTranslate().toUpperCase()
                + "    *******";
        word.setEnglishWordAndTranslate(englishWordAndTranslate);
        return word;
    }

    public Word getWordForTesting(UserBot userBot) {
        Set<Word> alreadyStudiedWords = userBot.getStudiedWords();
        int randomIndex = getRandomNumber(0, alreadyStudiedWords.size() - 1);
        Word word = (Word) alreadyStudiedWords.toArray()[randomIndex];
        word.setIsEnglishWord(getRandomNumber(0, 1) == 1);
        return word;
    }

    public Word getRandom() {
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
        int randomIndex = getRandomNumber(0, wordsForLearning.size()-1);
        Word word = wordsForLearning.get(randomIndex);
        String wordForTestingButton = userBot.getLearningWord().getIsEnglishWord()
                ? word.getTranslate()
                : word.getEnglishWord();
        return wordForTestingButton;
    }
}
