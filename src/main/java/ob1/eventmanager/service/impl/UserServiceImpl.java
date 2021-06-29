package ob1.eventmanager.service.impl;

import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.repository.UserRepository;
import ob1.eventmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserEntity createUser(int telegramId, String name) {
        final UserEntity user = UserEntity.builder()
                .telegramId(telegramId)
                .name(name)
                .build();

        System.out.println("CREATE USER");

        return userRepository.save(user);
    }

    @Override
    public Optional<UserEntity> getUserByTelegramId(int id) {
        return userRepository.getByTelegramId(id);
    }

    @Override
    public UserEntity setUserSelectedEvent(UserEntity user, EventEntity event) {
        System.out.println("SETTING SELECTED EVENT " + event.getId());
        final UserEntity userEntity = UserEntity.builder()
                .id(user.getId())
                .telegramId(user.getTelegramId())
                .name(user.getName())
                .chatId(user.getChatId())
                .selectedEvent(event)
                .previousState(user.getPreviousState())
                .currentState(user.getCurrentState())
                .build();

        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity setUserChatId(UserEntity user, Long chatId) {
        final UserEntity userEntity = UserEntity.builder()
                .id(user.getId())
                .telegramId(user.getTelegramId())
                .name(user.getName())
                .chatId(chatId)
                .selectedEvent(user.getSelectedEvent())
                .previousState(user.getPreviousState())
                .currentState(user.getCurrentState())
                .build();

        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity setUserState(UserEntity user, String previous, String current) {
        final UserEntity userEntity = UserEntity.builder()
                .id(user.getId())
                .telegramId(user.getTelegramId())
                .name(user.getName())
                .chatId(user.getChatId())
                .selectedEvent(user.getSelectedEvent())
                .previousState(previous)
                .currentState(current)
                .build();

        return userRepository.save(userEntity);
    }

}
