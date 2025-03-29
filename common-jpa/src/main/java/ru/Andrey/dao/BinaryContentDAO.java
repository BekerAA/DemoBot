package ru.Andrey.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.Andrey.BinaryContent;

public interface BinaryContentDAO extends JpaRepository<BinaryContent, Long> {
}
