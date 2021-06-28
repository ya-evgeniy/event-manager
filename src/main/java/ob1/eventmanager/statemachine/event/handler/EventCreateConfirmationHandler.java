package ob1.eventmanager.statemachine.event.handler;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.EventQuestionAnswerEntity;
import ob1.eventmanager.entity.EventQuestionEntity;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.event.EventStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.List;

@Component("eventCreateConfirmHandler")
public class EventCreateConfirmationHandler implements MessageStateMachineHandler<EventStates> {

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachineContext<EventStates> context) {
        System.out.println(context.getPreviousState() + " -> " + context.getCurrentState());

        EventEntity event = context.get("event");
        final String text = context.get("text");
        final String chatId = context.get("chatId");
        final String callbackQuery = context.get("callbackData");
        final int messageId = context.get("messageId");


        final EventStates previousState = context.getPreviousState();
        if (previousState == EventStates.TEMPLATE_QUESTIONS) {
            final EditMessageText editMessage = new EditMessageText();
            editMessage.setChatId(chatId);
            editMessage.setMessageId(messageId);
            editMessage.setText("Итак, ваше мероприятие полностью готово! Проверьте еще раз информацию о нем:\n" +
                    "Название мероприятия: " + event.getName() + "\n" +
                    "Время проведения: " + event.getDate() + "\n" +
                    "Место: " + event.getPlace() + "\n"
            );
            bot.send(editMessage);

            StringBuilder builder = new StringBuilder();
            builder.append("Я задам участникам вашего мероприятия следующие вопросы:\n");
            for (EventQuestionEntity question : event.getQuestions()) {
                builder.append("\n").append(question.getQuestion());
                List<EventQuestionAnswerEntity> answers = question.getAnswers();
                for (EventQuestionAnswerEntity answer : answers) {
                    builder.append("\n - ").append(answer.getAnswer());
                }
            }

            final SendMessage sendMessage = new SendMessage();

            sendMessage.setText(builder.toString());
            sendMessage.setChatId(chatId);
            sendMessage.setReplyMarkup(CreateConfirmationButtonBuilder.build());

            bot.send(sendMessage);

        } else if (previousState == EventStates.CREATE_CONFIRM) {
            if (callbackQuery.equals("confirm")) {

                final EditMessageText editMessage = new EditMessageText();
                editMessage.setMessageId(messageId);
                editMessage.setChatId(chatId);
                editMessage.setText("Мероприятие подтверждено! Начинайте приглашать людей в чат!");
                bot.send(editMessage);
                context.setNextState(EventStates.LISTEN_MEMBERS);

            } else if (callbackQuery.equals("cancel")) {
                final EditMessageText editMessage = new EditMessageText();
                editMessage.setMessageId(messageId);
                editMessage.setChatId(chatId);
                editMessage.setText("Мероприятие отменено!");
                bot.send(editMessage);
                context.setNextState(EventStates.LEAVE_FROM_CHAT);
            } else if (callbackQuery.equals("edit")) {
                throw new UnsupportedOperationException("not impl yet");
            }
        } else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}