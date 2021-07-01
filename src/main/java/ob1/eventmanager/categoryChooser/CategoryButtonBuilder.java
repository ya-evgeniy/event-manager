package ob1.eventmanager.categoryChooser;

import ob1.eventmanager.entity.CategoryEntity;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class CategoryButtonBuilder {
    public InlineKeyboardMarkup buildCategoryButtons(List<CategoryEntity> categoryEntities){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();

        for (CategoryEntity category:categoryEntities) {
            List<InlineKeyboardButton> lineButton = new ArrayList<>();
            InlineKeyboardButton categoryButton = new InlineKeyboardButton();

            categoryButton.setText(category.getName());

            categoryButton.setCallbackData("cat" + category.getId());

            lineButton.add(categoryButton);
            inlineButtons.add(lineButton);
        }

        inlineKeyboardMarkup.setKeyboard(inlineButtons);

        return inlineKeyboardMarkup;
    }


    public InlineKeyboardMarkup buildCategoryButtonsWithCancelButton(List<CategoryEntity> categoryEntities){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();

        for (CategoryEntity category:categoryEntities) {
            List<InlineKeyboardButton> lineButton = new ArrayList<>();
            InlineKeyboardButton categoryButton = new InlineKeyboardButton();

            categoryButton.setText(category.getName());

            categoryButton.setCallbackData("cat" + category.getId());

            lineButton.add(categoryButton);
            inlineButtons.add(lineButton);
        }
        List<InlineKeyboardButton> lineButton = new ArrayList<>();
        InlineKeyboardButton cancelButton = new InlineKeyboardButton();
        cancelButton.setText("Отменить");
        cancelButton.setCallbackData("cancel");
        lineButton.add(cancelButton);
        inlineButtons.add(lineButton);

        inlineKeyboardMarkup.setKeyboard(inlineButtons);

        return inlineKeyboardMarkup;
    }
}
