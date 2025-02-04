package ru.Andrey.Сontroller;


import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import javax.annotation.PostConstruct;


@Component
@Log4j
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;
    //адресс бота, для его обнаружения в ТГ

    @Override
    public String getBotUsername() {
        return botName;
    }//Метод, который выводит адресс бота

    @Value("${bot.token}")
    private String botToken;
    //Бот токен

    @Override
    public String getBotToken() {
        return botToken;
    }//Метод, который выводит токен бота

    private UpdateController updateController;
    //Создается объект класса UpdateController

    public TelegramBot(UpdateController updateController) {
        this.updateController = updateController;
    }// Метод для принятия ссылки на объект классом UpdateControlle

    @PostConstruct
    public void init(){
        updateController.registarBot(this);
    }//Метод, который передает ссылку на объект класса TelegaramBot в объект класса UpdateController

    @Override
    public void onUpdateReceived(Update update) {
        updateController.processUpdate(update);
    }//Данный метод нужен для логирования поступихших обновлений и подача ответов пользователям.

    public void sendAnswerMessage(SendMessage message){
        if (message != null){
            try {
                execute(message);
            } catch (TelegramApiException e){
                log.error(e);
            }
        }
    }//Метод для обработки полученных сообщений и ошибок.
}
