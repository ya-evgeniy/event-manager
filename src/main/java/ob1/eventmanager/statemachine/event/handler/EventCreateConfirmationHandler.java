package ob1.eventmanager.statemachine.event.handler;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.EventQuestionAnswerEntity;
import ob1.eventmanager.entity.EventQuestionEntity;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.event.EventStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

public class EventCreateConfirmationHandler implements MessageStateMachineHandler<EventStates> {

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachineContext<EventStates> context) {
        final String text = context.get("text");
        final String chatId = context.get("chatId");
        final EventEntity event = context.get("event");
        final String callbackQuery = context.get("callbackData");
        final int messageId = context.get("messageId");


        final EventStates previousState = context.getPreviousState();
        if (previousState == EventStates.TEMPLATE_QUESTIONS) {
            bot.send("Итак, ваше мероприятие полностью готово! Проверьте еще раз информацию о нем:\n" +
                    "Название мероприятия: "+event.getName()+"\n"+
                    "Время проведения: "+event.getDate()+"\n"+
                    "Место: "+event.getPlace()+"\n", chatId);

            List<EventQuestionEntity> questions = event.getQuestions();

            StringBuilder concatenatedString = new StringBuilder();
            for(EventQuestionEntity question: questions){
                concatenatedString.append("\nВопрос: ").append(question.getQuestion()).append("\n Варианты ответа: ");
                List<EventQuestionAnswerEntity> answers = question.getAnswers();
                for(EventQuestionAnswerEntity answer: answers){
                    concatenatedString.append(answer.getAnswer()).append(" ");
                }
            }
            bot.send("Я задам участникам вашего мероприятия следующие вопросы:\n"+
                    concatenatedString, chatId);

            InlineKeyboardMarkup keyboardMarkup = CreateConfirmationButtonBuilder.build();//fixme проверьте ето

            final SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setReplyMarkup(keyboardMarkup);
            bot.send(sendMessage);

        } else if (previousState == EventStates.CREATE_CONFIRM) {
            if (callbackQuery.equals("confirmation")) {

                final EditMessageText editMessage = new EditMessageText();
                editMessage.setMessageId(messageId);
                editMessage.setChatId(chatId);
                editMessage.setText("Мероприятие подтверждено!");
                bot.send(editMessage);
                context.setNextState(EventStates.LISTEN_MEMBERS);

            } else if (callbackQuery.equals("cancellation")) {
                final EditMessageText editMessage = new EditMessageText();
                editMessage.setMessageId(messageId);
                editMessage.setChatId(chatId);
                editMessage.setText("Мероприятие отменено!");
                bot.send(editMessage);
                context.setNextState(EventStates.NEW);
            }
        } else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}