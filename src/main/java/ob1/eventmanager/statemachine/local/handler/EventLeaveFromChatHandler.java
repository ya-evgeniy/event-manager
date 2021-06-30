package ob1.eventmanager.statemachine.local.handler;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component("eventLeaveFromChatHandler")
public class EventLeaveFromChatHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        System.out.println(context.getPreviousState() + " -> " + context.getCurrentState());

        final SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Пока!");
        sendMessage.setChatId(context.get("chatId"));

        bot.send(sendMessage);
        bot.leaveFromChat(context.get("chatId"));
    }

}
