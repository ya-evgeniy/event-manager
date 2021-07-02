package ob1.eventmanager.utils;

import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.EventQuestionAnswerEntity;
import ob1.eventmanager.entity.EventQuestionEntity;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class ObjectsToString {

    private static final Locale RU_LOCALE = new Locale("ru");

    public static String date(LocalDateTime date) {
        return String.format(
                "%s %s %s",
                date.getDayOfMonth(),
                date.getMonth().getDisplayName(TextStyle.FULL, RU_LOCALE),
                date.getYear()
        );
    }

    public static String dateDDMMYYYY(LocalDateTime date) {
        return String.format(
                "%02d.%02d.%04d",
                date.getDayOfMonth(),
                date.getMonthValue(),
                date.getYear()
        );
    }

    public static String time(LocalDateTime date) {
        return String.format(
                "%02d:%02d",
                date.getHour(),
                date.getMinute()
        );
    }

    public static String eventForOwner(EventEntity event) {
        StringBuilder messageTextBuilder = new StringBuilder();
        messageTextBuilder.append("Название: ").append(event.getName()).append('\n');
        messageTextBuilder.append("Место: ").append(event.getPlace()).append('\n');
        messageTextBuilder.append("Дата: ").append(ObjectsToString.date(event.getDate())).append('\n');
        messageTextBuilder.append("Время: ").append(ObjectsToString.time(event.getTime())).append('\n');
        messageTextBuilder.append('\n');

        messageTextBuilder.append("Вопросы для участников:");
        for (EventQuestionEntity question : event.getQuestions()) {
            messageTextBuilder.append("\n").append(question.getQuestion());
            List<EventQuestionAnswerEntity> answers = question.getAnswers();
            for (EventQuestionAnswerEntity answer : answers) {
                messageTextBuilder.append("\n - ").append(answer.getAnswer());
            }
        }

        if (event.getChatId() == null) {
            messageTextBuilder.append("\n\n").append("<b>Статус: Ожидает добавления в групповой чат");
            messageTextBuilder.append("\n").append("Не забудь мне дать права администратора</b>");
//            messageTextBuilder.append("\n").append("Если нужна помощь с добавлением бота в чат, воспользуйся командой</b> /help_invite");
        }
        else {
            messageTextBuilder.append("\n\n").append("Статус: Добавлен в групповой чат");
        }

        messageTextBuilder.append("\n\n").append("Для управления мероприятием, воспользуйся кнопками под сообщением");
        return messageTextBuilder.toString();
    }

}
