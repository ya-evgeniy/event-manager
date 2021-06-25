package ob1.eventmanager;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class TemplateButtonBuilder {
    public InlineKeyboardMarkup buildTemplateButtons(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();

        List<InlineKeyboardButton> firstLineButton = new ArrayList<>();
        List<InlineKeyboardButton> secondLineButton = new ArrayList<>();
        List<InlineKeyboardButton> backLineButton = new ArrayList<>();

        InlineKeyboardButton templateOneButton = new InlineKeyboardButton();
        InlineKeyboardButton templateTwoButton = new InlineKeyboardButton();
        InlineKeyboardButton backButton = new InlineKeyboardButton();

        templateOneButton.setText("Шаблон 1");
        templateTwoButton.setText("Шаблон 2");
        backButton.setText("<<--");

        templateOneButton.setCallbackData("firstTemplate");
        templateTwoButton.setCallbackData("secondTemplate");
        backButton.setCallbackData("backToCategoryList");

        firstLineButton.add(templateOneButton);
        secondLineButton.add(templateTwoButton);
        backLineButton.add(backButton);

        inlineButtons.add(firstLineButton);
        inlineButtons.add(secondLineButton);
        inlineButtons.add(backLineButton);

        inlineKeyboardMarkup.setKeyboard(inlineButtons);

        return inlineKeyboardMarkup;
    }
}
