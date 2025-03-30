package ru.Andrey.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.Andrey.AppDocument;
import ru.Andrey.AppPhoto;


public interface FileService {
    AppDocument processDoc(Message telegramMessage);

    AppPhoto processPhoto(Message telegramMessage);
}
