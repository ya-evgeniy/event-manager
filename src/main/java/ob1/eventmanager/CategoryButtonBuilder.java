package ob1.eventmanager;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class CategoryButtonBuilder {
    public InlineKeyboardMarkup buildCategoryButtons(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();

        List<InlineKeyboardButton> firstLineButton = new ArrayList<>();
        List<InlineKeyboardButton> secondLineButton = new ArrayList<>();

        InlineKeyboardButton categoryOneButton = new InlineKeyboardButton();
        InlineKeyboardButton categoryTwoButton = new InlineKeyboardButton();

        categoryOneButton.setText("Первая прекрасная категория");
        categoryTwoButton.setText("Вторая прекрасная категория");

        categoryOneButton.setCallbackData("firstCategory");
        categoryTwoButton.setCallbackData("secondCategory");

        firstLineButton.add(categoryOneButton);
        secondLineButton.add(categoryTwoButton);

        inlineButtons.add(firstLineButton);
        inlineButtons.add(secondLineButton);

        inlineKeyboardMarkup.setKeyboard(inlineButtons);

        return inlineKeyboardMarkup;
    }
}
