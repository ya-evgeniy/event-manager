package ob1.eventmanager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class Bot extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
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
        InlineKeyboardMarkup categoryKeyboardMarkup;
        InlineKeyboardMarkup templateKeyboardMarkup = new TemplateButtonBuilder().buildTemplateButtons();
        InlineKeyboardMarkup templateAcceptKeyboardMarkup = new TemplateAcceptButtonBuilder().buildTemplateAcceptButtons();

        if (update.hasMessage()) {
            final SendMessage sendMessage = new SendMessage();
            final Message message = update.getMessage();
            final String text = message.getText();
            sendMessage.setText(text);
            sendMessage.setChatId(String.valueOf(message.getChatId()));

            categoryKeyboardMarkup = new CategoryButtonBuilder().buildCategoryButtons();
            sendMessage.setReplyMarkup(categoryKeyboardMarkup);

            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.hasCallbackQuery()) {
            final EditMessageText editMessageText = new EditMessageText();
            final Message message = update.getCallbackQuery().getMessage();

            editMessageText.setChatId(String.valueOf(message.getChatId()));

            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();

            /** Псевдокодик
             * А - выбранная категория
             * Б - выбранный шаблон
             *
             * Если А = null, то
             *      Для всех категорий из списка категорий(а)
             *          Если data.equals(cat + а.getId), то
             *              А = текущая категория
             *              Изменить текст сообщения на выбранную категорию
             *              Сгенерировать кнопки шаблонов для выбранной категории
             *          конец если
             *      конец для всех категорий
             * Иначе если А != null и Б = null, то
             *      Если data.equals(backToCat), то
             *          А = null
             *          Б = null
             *          Изменить текст сообщения на выбор категорий
             *          Сгенерировать кнопки категорий
             *      Иначе
             *      Для всех шаблонов из списка шаблонов по категории(б)
             *          Если data.equals(temp + б.getId), то
             *              Б = текущий шаблон
             *              Изменить текст сообщения на выбранный шаблон
             *              Сгенерировать кнопки подтверждения шаблона
             *          конец если
             *      конец для всех шаблонов
             * Иначе
             *     Если data.equals(backToTemp), то
             *          Б = null
             *          Изменить текст сообщения на выбранную категорию
             *          Сгенерировать кнопки шаблонов для выбранной категории
             *     Иначе если data.equals(accept), то
             *          Вывести сообщение о подтверждении шаблона
             */



//            if (data.equals("firstCategory")) {
//                editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
//                editMessageText.setText("Выбрана категория: первая замечательная. Список доступных шаблонов:");
//                editMessageText.setReplyMarkup(templateKeyboardMarkup);
//            } else if (data.equals("secondCategory")) {
//                editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
//                editMessageText.setText("Выбрана категория: вторая замечательная. Список доступных шаблонов:");
//                editMessageText.setReplyMarkup(templateKeyboardMarkup);
//            }
//            else if (data.equals("firstTemplate")) {
//                editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
//                editMessageText.setText("Вопросы шаблона: ...");
//                editMessageText.setReplyMarkup(templateAcceptKeyboardMarkup);
//            }
//
//            else if (data.equals("secondTemplate")) {
//                editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
//                editMessageText.setText("Вопросы шаблона: ...");
//                editMessageText.setReplyMarkup(templateAcceptKeyboardMarkup);
//            }
//
//            else if (data.equals("backToCategoryList")) {
//                editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
//                editMessageText.setText("Выбор категории: ");
//                categoryKeyboardMarkup = new CategoryButtonBuilder().buildCategoryButtons();
//                editMessageText.setReplyMarkup(categoryKeyboardMarkup);
//            }
//
//            else if (data.equals("acceptTemplate")) {
//                editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
//                editMessageText.setText("Выбран шаблон: ");
//            }
//            else if (data.equals("backToTemplateList")) {
//                editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
//                editMessageText.setText("Выбор шаблона для категории: ");
//                editMessageText.setReplyMarkup(templateKeyboardMarkup);
//            }

            try {
                execute(editMessageText);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

}
