package ob1.eventmanager.service;

import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.UserEntity;

import java.util.Optional;

public interface UserService {

    UserEntity createUser(int telegramId, String name);

    Optional<UserEntity> getUserByTelegramId(int id);

    UserEntity setUserSelectedEvent(UserEntity user, EventEntity event);

    UserEntity setUserChatId(UserEntity user, Long chatId);

    UserEntity setUserState(UserEntity user, String previous, String current);

}
