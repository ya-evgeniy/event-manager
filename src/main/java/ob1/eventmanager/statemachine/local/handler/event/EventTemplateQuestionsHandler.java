package ob1.eventmanager.statemachine.local.handler.event;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.TemplateQuestionEntity;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.service.TemplateService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component("localEventTemplateQuestionHandler")
public class EventTemplateQuestionsHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Autowired
    private EventService eventService;

    @Autowired
    private TemplateService templateService;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        EventEntity event = context.get("event");
        final String text = context.get("text");
        final String chatId = context.get("chatId");

        final LocalChatStates previousState = context.getPreviousState();

        if (previousState == LocalChatStates.EVENT_TEMPLATE) {
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();//fixme кнопки по нормальному сделать

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

            final SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Выберите ответ:");
            sendMessage.setChatId(chatId);
            StringBuilder str = new StringBuilder();
            str.append("Вопросы:");
            for (TemplateQuestionEntity question : event.getTemplate().getQuestions()) {
                str.append("\n").append(question.getQuestion());
            }
            sendMessage.setText(str.toString());
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            bot.send(sendMessage);
        } else if (previousState == LocalChatStates.EVENT_TEMPLATE_QUESTION) {
            final String callbackData = context.get("callbackData");
            if (callbackData.equals("cancel")) {
                context.setNextState(LocalChatStates.EVENT_TEMPLATE);
            }
            else if (callbackData.equals("success")) {
                event = templateService.copyTemplateToEvent(event.getTemplate(), event);
                context.getHeaders().put("event", event);

                context.setNextState(LocalChatStates.EVENT_CONFIRM);
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
