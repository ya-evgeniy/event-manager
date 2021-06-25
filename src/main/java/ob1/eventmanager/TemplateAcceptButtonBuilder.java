package ob1.eventmanager;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class TemplateAcceptButtonBuilder {
    public InlineKeyboardMarkup buildTemplateAcceptButtons(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();

        List<InlineKeyboardButton> firstLineButton = new ArrayList<>();
        List<InlineKeyboardButton> backLineButton = new ArrayList<>();

        InlineKeyboardButton acceptButton = new InlineKeyboardButton();
        InlineKeyboardButton backButton = new InlineKeyboardButton();

        acceptButton.setText("Принять шаблон");
        backButton.setText("<<--");

        acceptButton.setCallbackData("acceptTemplate");
        backButton.setCallbackData("backToTemplateList");

        firstLineButton.add(acceptButton);
        backLineButton.add(backButton);

        inlineButtons.add(firstLineButton);
        inlineButtons.add(backLineButton);

        inlineKeyboardMarkup.setKeyboard(inlineButtons);

        return inlineKeyboardMarkup;
    }
}
