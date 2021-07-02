package ob1.eventmanager.utils;

import lombok.Getter;

@Getter
public enum MemberStatus {

    WAIT_PRIVATE_MESSAGE("Ожидание личного сообщения"),
    CONFIRM("Подтвердил участие"),
    THINK("Возможно будет участвовать"),
    CANCEL("Отменил участие"),
    LEAVE("Вышел и чата"),
    FILL_QUESTIONS("Заполняет вопросы");

    private final String localized;

    MemberStatus(String s) {
        this.localized = s;
    }
}
