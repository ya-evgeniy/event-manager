package ob1.eventmanager.statemachine.event;

import lombok.Getter;

public enum EventStates {
    NEW,
    NAME(EventEvents.NAME_TYPED),
    DATE(EventEvents.DATE_TYPED),
    PLACE(EventEvents.PLACE_TYPED),
    CATEGORY(EventEvents.CATEGORY_CHECK),
    TEMPLATE(EventEvents.TEMPLATE_CHECK),
    TEMPLATE_QUESTIONS(EventEvents.TEMPLATE_QUESTIONS_CHECK),
    CREATE_CONFIRM(EventEvents.CREATE_CHECK),
    LISTEN_MEMBERS(),
    LEAVE_FROM_CHAT();

    @Getter
    private final EventEvents event;

    EventStates() {
        this(null);
    }

    EventStates(EventEvents event) {
        this.event = event;
    }

}
