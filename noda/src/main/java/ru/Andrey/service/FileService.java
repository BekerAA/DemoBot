package ru.Andrey.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.Andrey.AppDocument;
import ru.Andrey.AppPhoto;
import ru.Andrey.service.emums.LinkType;


public interface FileService {
    AppDocument processDoc(Message telegramMessage);

    AppPhoto processPhoto(Message telegramMessage);

    String generateLink(Long docId, LinkType linkType);
}
