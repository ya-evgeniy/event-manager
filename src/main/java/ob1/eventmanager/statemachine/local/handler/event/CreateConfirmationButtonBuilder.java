package ob1.eventmanager.statemachine.local.handler.event;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.Collections;

public class CreateConfirmationButtonBuilder {

    public static InlineKeyboardMarkup build() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        markup.setKeyboard(Arrays.asList(
                Collections.singletonList(buttonOf("Создать", "confirm")),
                Collections.singletonList(buttonOf("Отменить", "cancel")),
                Collections.singletonList(buttonOf("Редактировать", "edit"))
        ));

        return markup;
    }

    private static InlineKeyboardButton buttonOf(String text, String callbackData) {
        final InlineKeyboardButton result = new InlineKeyboardButton();
        result.setText(text);
        result.setCallbackData(callbackData);
        return result;
    }

}
