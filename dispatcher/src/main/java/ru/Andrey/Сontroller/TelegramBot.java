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

    @Value("${bot.token}")
    private String botToken;
// ---------------------------------------------------------Часть кода, связующий в оба на правления Резистор обновления и телеграмм Бот
    private UpdateController updateController;

    public TelegramBot(UpdateController updateController) {
        this.updateController = updateController;
    }

    @PostConstruct
    public void init(){
        updateController.registarBot(this);
    }
// --------------------------------------------------------


    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        var originMessage = update.getMessage();
        log.debug(originMessage.getText());

        var response = new SendMessage();
        response.setChatId(originMessage.getChatId().toString());
        response.setText("Привет пользователь, я пока что нахожусь в тестовом режиме");
        sendAnswerMessage(response);
    }

    public void sendAnswerMessage(SendMessage message){
        if (message != null){
            try {
                execute(message);
            } catch (TelegramApiException e){
                log.error(e);
            }
        }
    }
}
