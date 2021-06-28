package ob1.eventmanager.service;

import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.MemberEntity;

import java.util.List;

public interface MemberService {

    List<EventEntity> getEventsByTelegramId(int telegramId);

    MemberEntity setComfortPlace(MemberEntity member, String place);

    MemberEntity setComfortDate(MemberEntity member, String date);

}
