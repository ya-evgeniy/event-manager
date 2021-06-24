package ob1.eventmanager.service;

import ob1.eventmanager.entity.UserEntity;

public interface UserService {

    UserEntity getUserByTelegramId(int id);

}
