package ob1.eventmanager.statemachine.local.handler.event;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.EventQuestionAnswerEntity;
import ob1.eventmanager.entity.EventQuestionEntity;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import ob1.eventmanager.utils.KeyboardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.Arrays;
import java.util.List;

import static ob1.eventmanager.utils.KeyboardUtils.buttonOf;

@Component("localEventConfirmHandler")
public class EventConfirmHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private EventService eventService;

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        EventEntity event = context.get("event");
        final String text = context.get("text");
        final String chatId = context.get("chatId");
        final String callbackQuery = context.get("callbackData");
        final int messageId = context.get("messageId");


        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == LocalChatStates.EVENT_TEMPLATE_QUESTION) {
            final EditMessageText editMessage = new EditMessageText();
            editMessage.setChatId(chatId);
            editMessage.setMessageId(messageId);

            StringBuilder messageTextBuilder = new StringBuilder();
            messageTextBuilder.append("Итак, твое мероприятие заполнено! Проверь информацию еще раз. В любом случае ты сможешь изменить ее позже.").append("\n\n");
            messageTextBuilder.append("Название: ").append(event.getName()).append('\n');
            messageTextBuilder.append("Место: ").append(event.getPlace()).append('\n');
            messageTextBuilder.append("Время: ").append(event.getDate()).append('\n');
            messageTextBuilder.append('\n');

            messageTextBuilder.append("Вопросы для участников:");
            for (EventQuestionEntity question : event.getQuestions()) {
                messageTextBuilder.append("\n").append(question.getQuestion());
                List<EventQuestionAnswerEntity> answers = question.getAnswers();
                for (EventQuestionAnswerEntity answer : answers) {
                    messageTextBuilder.append("\n - ").append(answer.getAnswer());
                }
            }

            messageTextBuilder.append("\n\n").append("Если все правильно, то приглашай меня в групповой чат.").append("\n");
            messageTextBuilder.append("Если хочешь отменить мероприятие или изменить, воспользуйся кнопками ниже");

            editMessage.setText(messageTextBuilder.toString());
            editMessage.setReplyMarkup(KeyboardUtils.inlineOf(
                    buttonOf("Отменить", "cancel"),
                    buttonOf("Изменить", "edit")
            ));

            bot.send(editMessage);
        } else if (previousState == LocalChatStates.EVENT_CONFIRM) {
            if (callbackQuery.equals("cancel")) {
                eventService.delete(event);

                final EditMessageText editMessage = new EditMessageText();
                editMessage.setMessageId(messageId);
                editMessage.setChatId(chatId);
                editMessage.setText("Мероприятие отменено!");
                bot.send(editMessage);

                context.setNextState(LocalChatStates.CHECK_ACTUAL_EVENTS);
            } else if (callbackQuery.equals("edit")) {
                throw new UnsupportedOperationException("not impl yet");
            }
        } else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}