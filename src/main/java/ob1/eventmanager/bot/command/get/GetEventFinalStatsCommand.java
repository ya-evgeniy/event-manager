package ob1.eventmanager.bot.command.get;

import com.opencsv.CSVWriter;
import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.bot.ann.LocalCommand;
import ob1.eventmanager.bot.command.LocalCommandHandler;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.EventQuestionEntity;
import ob1.eventmanager.entity.MemberAnswerEntity;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.exception.EventNotFoundException;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.statemachine.MessageStateMachine;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import ob1.eventmanager.utils.ObjectsToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@LocalCommand("get_event_final_stats")
public class GetEventFinalStatsCommand implements LocalCommandHandler {

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

        bot.send("Сейчас я сформирую файл с статистикой и пришлю его тебе!", chatId);

        try(StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer)) {

            final List<String> header = new ArrayList<>();
            header.add("ИД");
            header.add("ИД чата");
            header.add("Имя");
            header.add("Статус");
            header.add("Комфортное место");
            header.add("Комфортная дата");
            header.add("Комфортное время");

            final List<EventQuestionEntity> questions = event.getQuestions();
            for (EventQuestionEntity question : questions) {
                header.add(question.getQuestion());
            }

            csvWriter.writeNext(header.toArray(new String[0]));

            for (MemberEntity member : event.getMembers()) {
                final List<String> line = new ArrayList<>();
                line.add(String.valueOf(member.getUser().getTelegramId()));
                line.add(String.valueOf(member.getUser().getChatId()));
                line.add(String.valueOf(member.getUser().getName()));
                line.add(member.getStatus() == null ? "" : member.getStatus().getLocalized());
                line.add(member.getComfortPlace() == null ? "" : member.getComfortPlace());
                line.add(member.getComfortDate() == null ? "" : ObjectsToString.dateDDMMYYYY(member.getComfortDate()));
                line.add(member.getComfortTime() == null ? "" : ObjectsToString.time(member.getComfortTime()));

                contQuestion: for (EventQuestionEntity question : questions) {
                    for (MemberAnswerEntity answer : member.getAnswers()) {
                        if (answer.getQuestion().getId() == question.getId()) {
                            line.add(answer.getAnswer());
                            continue contQuestion;
                        }
                    }
                }

                csvWriter.writeNext(line.toArray(new String[0]));
            }

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(writer.toString().getBytes(StandardCharsets.UTF_8))) {
                final SendDocument sendDocument = new SendDocument();
                sendDocument.setChatId(chatId);
                sendDocument.setDocument(new InputFile(inputStream, event.getName() + ".csv"));
                bot.send(sendDocument);
            }
            catch (IOException e) {
                e.printStackTrace();
                bot.send("Упс! Что-то пошло не так во время формирования статистики. Попробуй сформировать ее позже.", chatId);
            }

        }
        catch (IOException e) {
            e.printStackTrace();
            bot.send("Упс! Что-то пошло не так во время формирования статистики. Попробуй сформировать ее позже.", chatId);
        }

    }

}
