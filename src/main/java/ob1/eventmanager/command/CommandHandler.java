package ob1.eventmanager.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class CommandHandler {
    private static String START_COMMAND = "/start";

    public static SendMessage parse(String text){
        final SendMessage sendMessage = new SendMessage();

        if(text.startsWith(START_COMMAND)){
            sendMessage.setText("Привет! Я - менеджер мероприятий. <>...");
        }

        return sendMessage;

    }
}
