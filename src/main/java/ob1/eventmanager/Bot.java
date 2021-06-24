package ob1.eventmanager;

import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
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

    final Stack<DeleteMessage> deleteMessageList = new Stack<>();


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
            final DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setMessageId(update.getMessage().getMessageId());
            deleteMessage.setChatId(String.valueOf(update.getMessage().getChatId()));

//            deleteMessageList.add(deleteMessage);
            deleteMessageList.push(deleteMessage);


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
            inlineKeyboardButton1.setText("Все верно ✅");
            inlineKeyboardButton2.setText("Отменить ❌");
            inlineKeyboardButton1.setCallbackData("confirmation");
            inlineKeyboardButton2.setCallbackData("cancellation");
            inlineKeyboardButtonList.add(inlineKeyboardButton1);
            inlineKeyboardButtonList.add(inlineKeyboardButton2);


            InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
            inlineKeyboardButton3.setText("Редактировать ✏️");
            inlineKeyboardButton3.setCallbackData("edit");
            List<InlineKeyboardButton> inlineKeyboardButtonList2 = new ArrayList<>();
//
            inlineKeyboardButtonList2.add(inlineKeyboardButton3);

            inlineButtons.add(inlineKeyboardButtonList);
            inlineButtons.add(inlineKeyboardButtonList2);

            inlineKeyboardMarkup.setKeyboard(inlineButtons);

            sendMessage.setReplyMarkup(inlineKeyboardMarkup);

            try {
                Message msg = new Message();
                msg = execute(sendMessage);
                DeleteMessage deleteMessage1 = new DeleteMessage();
                deleteMessage1.setMessageId(msg.getMessageId());
                deleteMessage1.setChatId(String.valueOf(msg.getChatId()));
                deleteMessageList.push(deleteMessage1);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.hasCallbackQuery()) {

            final DeleteMessage deleteMessage = new DeleteMessage();

            final EditMessageText editMessageText = new EditMessageText();

            final SendMessage sendMessage = new SendMessage();
            final Message message = update.getCallbackQuery().getMessage();


            editMessageText.setChatId(String.valueOf(message.getChatId()));
            editMessageText.enableHtml(true);

            sendMessage.setChatId(String.valueOf(message.getChatId()));
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            if (data.equals("confirmation")) {

                deleteMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                deleteMessage.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));

                sendMessage.setText("Мероприятие подтверждено!");


                editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                editMessageText.setText(message.getText() + "\n<b>Мероприятие подтверждено!</b>");

            } else if (data.equals("cancellation")) {
                sendMessage.setText("Мероприятие отменено!");

                editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                editMessageText.setText(message.getText() + "\n<b>Мероприятие отменено!</b>");
            }

            try {
//                execute(sendMessage);
//                execute(editMessageText);
//                execute(deleteMessage);
                Stack<DeleteMessage> deleteExceptLast = new Stack<>();
                while (!deleteMessageList.isEmpty()) {
                    deleteExceptLast.push(deleteMessageList.pop());
                }


                while (deleteExceptLast.size() > 1) {
                    execute(deleteExceptLast.pop());
                }
//                System.out.println(deleteMessageList);
                execute(editMessageText);

            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }

}
