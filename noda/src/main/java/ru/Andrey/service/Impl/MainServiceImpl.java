package ru.Andrey.service.Impl;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.Andrey.AppUser;
import ru.Andrey.dao.AppUserDAO;
import ru.Andrey.dao.RawDataDAO;
import ru.Andrey.entity.RawData;
import ru.Andrey.service.MainService;
import ru.Andrey.service.ProducerService;

import static ru.Andrey.entity.UserState.BASIC_STATE;
import static ru.Andrey.entity.UserState.WAIT_FOR_EMAIL_STATE;
import static ru.Andrey.service.emums.ServiceComands.*;


@Slf4j
@Service
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;


    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";

        if(CANCEL.equals(text)){
            output = cancelProcess(appUser);
        }else if(BASIC_STATE.equals(userState)){
            output = producerServiceCommand(appUser, text);
        }else if (WAIT_FOR_EMAIL_STATE.equals(userState)){
            //TODO доделать обработку email
        } else {
            log.error("Unknown user state:" + userState);
            output = "Неизвестная ошибка! Введите /cancel и попробуйте снова";
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);

    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)){
            return;
        }
        //TODO добавить сохранение документов
        var answer = "Документ успешно загружен! Ссылка для скачивания: http://test.ru/get-photo/777";
        sendAnswer(answer, chatId);
    }


    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)){
            return;
        }
        //TODO добавить сохранение документов
        var answer = "Фото успешно загружено! Ссылка для скачивания: http://test.ru/get-photo/777";
        sendAnswer(answer, chatId);
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getState();
        if (!appUser.getIsActive()){
            var error = "Зарегестируйтесь или активируете свое учетную запись для загрузки контента";
            sendAnswer(error, chatId);
            return true;
        }
        else if (!BASIC_STATE.equals(userState)){
            var error = "Отмените текущию команду с помощью /cansel для отправки файлов.";
            sendAnswer(error, chatId);
            return true;
        }
        return false;
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produserAnswer(sendMessage);
    }

    private String producerServiceCommand(AppUser appUser, String cmd) {
        if (REGISTRATION.equals(cmd)){
            //TODO добавит регистрацию
            return "Временно не доступана команда";
        }else if (HELP.equals(cmd)){
            return help();
        }else if (START.equals(cmd)){
            return "Привет, Чтобы посмотреть список доступных команд введите /help.";
        }else {
            return "Неизвестная команда , Чтобы посмотреть список доступных команд введите /help.";
        }
    }

    private String help() {
        return "Список доступных команд: \n"
                + "/cancel - отмена выполнения текущей команды. \n"
                + "/registration - регистрация пользователя. ";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Команда отменена!";
    }

    private AppUser findOrSaveAppUser(Update update){
        User telegramUser = update.getMessage().getFrom();
        AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
        if (persistentAppUser == null){
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    //TODO изменить значение по умолчанию после добавления регистрации
                    .isActive(true)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }
        return persistentAppUser;
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);


    }
}
