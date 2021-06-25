package ob1.eventmanager.service.impl;

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
    public UserEntity getUserByTelegramId(int id) {
        final Optional<UserEntity> optUser = userRepository.getByTelegramId(id);
        if (optUser.isPresent()) {
            return optUser.get();
        }

        final UserEntity user = UserEntity.builder()
                .telegramId(id)
                .build();

        return userRepository.save(user);
    }

}
