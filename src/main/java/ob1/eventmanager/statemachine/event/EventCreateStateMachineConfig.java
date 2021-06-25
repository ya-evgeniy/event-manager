package ob1.eventmanager.statemachine.event;

import lombok.extern.slf4j.Slf4j;
import ob1.eventmanager.statemachine.event.action.EventCreateActions;
import ob1.eventmanager.statemachine.event.action.EventDescriptionActions;
import ob1.eventmanager.statemachine.event.action.EventQuestionActions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Slf4j
@Configuration
@EnableStateMachineFactory
class EventCreateStateMachineConfig extends EnumStateMachineConfigurerAdapter<EventStates, EventEvents> {

    @Autowired
    private EventDescriptionActions baseDescriptionActions;

    @Autowired
    private EventQuestionActions questionActions;

    @Autowired
    private EventCreateActions createActions;

    @Override
    public void configure(
            StateMachineConfigurationConfigurer<EventStates, EventEvents> config)
            throws Exception {

        config.withConfiguration()
                .autoStartup(false);
    }

    @Override
    public void configure(
            StateMachineStateConfigurer<EventStates, EventEvents> states)
            throws Exception {

        states.withStates()
                .initial(EventStates.NEW)
                .states(EnumSet.allOf(EventStates.class))
                .end(EventStates.LISTEN_MEMBERS);
    }

    @Override
    public void configure(
            StateMachineTransitionConfigurer<EventStates, EventEvents> transitions)
            throws Exception {

        transitions.withExternal()
                .source(EventStates.NEW)
                .target(EventStates.NAME)
                .event(EventEvents.STARTED)
                .action(baseDescriptionActions::waitName)
                .and()

                .withExternal()
                .source(EventStates.NAME)
                .target(EventStates.DATE)
                .event(EventEvents.NAME_TYPED)
                .action(baseDescriptionActions::waitDate)
                .and()

                .withExternal()
                .source(EventStates.DATE)
                .target(EventStates.PLACE)
                .event(EventEvents.DATE_TYPED)
                .action(baseDescriptionActions::waitPlace)
                .and()

                .withExternal()
                .source(EventStates.PLACE)
                .target(EventStates.CATEGORY)
                .event(EventEvents.PLACE_TYPED)
                .action(questionActions::waitCategory)
                .and()

                .withExternal()
                .source(EventStates.CATEGORY)
                .target(EventStates.CATEGORY)
                .event(EventEvents.CATEGORY_CHECK)
                .action(questionActions::categoryCheck)
                .and()

                .withExternal()
                .source(EventStates.CATEGORY)
                .target(EventStates.TEMPLATE)
                .event(EventEvents.CATEGORY_IS_INCORRECT)
//                .action(questionActions::waitTemplate) // FIXME
                .and()

                .withExternal()
                .source(EventStates.CATEGORY)
                .target(EventStates.TEMPLATE)
                .event(EventEvents.CATEGORY_IS_CORRECT)
                .action(questionActions::waitTemplate)
                .and()

                .withExternal()
                .source(EventStates.TEMPLATE)
                .target(EventStates.TEMPLATE)
                .event(EventEvents.TEMPLATE_CHECK)
                .action(questionActions::templateCheck)
                .and()

                .withExternal()
                .source(EventStates.TEMPLATE)
                .target(EventStates.CATEGORY)
                .event(EventEvents.TEMPLATE_IS_INCORRECT)
                .action(questionActions::waitCategory)
                .and()

                .withExternal()
                .source(EventStates.TEMPLATE)
                .target(EventStates.TEMPLATE_QUESTIONS)
                .event(EventEvents.TEMPLATE_IS_CORRECT)
                .action(questionActions::waitQuestionsConfirm)
                .and()

                .withExternal()
                .source(EventStates.TEMPLATE_QUESTIONS)
                .target(EventStates.TEMPLATE_QUESTIONS)
                .event(EventEvents.TEMPLATE_QUESTIONS_CHECK)
                .action(questionActions::questionCheck)
                .and()

                .withExternal()
                .source(EventStates.TEMPLATE_QUESTIONS)
                .target(EventStates.TEMPLATE)
                .event(EventEvents.TEMPLATE_QUESTIONS_IS_INCORRECT)
                .action(questionActions::waitTemplate)
                .and()

                .withExternal()
                .source(EventStates.TEMPLATE_QUESTIONS)
                .target(EventStates.CREATE_CONFIRM)
                .event(EventEvents.TEMPLATE_QUESTIONS_IS_CORRECT)
                .action(createActions::waitConfirm)
                .and()

                .withExternal()
                .source(EventStates.CREATE_CONFIRM)
                .target(EventStates.CREATE_CONFIRM)
                .event(EventEvents.CREATE_CHECK)
                .action(createActions::createCheck)
                .and()

                .withExternal()
                .source(EventStates.CREATE_CONFIRM)
                .target(EventStates.LEAVE_FROM_CHAT)
                .event(EventEvents.CREATE_CONFIRM_DISCARD)
                .action(createActions::leaveFromChat)
                .and()

                .withExternal()
                .source(EventStates.CREATE_CONFIRM)
                .target(EventStates.LISTEN_MEMBERS)
                .event(EventEvents.CREATE_CONFIRM_CREATE)
                .action(createActions::listenMembers)
                .and();
    }
}


