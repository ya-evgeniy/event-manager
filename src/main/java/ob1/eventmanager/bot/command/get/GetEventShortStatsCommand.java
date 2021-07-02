package ob1.eventmanager.bot.command.get;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.bot.ann.LocalCommand;
import ob1.eventmanager.bot.command.LocalCommandHandler;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.EventQuestionAnswerEntity;
import ob1.eventmanager.entity.EventQuestionEntity;
import ob1.eventmanager.entity.MemberAnswerEntity;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.exception.EventNotFoundException;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.statemachine.MessageStateMachine;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import ob1.eventmanager.utils.KeyboardUtils;
import ob1.eventmanager.utils.MapUtils;
import ob1.eventmanager.utils.MemberStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ob1.eventmanager.utils.KeyboardUtils.buttonOf;

@LocalCommand("get_event_short_stats")
public class GetEventShortStatsCommand implements LocalCommandHandler {

    @Autowired
    private EventService eventService;

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachine<LocalChatStates> stateMachine, Map<String, Object> headers) {
        String chatId = (String) headers.get("chatId");

        if (stateMachine.getCurrentState() != LocalChatStates.WAIT_COMMANDS) {
            bot.send(
                    "Похоже, что ты все еще не завершил предудыщее действие(почитай выше в чате). Как закончишь, выполни команду заново.",
                    chatId
            );
            return;
        }

        UserEntity user = (UserEntity) headers.get("user");

        final String[] commandArgs = (String[]) headers.get("commandArgs");

        if (commandArgs.length == 0) {
            bot.send("К сожалению, не могу найти это мероприятие...Попробуй выбрать другое.", chatId);
            return;
        }

        EventEntity event;
        try {
            long eventId = Long.parseLong(commandArgs[0]);
            event = eventService.getEventById(eventId);
        }
        catch (NumberFormatException | EventNotFoundException e) {
            bot.send("К сожалению, не могу найти это мероприятие...Попробуй выбрать другое.", chatId);
            return;
        }

        if (event.getOwner().getId() != user.getId()) {
            bot.send("Мероприятие не пренадлежит тебе, ты не можешь его изменять!", chatId);
            return;
        }

        final StringBuilder builder = new StringBuilder();
        builder.append("<b>Краткая статистика по мероприятию</b>").append("\n\n");

        appendMembersStats(builder, event);
        appendBaseQuestionsStats(builder, event);
        appendQuestionsStats(builder, event);
        appendStartingStats(builder, event);

        final SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(builder.toString());
        sendMessage.enableHtml(true);

        sendMessage.setReplyMarkup(KeyboardUtils.inlineOf(
                buttonOf("Назад", "/edit_event " + event.getId())
        ));

        bot.send(sendMessage);
    }

    private void appendMembersStats(StringBuilder builder, EventEntity event) {
        final List<MemberEntity> members = event.getMembers();

        builder.append("Зарегистрировалось участников: ").append(members.size()).append("\n");
        builder.append("Из них:").append("\n");

        long confirmCount = members.stream()
                .filter(member -> member.getStatus() == MemberStatus.CONFIRM)
                .count();

        long thinkCount = members.stream()
                .filter(member -> member.getStatus() == MemberStatus.THINK)
                .count();

        long cancelCount = members.stream()
                .filter(member -> member.getStatus() == MemberStatus.CANCEL || member.getStatus() == MemberStatus.LEAVE)
                .count();

        long fillQuestionsCount = members.stream()
                .filter(member -> member.getStatus() == MemberStatus.FILL_QUESTIONS)
                .count();

        builder.append("    ").append(confirmCount)
                .append(" (").append(percent(confirmCount, members.size())).append("%) ")
                .append("подтвердили участие").append("\n");

        builder.append("    ").append(thinkCount)
                .append(" (").append(percent(thinkCount, members.size())).append("%) ")
                .append("думают об участии").append("\n");

        builder.append("    ").append(cancelCount)
                .append(" (").append(percent(cancelCount, members.size())).append("%) ")
                .append("отказались участвовать").append("\n");

        builder.append("    ").append(fillQuestionsCount)
                .append(" (").append(percent(fillQuestionsCount, members.size())).append("%) ")
                .append("отвечают на вопросы").append("\n\n");
    }

    private void appendBaseQuestionsStats(StringBuilder builder, EventEntity event) {
        final List<MemberEntity> members = event.getMembers()
                .stream()
                .filter(member -> member.getStatus() != MemberStatus.FILL_QUESTIONS && member.getStatus() != MemberStatus.LEAVE)
                .collect(Collectors.toList());

        long place = members.stream()
                .filter(member -> member.getComfortPlace() == null)
                .count();

        long date = members.stream()
                .filter(member -> member.getComfortDate() == null)
                .count();

        long time = members.stream()
                .filter(member -> member.getComfortTime() == null)
                .count();

        builder.append(place).append(" ")
                .append(" (").append(percent(place, members.size())).append("%) ")
                .append("участников согласны с местом проведения").append("\n");

        builder.append(date).append(" ")
                .append(" (").append(percent(date, members.size())).append("%) ")
                .append("участников согласны с датой проведения").append("\n");

        builder.append(time).append(" ")
                .append(" (").append(percent(time, members.size())).append("%) ")
                .append("участников согласны с временем проведения").append("\n\n");
    }

    private void appendQuestionsStats(StringBuilder builder, EventEntity event) {
        final List<MemberEntity> members = event.getMembers()
                .stream()
                .filter(member -> member.getStatus() != MemberStatus.FILL_QUESTIONS && member.getStatus() != MemberStatus.LEAVE)
                .collect(Collectors.toList());
        final List<EventQuestionEntity> questions = event.getQuestions();

        final long count = members.stream()
                .filter(member -> member.getStatus() != MemberStatus.FILL_QUESTIONS)
                .count();

        builder.append(count).append(" ")
                .append(" (").append(percent(count, members.size())).append("%) ")
                .append("участников ответили на вопросы:");

        for (EventQuestionEntity question : questions) {
            final Map<String, Integer> answers = countAnswers(members, question);
            final long sum = answers.values().stream().mapToLong(value -> value).sum();

            builder.append("\n    ").append(question.getQuestion());

            final Map<String, Integer> sortedAnswers = MapUtils.sortByValue(answers);
            for (Map.Entry<String, Integer> entry : sortedAnswers.entrySet()) {
                builder.append("\n        ").append(entry.getValue())
                        .append(" (").append(percent(entry.getValue(), sum)).append("%) ")
                        .append("- ").append(entry.getKey());
            }

            for (EventQuestionAnswerEntity answer : question.getAnswers()) {
                if (sortedAnswers.containsKey(answer.getAnswer())) continue;

                builder.append("\n        ").append(0)
                        .append(" (").append(percent(0, sum)).append("%) ")
                        .append("- ").append(answer.getAnswer());
            }
        }
    }

    private Map<String, Integer> countAnswers(List<MemberEntity> members, EventQuestionEntity question) {
        Map<String, Integer> countAnswers = new HashMap<>();

        final List<MemberAnswerEntity> answers = members.stream()
                .filter(member -> member.getStatus() != MemberStatus.FILL_QUESTIONS)
                .flatMap(member -> member.getAnswers().stream())
                .filter(memberAnswer -> memberAnswer.getQuestion().getId() == question.getId())
                .collect(Collectors.toList());

        for (MemberAnswerEntity answer : answers) {
            final Integer count = countAnswers.getOrDefault(answer.getAnswer(), 0);
            countAnswers.put(answer.getAnswer(), count + 1);
        }

        return countAnswers;
    }

    private void appendStartingStats(StringBuilder builder, EventEntity event) {

    }

    private long percent(long value, long maxValue) {
        if (maxValue < 1) return 0;
        return (long) ((double) value / (double) maxValue * 100);
    }

}
