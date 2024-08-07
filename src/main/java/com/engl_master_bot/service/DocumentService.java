package com.engl_master_bot.service;

import com.engl_master_bot.bot.EnglishBot;
import com.engl_master_bot.model.Word;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Component
@Getter
@Setter
public class DocumentService {
    private static final Logger logger = Logger.getLogger(EnglishBot.class.getName());
    private String fileName;
    private List<Word> words = new ArrayList<>();
    @Getter
    private static final DocumentService instance = new DocumentService();

    private DocumentService() {
    }

    public void readFile() {
        try (InputStream is = DocumentService.class.getClassLoader().getResourceAsStream(fileName);
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Cell wordCell = row.getCell(0);  // Первая колонка
                Cell translationCell = row.getCell(1);  // Вторая колонка

                if (wordCell != null && translationCell != null) {
                    String englishWord = wordCell.getStringCellValue();
                    String translation = translationCell.getStringCellValue();
                    Word word = new Word(englishWord, translation);
                    words.add(word);
                }
            }
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
    }
}
