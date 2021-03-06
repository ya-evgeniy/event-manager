package ob1.eventmanager.statemachine.local;

public enum EventEvents {
    STARTED,

    NAME_TYPED,
    @Deprecated NAME_CHECK,
    @Deprecated NAME_IS_CORRECT,
    @Deprecated NAME_IS_INCORRECT,

    DATE_TYPED,
    @Deprecated DATE_CHECK,
    @Deprecated DATE_IS_CORRECT,
    @Deprecated DATE_IS_INCORRECT,

    PLACE_TYPED,
    LOCATION_CHECK,
    LOCATION_IS_CORRECT,
    LOCATION_IS_INCORRECT,

    SELECT_CATEGORY,
    CATEGORY_CHECK,
    CATEGORY_IS_CORRECT,
    CATEGORY_IS_INCORRECT,

    SELECT_TEMPLATE,
    TEMPLATE_CHECK,
    TEMPLATE_IS_CORRECT,
    TEMPLATE_IS_INCORRECT,

    VIEW_QUESTIONS,
    TEMPLATE_QUESTIONS_WERE_SHOWN,
    TEMPLATE_QUESTIONS_CHECK,
    TEMPLATE_QUESTIONS_IS_CORRECT,
    TEMPLATE_QUESTIONS_IS_INCORRECT,
    TEMPLATE_EDIT,

    CREATE_CHECK,
    CREATE_CONFIRM_CREATE,
    CREATE_CONFIRM_DISCARD,
    CREATE_CONFIRM_EDIT,

    FINISHED
}
