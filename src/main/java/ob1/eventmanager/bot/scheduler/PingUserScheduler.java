package ob1.eventmanager.bot.scheduler;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.service.MemberService;
import ob1.eventmanager.utils.MemberStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PingUserScheduler {

    @Autowired
    private EventService eventService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private TelegramBot bot;

    @Scheduled(fixedDelay = 10000)
    private void check() {
        final List<EventEntity> events = eventService.getAllEvents();

        final LocalDateTime now = LocalDateTime.now();

        for (EventEntity event : events) {
            final String chatIdStr = String.valueOf(event.getChatId());

            final List<MemberEntity> members = event.getMembers().stream()
                    .filter(member -> member.getStatus() == MemberStatus.WAIT_PRIVATE_MESSAGE)
                    .filter(member -> member.getAnnounceDate() != null)
                    .filter(member -> now.isAfter(member.getAnnounceDate().plusSeconds(30)))
                    .collect(Collectors.toList());

            members.stream()
                    .filter(member -> member.getAnnounceCount() > 0)
                    .collect(Collectors.toList())
                    .forEach(member -> {
                        memberService.setStatus(member, MemberStatus.LEAVE);
                        bot.kickUser(member.getUser().getTelegramId(), chatIdStr);
                    });

            final List<MemberEntity> announceMembers = members.stream()
                    .filter(member -> member.getAnnounceCount() < 1)
                    .collect(Collectors.toList());

            if (!announceMembers.isEmpty()) {
                final SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatIdStr);
                sendMessage.setParseMode(ParseMode.MARKDOWNV2);

                if (announceMembers.size() == 1) {
                    final MemberEntity member = announceMembers.get(0);

                    sendMessage.setText(bot.getMarkdownMention(member.getUser()) + ", видимо ты не расслышал, но прошу тебя, заполни опросник у меня в ЛС");
                }
                else {
                    final StringBuilder builder = new StringBuilder();

                    for (MemberEntity member : announceMembers) {
                        builder.append(bot.getMarkdownMention(member.getUser())).append(", ");
                    }

                    builder.append(", видимо вы не расслышали, но прошу вас, заполните опросник у меня в ЛС");
                    sendMessage.setText(builder.toString());
                }

                for (MemberEntity member : announceMembers) {
                    member = memberService.setAnnounceDate(member, now);
                    member = memberService.setAnnounceCount(member, member.getAnnounceCount() + 1);
                }
                bot.send(sendMessage);
            }

        }
    }

}
