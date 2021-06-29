package ob1.eventmanager.statemachine.local;

import java.util.Optional;

public enum LocalChatStates {
    START,
    CHECK_ACTUAL_EVENTS,
    WAIT_COMMANDS,

    EVENT_CREATE,
    EVENT_NAME,
    EVENT_PLACE,
    EVENT_DATE,
    EVENT_CATEGORY,
    EVENT_TEMPLATE,
    EVENT_TEMPLATE_QUESTION,
    EVENT_CONFIRM,

    MEMBER_INFO,
    MEMBER_PLACE,
    MEMBER_PLACE_EDIT,
    MEMBER_DATE,
    MEMBER_QUESTION,
    MEMBER_CONFIRM;

    public static Optional<LocalChatStates> getByName(String name) {
        if (name == null) return Optional.empty();

        final String stateName = name.toUpperCase();
        try {
            return Optional.of(LocalChatStates.valueOf(stateName));
        }
        catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

}
