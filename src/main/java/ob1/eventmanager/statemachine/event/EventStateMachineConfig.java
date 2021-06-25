package ob1.eventmanager.statemachine.event;

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

    @Bean
    public MessageStateMachineFactory<EventStates> eventStateMachine() {
        final Class<MessageStateMachineHandler<EventStates>> requiredType = cast(MessageStateMachineHandler.class);

        return MessageStateMachineFactory.builder(EventStates.class)
                .node(EventStates.NEW, context.getBean("eventNewHandler", requiredType))
                .node(EventStates.NAME, context.getBean("eventNameHandler", requiredType))
                .node(EventStates.DATE, context.getBean("eventDateHandler", requiredType))
                .node(EventStates.PLACE, context.getBean("eventPlaceHandler", requiredType))
                .node(EventStates.CATEGORY, context.getBean("eventCategoryHandler", requiredType))
                .node(EventStates.TEMPLATE, context.getBean("eventTemplateHandler", requiredType))
                .node(EventStates.TEMPLATE_QUESTIONS, context.getBean("eventTemplateQuestionHandler", requiredType))
                .node(EventStates.CREATE_CONFIRM, context.getBean("eventCreateConfirmHandler", requiredType))
                .node(EventStates.LISTEN_MEMBERS, context.getBean("eventListenMembersHandler", requiredType))
                .node(EventStates.LEAVE_FROM_CHAT, context.getBean("eventLeaveFromChatHandler", requiredType))
                .build();
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> cast(Class<?> type) {
        return (Class<T>) type;
    }

}


