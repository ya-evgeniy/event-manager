package ob1.eventmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Component
public class Bot extends TelegramLongPollingBot {

    @Value("Event Confirmation")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            final SendMessage sendMessage = new SendMessage();

            final Message message = update.getMessage();
            final String text = message.getText();
            sendMessage.setText(text);
            sendMessage.setChatId(String.valueOf(message.getChatId()));


            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
            List<InlineKeyboardButton> inlineKeyboardButtonList = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
            InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
            inlineKeyboardButton1.setText("Все верно");
            inlineKeyboardButton2.setText("Отменить");
            inlineKeyboardButton1.setCallbackData("confirmation");
            inlineKeyboardButton2.setCallbackData("cancellation");
            inlineKeyboardButtonList.add(inlineKeyboardButton1);
            inlineKeyboardButtonList.add(inlineKeyboardButton2);

            inlineButtons.add(inlineKeyboardButtonList);

            inlineKeyboardMarkup.setKeyboard(inlineButtons);

            sendMessage.setReplyMarkup(inlineKeyboardMarkup);

            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.hasCallbackQuery()) {
            final EditMessageText editMessageText = new EditMessageText();

            final SendMessage sendMessage = new SendMessage();
            final Message message = update.getCallbackQuery().getMessage();

            editMessageText.setChatId(String.valueOf(message.getChatId()));

            sendMessage.setChatId(String.valueOf(message.getChatId()));
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            if (data.equals("confirmation")) {
                sendMessage.setText("Мероприятние подтверждено!");
                editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                editMessageText.setText("Мероприятние подтверждено!");
            } else if (data.equals("cancellation")) {
                sendMessage.setText("Мероприятие отменено!");
                editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                editMessageText.setText("Мероприятие отменено!");
            }

            try {
                execute(editMessageText);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
//        final EventConfirmation eventConfirmation = new EventConfirmation();
//
//        final KeyboardRow keyboardButtons = eventConfirmation.showButtons();
//        final KeyboardRow keyboardButtons = new KeyboardRow();
//        keyboardButtons.add("всё верно ✔️");
//        keyboardButtons.add("отменить");


//        sendMessage.setReplyMarkup(new ReplyKeyboardMarkup(
//                Arrays.asList(
//                        keyboardButtons
//                )
//        ));

    }

}
