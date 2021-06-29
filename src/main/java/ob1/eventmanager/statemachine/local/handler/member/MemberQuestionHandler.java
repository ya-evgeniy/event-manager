package ob1.eventmanager.statemachine.local.handler.member;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventQuestionEntity;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.service.EventQuestionService;
import ob1.eventmanager.service.MemberAnswerService;
import ob1.eventmanager.service.MemberService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("localMemberQuestionHandler")
public class MemberQuestionHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Autowired
    private MemberAnswerService memberAnswerService;

    @Autowired
    private EventQuestionService eventQuestionService;

    @Autowired
    private MemberService memberService;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        final String text = context.get("text");
        final String chatId = context.get("chatId");
        final MemberEntity member = new MemberEntity();//fixme from context

        final LocalChatStates previousState = context.getPreviousState();

        if (previousState != LocalChatStates.MEMBER_DATE && previousState != LocalChatStates.MEMBER_QUESTION) {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
        if(member.getCurrentQuestion()!=null){
            memberAnswerService.setAnswer(member,text);
        }
        Optional<EventQuestionEntity> currentQuestion = eventQuestionService.getUnansweredQuestion(member);
        if (currentQuestion != null) {
            bot.send(currentQuestion.get().getQuestion(), chatId);
        } else {
            bot.send("Ура, мой список вопросов закончился!", chatId);
            context.setNextState(LocalChatStates.MEMBER_CONFIRM);
        }
    }

}
