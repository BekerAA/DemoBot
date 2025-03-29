package ru.Andrey.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.Andrey.AppDocument;

public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {
}
