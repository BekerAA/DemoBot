package ru.Andrey.service.Impl;

import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.Andrey.AppDocument;
import ru.Andrey.BinaryContent;
import ru.Andrey.dao.AppDocumentDAO;
import ru.Andrey.dao.BinaryContentDAO;
import ru.Andrey.exception.UploadFileException;
import ru.Andrey.service.FileService;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Log4j
@Service
public class FileServiceImpl implements FileService {
   @Value("${token}")
   private String token;

   @Value("${service.file_info.uri}")
   private String fileInfoUri;

   @Value("${service.file_storage.uri}")
   private String fileStorageUri;

   private final AppDocumentDAO appDocumentDAO;

   private final BinaryContentDAO binaryContentDAO;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, BinaryContentDAO binaryContentDAO) {
        this.appDocumentDAO = appDocumentDAO;
        this.binaryContentDAO = binaryContentDAO;
    }


    @Override
    public AppDocument processDoc(Message telegramMessage) {
        String fileId = telegramMessage.getDocument().getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK){
            JSONObject jsonObject = new JSONObject(response.getBody());
            String filePath = String.valueOf(jsonObject
                    .getJSONObject("result")
                    .getString("file_path"));
            byte[] fileInByte = downloadFile(filePath);
            BinaryContent transientBinaryContent = BinaryContent.builder()
                    .fileAsArrayOfBytes(fileInByte)
                    .build();
            BinaryContent persistentBinaryContent = binaryContentDAO.save(transientBinaryContent);
            Document telegramDoc = telegramMessage.getDocument();
            AppDocument transientAppDoc = buidTransientAppDoc(telegramDoc, persistentBinaryContent);
            return appDocumentDAO.save(transientAppDoc);
        }else {
            throw  new UploadFileException("Bad response from telegram server "+ response);
        }
    }

    private AppDocument buidTransientAppDoc(Document telegarmDoc, BinaryContent persistentBinaryContent){
        return AppDocument.builder()
                .telegramFileId(telegarmDoc.getFileId())
                .docName(telegarmDoc.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimeType(telegarmDoc.getMimeType())
                .fileSize(telegarmDoc.getFileSize())
                .build();
    }//Вытаскивает значения документа из телеграмовского объекта в наш объект

    private ResponseEntity<String> getFilePath(String fileId){
        RestTemplate restTemplate = new RestTemplate();//Http запрос в телеграм
        HttpHeaders headers = new HttpHeaders();//Создает заголовок запроса.
        HttpEntity<String> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token, fileId
        );
    }

    private byte[] downloadFile(String filePath){
        String fullUri = fileStorageUri.replace("{token}", token)
                .replace("{filePath}", filePath);
        URL urlObj = null;
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e){
            throw new UploadFileException(e);
        }
        try (InputStream is = urlObj.openStream()){
            return is.readAllBytes();
        } catch (IOException e){
            throw  new UploadFileException(urlObj.toExternalForm(), e);
        }

    }
}

