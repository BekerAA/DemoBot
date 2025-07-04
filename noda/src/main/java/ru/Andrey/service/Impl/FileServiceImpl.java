package ru.Andrey.service.Impl;

import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import ru.Andrey.AppDocument;
import ru.Andrey.AppPhoto;
import ru.Andrey.BinaryContent;
import ru.Andrey.dao.AppDocumentDAO;
import ru.Andrey.dao.AppPhotoDAO;
import ru.Andrey.dao.BinaryContentDAO;
import ru.Andrey.exception.UploadFileException;
import ru.Andrey.service.FileService;
import ru.Andrey.service.emums.LinkType;
import ru.Andrey.utils.CryptoTool;

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

   @Value("${link.address}")
   private String linkAddress;

   private final AppDocumentDAO appDocumentDAO;

   private final AppPhotoDAO appPhotoDAO;

   private final BinaryContentDAO binaryContentDAO;

   private final CryptoTool cryptoTool;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO, BinaryContentDAO binaryContentDAO, CryptoTool cryptoTool) {
        this.appDocumentDAO = appDocumentDAO;
        this.appPhotoDAO = appPhotoDAO;
        this.binaryContentDAO = binaryContentDAO;
        this.cryptoTool = cryptoTool;
    }


    @Override
    public AppDocument processDoc(Message telegramMessage) {
        Document telegramDoc = telegramMessage.getDocument();
        String fileId = telegramDoc.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK){
            BinaryContent persistentBinaryContent = getBinaryContent(response);
            AppDocument transientAppDoc = buidTransientAppDoc(telegramDoc, persistentBinaryContent);
            return appDocumentDAO.save(transientAppDoc);
        }else {
            throw  new UploadFileException("Bad response from telegram server "+ response);
        }
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        var photoSizeCount = telegramMessage.getPhoto().size();
        var photoIndex = photoSizeCount > 1 ? telegramMessage.getPhoto().size() - 1 : 0;
        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(photoIndex);
        String fileId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK){
            BinaryContent persistentBinaryContent = getBinaryContent(response);
            AppPhoto transientAppDoc = buidTransientAppPhoto(telegramPhoto, persistentBinaryContent);
            return appPhotoDAO.save(transientAppDoc);
        }else {
            throw  new UploadFileException("Bad response from telegram server "+ response);
        }
    }




    public BinaryContent getBinaryContent(ResponseEntity<String> response){
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath);
        BinaryContent transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();

        return binaryContentDAO.save(transientBinaryContent);
    }

    public String getFilePath(ResponseEntity<String> response){
        JSONObject jsonObject = new JSONObject(response.getBody());
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
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

    private AppPhoto buidTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistentBinaryContent) {
        return AppPhoto.builder()
                .telegramFileId(telegramPhoto.getFileId())
                .binaryContent(persistentBinaryContent)
                .fileSize(telegramPhoto.getFileSize())
                .build();
    }


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

    @Override
    public String generateLink(Long docId, LinkType linkType) {
        var hash = cryptoTool.hashOf(docId);
        return "http://" + linkAddress + "/" + linkType + "?id=" + hash;
    }



    //JSONObject jsonObject = new JSONObject(response.getBody());
    //            String filePath = String.valueOf(jsonObject
    //                    .getJSONObject("result")
    //                    .getString("file_path"));
    //            byte[] fileInByte = downloadFile(filePath);
    //            BinaryContent transientBinaryContent = BinaryContent.builder()
    //                    .fileAsArrayOfBytes(fileInByte)
    //                    .build();
}

