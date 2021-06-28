package ob1.eventmanager.service.impl;

import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.repository.MemberRepository;
import ob1.eventmanager.service.MemberService;
import ob1.eventmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private UserService userService;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public Set<EventEntity> getEventsByTelegramId(int telegramId) {
        final UserEntity userEntity = userService.getUserByTelegramId(telegramId);
        final Set<MemberEntity> members = memberRepository.findAllByUser(userEntity);

        return members.stream()
                .map(MemberEntity::getEvent)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<EventEntity> getActualEventsByTelegramId(int telegramId) {
        final LocalDateTime now = LocalDateTime.now();

        return getEventsByTelegramId(telegramId).stream()
                .filter(EventEntity::isVerified)
                .filter(event -> event.getDate() != null)
                .filter(event -> event.getDate().isAfter(now))
                .collect(Collectors.toSet());
    }

}
