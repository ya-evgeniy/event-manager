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
                .node(LocalChatStates.NEW, context.getBean("eventNewHandler", requiredType))
                .node(LocalChatStates.NAME, context.getBean("eventNameHandler", requiredType))
                .node(LocalChatStates.DATE, context.getBean("eventDateHandler", requiredType))
                .node(LocalChatStates.PLACE, context.getBean("eventPlaceHandler", requiredType))
                .node(LocalChatStates.CATEGORY, context.getBean("eventCategoryHandler", requiredType))
                .node(LocalChatStates.TEMPLATE, context.getBean("eventTemplateHandler", requiredType))
                .node(LocalChatStates.TEMPLATE_QUESTIONS, context.getBean("eventTemplateQuestionHandler", requiredType))
                .node(LocalChatStates.CREATE_CONFIRM, context.getBean("eventCreateConfirmHandler", requiredType))
                .node(LocalChatStates.LISTEN_MEMBERS, context.getBean("eventListenMembersHandler", requiredType))
                .node(LocalChatStates.LEAVE_FROM_CHAT, context.getBean("eventLeaveFromChatHandler", requiredType))
                .build();
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> cast(Class<?> type) {
        return (Class<T>) type;
    }

}


