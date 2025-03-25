package ru.Andrey.Сontroller;
//Используемые библиотеки и файлы
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.Andrey.service.UpdateProducer;
import ru.Andrey.utils.MessageUtils;

import static ru.Andrey.model.RabbitQueue.*;

//Данный класс нужен для распределения сообщений

@Component
@Log4j
public class UpdateController {

    private TelegramBot telegramBot;// Пременная с cсылкой на TelegramBot

    public void registarBot(TelegramBot telegramBot){
        this.telegramBot = telegramBot;
    }//Метод для принятия ссылки на TelegramBot

    private final MessageUtils messageUtils;//Объект который создает сообщения для пользователей

    private final UpdateProducer updateProducer;

    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer) {
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
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
            log.error("Unsupported message type is received " + update);
        }//Процесс проверки на наличие сообщений.
    }
//-----------------------------------------------------------------------

    private void distriduteMessagesByType(Update update) {
        var message = update.getMessage();
        if (message.hasText()){
            processTextMessage(update);
        } else if (message.hasDocument()){
            processDocMessage(update);
        } else if (message.hasPhoto()){
            processPhotoMessage(update);
        } else {
            setUnsupportedMessage(update);
        }
    }//Метод для обработки типа сообщений.
    private void setUnsupportedMessage(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update, "Неподдерживаемый тип сообщений");
        setView(sendMessage);
    }//Метод для обработки сообщей ни поддерживаемого типа данных

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }//метод для отправки сообщений

    private void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
    }//Метод для обработки сообщей с фото

    private void processDocMessage(Update update) {
        updateProducer.produce(DOC_MESSAGE_UPDATE, update);
    }//Метод для обработки сообщей с документами

    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }//Метод для обработки сообщей с текстом























    
}
