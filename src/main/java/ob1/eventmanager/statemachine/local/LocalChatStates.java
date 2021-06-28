package ob1.eventmanager.statemachine.local;

public enum LocalChatStates {
    START,
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
    MEMBER_DATE,
    MEMBER_QUESTION,
    MEMBER_CONFIRM,
}
