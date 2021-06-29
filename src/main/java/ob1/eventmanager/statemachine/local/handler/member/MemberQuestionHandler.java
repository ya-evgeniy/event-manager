package ob1.eventmanager.statemachine.local.handler.member;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventQuestionEntity;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.service.MemberAnswerService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("localMemberQuestionHandler")
public class MemberQuestionHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Autowired
    private MemberAnswerService memberAnswerService;

    @Autowired
    private EventService eventService;

    private int prevQuestionIndex = 0;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        final String text = context.get("text");
        final String chatId = context.get("chatId");
        final String callbackQuery = context.get("callbackData");
        final int messageId = context.get("messageId");
        final MemberEntity member = new MemberEntity();//fixme добавить getMemberEntity()
//        List<EventQuestionEntity> questions = eventService.getGroupEvent(Long.parseLong(chatId)).getQuestions(); // fixme
        List<EventQuestionEntity> questions = new ArrayList<>();
        final String currentQuestion = questions.get(prevQuestionIndex).getQuestion();

        final LocalChatStates previousState = context.getPreviousState();

        if (previousState == LocalChatStates.MEMBER_DATE) {
            bot.send(currentQuestion,chatId);
            prevQuestionIndex+=1;
            //todo добавить вывод вопросов

        } else if (previousState == LocalChatStates.MEMBER_QUESTION) {

                context.setNextState(LocalChatStates.MEMBER_CONFIRM);
        } else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }
}
