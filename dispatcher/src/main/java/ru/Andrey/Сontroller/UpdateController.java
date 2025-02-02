package ru.Andrey.Сontroller;


import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.Andrey.utils.MessageUtils;

@Component
@Log4j
public class UpdateController {
    private TelegramBot telegramBot;

    private MessageUtils messageUtils;

    public UpdateController(MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }

    public void registarBot(TelegramBot telegramBot){
        this.telegramBot = telegramBot;
    }
//---------------------------------------------------------------------- Процесс проверки обновлений в боте
    public void processUpdate(Update update){
        if (update == null){
            log.error("Received update is null");
            return;
        }//Процесс проверки обнавления
        
        if (update.getMessage() != null){
            distriduteMessagesByType(update);
        } else {
            log.error("Received unsupported message type " + update);
        }
    }//Процесс проверки на наличие сообщений.
//-----------------------------------------------------------------------
    //-----------------------------------------------------------------------Метод для обработки типа сообщений.
    private void distriduteMessagesByType(Update update) {
        var message = update.getMessage();
        if (message.getText() != null){
            processTextMessage(update);
        } else if (message.getDocument() != null){
            processDocMessage(update);
        } else if (message.getPhoto() != null){
            processPhotoMessage(update);
        } else {
            setUnsupportedMessage(update);
        }
    }//-----------------------------------------------------------------
    private void setUnsupportedMessage(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update, "Неподдерживаемый тип сообщений");
        setView(sendMessage);
    }

    private void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    private void processPhotoMessage(Update update) {
    }

    private void processDocMessage(Update update) {
    }

    private void processTextMessage(Update update) {
    }






















    
}
