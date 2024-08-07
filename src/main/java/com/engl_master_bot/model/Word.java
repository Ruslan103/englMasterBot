package com.engl_master_bot.model;

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
public class Word {
    private String englishWord;
    private String translate;
    private String association;
    private String englishWordAndTranslate;

    public Word(String englishWord, String translate) {
        this.englishWord = englishWord;
        this.translate = translate;
    }
}
