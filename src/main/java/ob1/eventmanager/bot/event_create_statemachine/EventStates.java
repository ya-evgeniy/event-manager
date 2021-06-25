package ob1.eventmanager.bot.event_create_statemachine;

public enum EventStates {
    NEW,
    NAME,
    NAME_CONFIRM,
    DATE,
    DATE_CONFIRM,
    LOCATION,
    LOCATION_CONFIRM,
    CATEGORY,
    TEMPLATE,
    TEMPLATE_QUESTIONS,
    TEMPLATE_QUESTIONS_CONFIRM,
    CREATE_CONFIRM,
    LISTEN_MEMBERS,
}
