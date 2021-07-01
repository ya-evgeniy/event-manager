package ob1.eventmanager.bot.command;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.bot.ann.LocalCommand;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.EventQuestionAnswerEntity;
import ob1.eventmanager.entity.EventQuestionEntity;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.statemachine.MessageStateMachine;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import ob1.eventmanager.utils.KeyboardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ob1.eventmanager.utils.KeyboardUtils.buttonOf;

@LocalCommand("manage_events")
public class ManageEventsCommand implements LocalCommandHandler {

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
        long chatIdLong = (long) headers.get("chatIdLong");

        final List<EventEntity> ownerEvents = eventService.getOwnerEvents(chatIdLong).stream()
                .filter(event -> !event.isCompleted())
                .collect(Collectors.toList());

        if (ownerEvents.isEmpty()) {
            final SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Сейчас у тебя нет организованных мероприятий.\n\nДля создания нового мероприятия, воспользуйся командой /new_event");
            bot.send(sendMessage);
            return;
        }

        final SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Выбери мероприятие которое хочешь изменить");

        final List<List<InlineKeyboardButton>> keyboard = ownerEvents.stream()
                .map(event -> buttonOf(event.getName(), "/edit_event " + event.getId()))
                .map(Collections::singletonList)
                .collect(Collectors.toList());
        sendMessage.setReplyMarkup(new InlineKeyboardMarkup(keyboard));

        bot.send(sendMessage);

//        for (EventEntity event : ownerEvents) {
//            StringBuilder messageTextBuilder = new StringBuilder();
//            messageTextBuilder.append("Название: ").append(event.getName()).append('\n');
//            messageTextBuilder.append("Место: ").append(event.getPlace()).append('\n');
//            messageTextBuilder.append("Время: ").append(event.getDate()).append('\n');
//            messageTextBuilder.append('\n');
//
//            messageTextBuilder.append("Вопросы для участников:");
//            for (EventQuestionEntity question : event.getQuestions()) {
//                messageTextBuilder.append("\n").append(question.getQuestion());
//                List<EventQuestionAnswerEntity> answers = question.getAnswers();
//                for (EventQuestionAnswerEntity answer : answers) {
//                    messageTextBuilder.append("\n - ").append(answer.getAnswer());
//                }
//            }
//
//            messageTextBuilder.append("\n\n").append("Для изменения или отмены мероприятия, воспользуйся кнопками под сообщением");
//
//            final SendMessage message = new SendMessage();
//            message.setChatId(chatId);
//            message.setText(messageTextBuilder.toString());
//
//            message.setReplyMarkup(KeyboardUtils.inlineOf(
//                    buttonOf("Изменить место", "/edit_event_place " + event.getId()),
//                    buttonOf("Изменить дату", "/edit_event_date " + event.getId()),
//                    buttonOf("Изменить время", "/edit_event_time " + event.getId()),
//                    buttonOf("Изменить вопросы", "/edit_event_questions " + event.getId()),
//                    buttonOf("Отменить мероприятие", "/cancel_event " + event.getId())
//            ));
//
//            bot.send(message);
//        }
    }

}
