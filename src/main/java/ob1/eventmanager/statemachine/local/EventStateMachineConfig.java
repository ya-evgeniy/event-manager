package ob1.eventmanager.statemachine.local;

import ob1.eventmanager.statemachine.MessageStateMachineFactory;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class EventStateMachineConfig {

    @Autowired
    private ApplicationContext context;

    @Bean("localStateMachineFactory")
    public MessageStateMachineFactory<LocalChatStates> eventStateMachine() {
        final Class<MessageStateMachineHandler<LocalChatStates>> requiredType = cast(MessageStateMachineHandler.class);

        return MessageStateMachineFactory.builder(LocalChatStates.class)
                .node(LocalChatStates.START, context.getBean("localStartHandler", requiredType))
                .node(LocalChatStates.CHECK_ACTUAL_EVENTS, context.getBean("localCheckActualEventsHandler", requiredType))
                .node(LocalChatStates.WAIT_COMMANDS, context.getBean("localWaitCommandsHandler", requiredType))

                .node(LocalChatStates.EVENT_CREATE, context.getBean("localEventCreateHandler", requiredType))
                .node(LocalChatStates.EVENT_NAME, context.getBean("localEventNameHandler", requiredType))
                .node(LocalChatStates.EVENT_PLACE, context.getBean("localEventPlaceHandler", requiredType))
                .node(LocalChatStates.EVENT_DATE, context.getBean("localEventDateHandler", requiredType))
                .node(LocalChatStates.EVENT_TIME, context.getBean("localEventTimeHandler", requiredType))
                .node(LocalChatStates.EVENT_CATEGORY, context.getBean("localEventCategoryHandler", requiredType))
                .node(LocalChatStates.EVENT_TEMPLATE, context.getBean("localEventTemplateHandler", requiredType))
                .node(LocalChatStates.EVENT_TEMPLATE_QUESTION, context.getBean("localEventTemplateQuestionHandler", requiredType))
                .node(LocalChatStates.EVENT_CONFIRM, context.getBean("localEventConfirmHandler", requiredType))

                .node(LocalChatStates.MEMBER_INFO, context.getBean("localMemberInfoHandler", requiredType))
                .node(LocalChatStates.MEMBER_PLACE, context.getBean("localMemberPlaceHandler", requiredType))
                .node(LocalChatStates.MEMBER_PLACE_EDIT, context.getBean("localMemberPlaceEditHandler", requiredType))
                .node(LocalChatStates.MEMBER_DATE, context.getBean("localMemberDateHandler", requiredType))
                .node(LocalChatStates.MEMBER_DATE_EDIT, context.getBean("localMemberDateEditHandler", requiredType))
                .node(LocalChatStates.MEMBER_TIME, context.getBean("localMemberTimeHandler", requiredType))
                .node(LocalChatStates.MEMBER_TIME_EDIT, context.getBean("localMemberTimeEditHandler", requiredType))
                .node(LocalChatStates.MEMBER_QUESTION, context.getBean("localMemberQuestionHandler", requiredType))
                .node(LocalChatStates.MEMBER_CONFIRM, context.getBean("localMemberConfirmHandler", requiredType))

                .node(LocalChatStates.EDIT_EVENT_SHOW, context.getBean("localEditEventShowHandler", requiredType))
                .node(LocalChatStates.EDIT_EVENT_NAME, context.getBean("localEditEventNameHandler", requiredType))
                .node(LocalChatStates.EDIT_EVENT_PLACE, context.getBean("localEditEventPlaceHandler", requiredType))
                .node(LocalChatStates.EDIT_EVENT_DATE, context.getBean("localEditEventDateHandler", requiredType))
                .node(LocalChatStates.EDIT_EVENT_TIME, context.getBean("localEditEventTimeHandler", requiredType))
                .node(LocalChatStates.EDIT_EVENT_CATEGORY, context.getBean("localEditEventCategoryHandler", requiredType))
                .node(LocalChatStates.EDIT_EVENT_TEMPLATE, context.getBean("localEditEventTemplateHandler", requiredType))
                .node(LocalChatStates.EDIT_EVENT_TEMPLATE_QUESTION, context.getBean("localEditEventTemplateQuestionsHandler", requiredType))

                .build();
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> cast(Class<?> type) {
        return (Class<T>) type;
    }

}


