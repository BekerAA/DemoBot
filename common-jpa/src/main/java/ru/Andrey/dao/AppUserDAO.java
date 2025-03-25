package ru.Andrey.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.Andrey.AppUser;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {
    AppUser findAppUserByTelegramUserId(Long id);

}
