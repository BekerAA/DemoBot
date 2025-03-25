package ru.Andrey.service.Impl;

import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.Andrey.service.ConsumerService;
import ru.Andrey.service.MainService;
import ru.Andrey.service.ProducerService;

import static ru.Andrey.model.RabbitQueue.*;

@Service
@Log4j
public class ConsumerServiceImpl implements ConsumerService {

    private final MainService mainService;

    public ConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumerTextMessageUpdate(Update update) {
        log.debug("NODE: Text message is received");
        mainService.processTextMessage(update);
    }

    @Override
    @RabbitListener(queues = DOC_MESSAGE_UPDATE)
    public void consumerDocMessageUpdate(Update update) {
        log.debug("NODE: Dock message is received");
        mainService.processDocMessage(update);
    }

    @Override
    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
    public void consumerPhotoMessageUpdate(Update update) {
        log.debug("NODE: Photo message is received");
        mainService.processPhotoMessage(update);
    }
}
