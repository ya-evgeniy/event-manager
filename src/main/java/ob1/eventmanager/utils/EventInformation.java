package ob1.eventmanager.utils;

import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.TemplateQuestionEntity;


public class EventInformation {

    private final EventEntity event;

    public EventInformation(final EventEntity event) {
        this.event = event;
    }

    public String showAll() {

        StringBuilder str = new StringBuilder();
        str.append("Вопросы:");
        for (TemplateQuestionEntity question : event.getTemplate().getQuestions()) {
            str.append("\n").append(question.getQuestion());
        }

        return String.format(
                "Название мероприятия: %s\nДата и время: %s\nМесто: %s\n%s",
                event.getName(),
                event.getDate(),
                event.getPlace(),
                str);
    }
}
