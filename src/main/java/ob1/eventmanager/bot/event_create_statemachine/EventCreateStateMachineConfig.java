package ob1.eventmanager.bot.event_create_statemachine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Slf4j
@Configuration
@EnableStateMachineFactory
class EventCreateStateMachineConfig extends EnumStateMachineConfigurerAdapter<EventStates, EventEvents> {

    @Override
    public void configure(
            StateMachineConfigurationConfigurer
                    <EventStates, EventEvents> config) throws Exception {

        config.withConfiguration()
                .autoStartup(true);
    }

    @Override
    public void configure(
            StateMachineStateConfigurer<EventStates, EventEvents> states)
            throws Exception {

        states.withStates()
                .initial(EventStates.NEW)
                .state(EventStates.NAME)
                .state(EventStates.NAME_CONFIRM)
                .state(EventStates.DATE)
                .state(EventStates.DATE_CONFIRM)
                .state(EventStates.LOCATION)
                .state(EventStates.LOCATION_CONFIRM)
                .state(EventStates.CATEGORY)
                .state(EventStates.TEMPLATE)
                .state(EventStates.TEMPLATE_QUESTIONS)
                .state(EventStates.TEMPLATE_QUESTIONS_CONFIRM)
                .state(EventStates.CREATE_CONFIRM)
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
                .and()

                .withExternal()
                .source(EventStates.NAME)
                .target(EventStates.NAME_CONFIRM)
                .event(EventEvents.NAME_TYPED)
                .and()

                .withExternal()
                .source(EventStates.NAME_CONFIRM)
                .target(EventStates.DATE)
                .event(EventEvents.NAME_IS_CORRECT)
                .and()

                .withExternal()
                .source(EventStates.NAME_CONFIRM)
                .target(EventStates.NAME)
                .event(EventEvents.NAME_IS_INCORRECT)
                .and()


                .withExternal()
                .source(EventStates.DATE)
                .target(EventStates.DATE_CONFIRM)
                .event(EventEvents.DATE_TYPED)
                .and()

                .withExternal()
                .source(EventStates.DATE_CONFIRM)
                .target(EventStates.DATE)
                .event(EventEvents.DATE_IS_INCORRECT)
                .and()

                .withExternal()
                .source(EventStates.DATE_CONFIRM)
                .target(EventStates.LOCATION)
                .event(EventEvents.DATE_IS_CORRECT)
                .and()


                .withExternal()
                .source(EventStates.LOCATION)
                .target(EventStates.LOCATION_CONFIRM)
                .event(EventEvents.LOCATION_TYPED)
                .and()

                .withExternal()
                .source(EventStates.LOCATION_CONFIRM)
                .target(EventStates.LOCATION)
                .event(EventEvents.LOCATION_IS_INCORRECT)
                .and()

                .withExternal()
                .source(EventStates.LOCATION_CONFIRM)
                .target(EventStates.CATEGORY)
                .event(EventEvents.LOCATION_IS_INCORRECT)
                .and()


                .withExternal()
                .source(EventStates.CATEGORY)
                .target(EventStates.TEMPLATE)
                .event(EventEvents.CATEGORY_IS_CORRECT)
                .and()


                .withExternal()
                .source(EventStates.TEMPLATE)
                .target(EventStates.TEMPLATE_QUESTIONS)
                .event(EventEvents.TEMPLATE_IS_CORRECT)
                .and()

                .withExternal()
                .source(EventStates.TEMPLATE_QUESTIONS)
                .target(EventStates.TEMPLATE_QUESTIONS_CONFIRM)
                .event(EventEvents.TEMPLATE_QUESTIONS_WERE_SHOWN)
                .and()

                .withExternal()
                .source(EventStates.TEMPLATE_QUESTIONS_CONFIRM)
                .target(EventStates.CREATE_CONFIRM)
                .event(EventEvents.TEMPLATE_QUESTIONS_IS_CORRECT)
                .and()

                .withExternal()
                .source(EventStates.TEMPLATE_QUESTIONS_CONFIRM)
                .target(EventStates.TEMPLATE)
                .event(EventEvents.TEMPLATE_QUESTIONS_IS_INCORRECT)
                .and()

                .withExternal()
                .source(EventStates.TEMPLATE_QUESTIONS_CONFIRM)
                .target(EventStates.TEMPLATE)
                .event(EventEvents.TEMPLATE_EDIT)
                .and()

                .withExternal()
                .source(EventStates.CREATE_CONFIRM)
                .target(EventStates.LISTEN_MEMBERS)
                .event(EventEvents.CREATE_CONFIRM_CREATE)
                .and()

                .withExternal()
                .source(EventStates.CREATE_CONFIRM)
                .target(EventStates.LISTEN_MEMBERS)
                .event(EventEvents.CREATE_CONFIRM_DISCARD);
    }
}


