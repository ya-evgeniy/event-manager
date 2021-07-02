package ob1.eventmanager.statemachine.local.handler.member;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.Collections;

public class MemberConfirmationButtonBuilder {
    public static InlineKeyboardMarkup build() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        markup.setKeyboard(Arrays.asList(
                Collections.singletonList(buttonOf("Да", "confirm")),
                Collections.singletonList(buttonOf("Нет", "edit"))
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
