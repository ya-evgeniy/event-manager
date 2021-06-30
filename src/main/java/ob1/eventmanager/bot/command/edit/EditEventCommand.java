package ob1.eventmanager.bot.command.edit;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.bot.ann.LocalCommand;
import ob1.eventmanager.bot.command.LocalCommandHandler;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.EventQuestionAnswerEntity;
import ob1.eventmanager.entity.EventQuestionEntity;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.exception.EventNotFoundException;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.statemachine.MessageStateMachine;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import ob1.eventmanager.utils.KeyboardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.Map;

import static ob1.eventmanager.utils.KeyboardUtils.buttonOf;

@LocalCommand("edit_event")
public class EditEventCommand implements LocalCommandHandler {

    @Autowired
    private EventService eventService;

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachine<LocalChatStates> stateMachine, Map<String, Object> headers) {
        String chatId = (String) headers.get("chatId");

        if (stateMachine.getCurrentState() != LocalChatStates.WAIT_COMMANDS) {
            bot.send(
                    "Похоже, что ты сейчас заполняешь какую-то информацию. Как закончишь, выполни команду заного.",
                    chatId
            );
            return;
        }

        UserEntity user = (UserEntity) headers.get("user");
        long chatIdLong = (long) headers.get("chatIdLong");
        final String[] commandArgs = (String[]) headers.get("commandArgs");

        if (commandArgs.length == 0) {
            bot.send("Выбранное мероприятие не найдено, попробуй выбрать другое.", chatId);
            return;
        }

        EventEntity event;
        try {
            long eventId = Long.parseLong(commandArgs[0]);
            event = eventService.getEventById(eventId);
        }
        catch (NumberFormatException | EventNotFoundException e) {
            bot.send("Выбранное мероприятие не найдено, попробуй выбрать другое.", chatId);
            return;
        }

        if (event.getOwner().getId() != user.getId()) {
            bot.send("Мероприятие не пренадлежит тебе, ты не можешь его изменять!", chatId);
            return;
        }

        StringBuilder messageTextBuilder = new StringBuilder();
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

        if (event.getChatId() == null) {
            messageTextBuilder.append("\n\n").append("<b>Статус: Ожидает добавления в групповой чат");
            messageTextBuilder.append("\n").append("Если нужна помощь с добавлением бота в чат, воспользуйся командой</b> /help_invite");
        }
        else {
            messageTextBuilder.append("\n\n").append("Статус: Добавлен в групповой чат");
        }

        messageTextBuilder.append("\n\n").append("Для изменения или отмены мероприятия, воспользуйся кнопками под сообщением");

        final SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messageTextBuilder.toString());
        message.enableHtml(true);

        message.setReplyMarkup(KeyboardUtils.inlineOf(
                buttonOf("Изменить название", "/edit_event_name " + event.getId()),
                buttonOf("Изменить место", "/edit_event_place " + event.getId()),
                buttonOf("Изменить дату", "/edit_event_date " + event.getId()),
                buttonOf("Изменить время", "/edit_event_time " + event.getId()),
                buttonOf("Изменить вопросы", "/edit_event_questions " + event.getId()),
                buttonOf("Отменить мероприятие", "/cancel_event " + event.getId()),
                buttonOf("Назад", "/manage_events")
        ));

        bot.send(message);
    }

}
