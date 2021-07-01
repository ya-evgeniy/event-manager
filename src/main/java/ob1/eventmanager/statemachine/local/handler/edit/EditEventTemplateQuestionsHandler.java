package ob1.eventmanager.statemachine.local.handler.edit;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.categoryChooser.TemplateButtonBuilder;
import ob1.eventmanager.entity.CategoryEntity;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.TemplateEntity;
import ob1.eventmanager.entity.TemplateQuestionEntity;
import ob1.eventmanager.exception.TemplateNotFoundException;
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

import java.util.List;
import java.util.Objects;

@Component("localEditEventTemplateQuestionsHandler")
public class EditEventTemplateQuestionsHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Autowired
    private EventService eventService;

    @Autowired
    private TemplateService templateService;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        EventEntity event = context.get("event");
        final CategoryEntity category = event.getCategory();
        final String text = context.get("text");
        final String chatId = context.get("chatId");
        final int messageId = context.get("messageId");

        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == LocalChatStates.EDIT_EVENT_TEMPLATE) {
            final SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            StringBuilder str = new StringBuilder();
            str.append("Вопросы: ");
            for (TemplateQuestionEntity question : event.getTemplate().getQuestions()) {
                str.append("\n").append(question.getQuestion());
            }
            sendMessage.setText(str.toString());
            sendMessage.setReplyMarkup(KeyboardUtils.inlineOf(
                    KeyboardUtils.buttonOf("Устраивают", "success"),
                    KeyboardUtils.buttonOf("Не устраивают", "fail"),
                    KeyboardUtils.buttonOf("Отменить", "cancel")));
            bot.send(sendMessage);
        } else if (previousState == LocalChatStates.EDIT_EVENT_TEMPLATE_QUESTION) {
            final String callbackData = context.get("callbackData");
            if (Objects.equals(callbackData, "cancel")) {
                context.setNextState(LocalChatStates.EDIT_EVENT_SHOW);
            } else if (Objects.equals(callbackData, "success")) {
                event = templateService.copyTemplateToEvent(event.getTemplate(), event);
                context.getHeaders().put("event", event);
                context.setNextState(LocalChatStates.EDIT_EVENT_SHOW);
            } else if (Objects.equals(callbackData, "fail")) {
                throw new UnsupportedOperationException("edit not implemented"); //TODO
            }
        }
        else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}
