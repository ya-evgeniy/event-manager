package ob1.eventmanager.statemachine.local.handler.event;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.TemplateQuestionEntity;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.service.TemplateService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import ob1.eventmanager.utils.KeyboardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
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
            final SendMessage editMessage = new SendMessage();
            editMessage.setText("Выберите ответ:");
            editMessage.setChatId(chatId);
//            editMessage.setMessageId(context.get("messageId"));
            StringBuilder str = new StringBuilder();
            str.append("Вопросы:");
            for (TemplateQuestionEntity question : event.getTemplate().getQuestions()) {
                str.append("\n").append(question.getQuestion());
            }
            editMessage.setText(str.toString());
            editMessage.setReplyMarkup(KeyboardUtils.inlineOf(
                    KeyboardUtils.buttonOf("Устраивают", "success"),
                    KeyboardUtils.buttonOf("Не устраивают", "cancel")//,
//                    KeyboardUtils.buttonOf("Хочу изменить шаблон", "edit")
            ));
            bot.send(editMessage);
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
