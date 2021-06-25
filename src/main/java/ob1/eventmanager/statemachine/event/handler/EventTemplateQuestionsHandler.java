package ob1.eventmanager.statemachine.event.handler;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.TemplateQuestionEntity;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.event.EventStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class EventTemplateQuestionsHandler implements MessageStateMachineHandler<EventStates> {

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachineContext<EventStates> context) {

        final String text = context.get("text");
        final String chatId = context.get("chatId");
        final EventEntity event = context.get("event");

        final EventStates previousState = context.getPreviousState();

        if (previousState == EventStates.TEMPLATE) {
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();

            List<InlineKeyboardButton> inlineKeyboardButtonList1 = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
            inlineKeyboardButton1.setText("Не устраивают");
            inlineKeyboardButton1.setCallbackData("cancel");

            List<InlineKeyboardButton> inlineKeyboardButtonList2 = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
            inlineKeyboardButton2.setText("Устраивают");
            inlineKeyboardButton2.setCallbackData("success");

            List<InlineKeyboardButton> inlineKeyboardButtonList3 = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
            inlineKeyboardButton3.setText("Хочу изменить шаблон");
            inlineKeyboardButton3.setCallbackData("edit");

            inlineKeyboardButtonList1.add(inlineKeyboardButton1);
            inlineKeyboardButtonList2.add(inlineKeyboardButton2);
            inlineKeyboardButtonList3.add(inlineKeyboardButton3);

            inlineButtons.add(inlineKeyboardButtonList1);
            inlineButtons.add(inlineKeyboardButtonList2);
            inlineButtons.add(inlineKeyboardButtonList3);

            inlineKeyboardMarkup.setKeyboard(inlineButtons);

            final EditMessageText editMessage = new EditMessageText();
            editMessage.setText("Выберите ответ:");
            editMessage.setChatId(chatId);
            editMessage.setMessageId(context.get("messageId"));
            StringBuilder str = new StringBuilder();
            str.append("Вопросы:");
            for (TemplateQuestionEntity question : event.getTemplate().getQuestions()) {
                str.append("\n").append(question.getQuestion());
            }
            editMessage.setText(str.toString());
            editMessage.setReplyMarkup(inlineKeyboardMarkup);
            bot.send(editMessage);
        } else if (previousState == EventStates.TEMPLATE_QUESTIONS) {
            final String callbackData = context.get("callbackData");
            if (callbackData.equals("cancel")) {
                context.setNextState(EventStates.TEMPLATE);
            }
            else if (callbackData.equals("success")) {
                context.setNextState(EventStates.CREATE_CONFIRM);
            }
            else if (callbackData.equals("edit")) {
                throw new UnsupportedOperationException("edit not implemented"); //TODO
            }
            else {
                throw new UnsupportedOperationException(callbackData);
            }
        } else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }
}
