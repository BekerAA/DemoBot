package ru.Andrey.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ProducerService {
    void produserAnswer(SendMessage sendMessage);
}
