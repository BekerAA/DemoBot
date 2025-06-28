package ru.Andrey.sercvice;

import org.springframework.core.io.FileSystemResource;
import ru.Andrey.AppDocument;
import ru.Andrey.AppPhoto;
import ru.Andrey.BinaryContent;

public interface FileService {
    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);
    FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
