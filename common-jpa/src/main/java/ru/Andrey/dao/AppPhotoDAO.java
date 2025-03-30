package ru.Andrey.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.Andrey.AppPhoto;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long>{
}
