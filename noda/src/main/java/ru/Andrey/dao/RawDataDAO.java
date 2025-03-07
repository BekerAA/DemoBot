package ru.Andrey.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.Andrey.entity.RawData;

public interface RawDataDAO extends JpaRepository<RawData, Long> {

}
