package com.engl_master_bot.model;

import com.engl_master_bot.enums.UserState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserBot {
    private Long chatId;
    private User user;
    private UserState userState;
    private Word learningWord;
    private Message message;
    private Set<Word> studiedWords = new HashSet<>();
    private List<Integer> sentMessageIds = new ArrayList<>();
}
