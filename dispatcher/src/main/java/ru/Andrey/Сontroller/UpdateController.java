package ru.Andrey.Сontroller;


import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

@Component
@Log4j
public class UpdateController {
    private TelegramBot telegramBot;

    public void registarBot(TelegramBot telegramBot){
        this.telegramBot = telegramBot;


    }
}
