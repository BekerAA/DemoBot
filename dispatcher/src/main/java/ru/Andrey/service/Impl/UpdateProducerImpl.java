package ru.Andrey.service.Impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.Andrey.service.UpdateProducer;


@Service
@Log4j
public class UpdateProducerImpl implements UpdateProducer {
    @Override
    public void produce(String rabbitQueue, Update update) {
        log.debug(update.getMessage().getText());//На данный момент это заглушка

    }
}
