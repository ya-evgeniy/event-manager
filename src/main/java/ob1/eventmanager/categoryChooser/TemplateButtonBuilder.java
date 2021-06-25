package ob1.eventmanager.categoryChooser;

import ob1.eventmanager.entity.TemplateEntity;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class TemplateButtonBuilder {
    public InlineKeyboardMarkup buildTemplateButtons(List<TemplateEntity> templateEntities){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();

        for (TemplateEntity template: templateEntities) {
            List<InlineKeyboardButton> lineButton = new ArrayList<>();
            InlineKeyboardButton templateButton = new InlineKeyboardButton();

            templateButton.setText(template.getName());
            templateButton.setCallbackData("temp" + template.getId());

            lineButton.add(templateButton);
            inlineButtons.add(lineButton);
        }

        List<InlineKeyboardButton> backLineButton = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();

        backButton.setText("<<--");
        backButton.setCallbackData("backToCategoryList");

        backLineButton.add(backButton);
        inlineButtons.add(backLineButton);

        inlineKeyboardMarkup.setKeyboard(inlineButtons);

        return inlineKeyboardMarkup;
    }
}
