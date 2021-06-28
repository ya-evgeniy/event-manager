package ob1.eventmanager.service.impl;

import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.service.MemberService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    @Override
    public List<EventEntity> getEventsByTelegramId(int telegramId) {
        return null;
    }

}
