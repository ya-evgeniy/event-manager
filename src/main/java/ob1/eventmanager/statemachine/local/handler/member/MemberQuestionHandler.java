package ob1.eventmanager.statemachine.local.handler.member;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.EventQuestionAnswerEntity;
import ob1.eventmanager.entity.EventQuestionEntity;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.service.EventQuestionService;
import ob1.eventmanager.service.MemberAnswerService;
import ob1.eventmanager.service.MemberService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import ob1.eventmanager.utils.KeyboardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        final String chatId = context.get("chatId");
        final int messageId = context.get("messageId");
        final String callbackData = context.get("callbackData");

        MemberEntity member = context.get("member");

        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == LocalChatStates.MEMBER_TIME || previousState == LocalChatStates.MEMBER_TIME_EDIT) {
            final Optional<EventQuestionEntity> optUnansweredQuestion = eventQuestionService.getUnansweredQuestion(member);
            if (optUnansweredQuestion.isEmpty()) {
                context.setNextState(LocalChatStates.MEMBER_CONFIRM);
                return;
            }

            final EventQuestionEntity unansweredQuestion = optUnansweredQuestion.get();
            member = memberService.setCurrentQuestion(member, unansweredQuestion);
            context.getHeaders().put("member", member);

            final SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(unansweredQuestion.getQuestion());

            final List<List<InlineKeyboardButton>> keyboard = unansweredQuestion.getAnswers().stream()
                    .map(EventQuestionAnswerEntity::getAnswer)
                    .map(answer -> KeyboardUtils.buttonOf(answer, answer))
                    .map(Collections::singletonList)
                    .collect(Collectors.toList());
            sendMessage.setReplyMarkup(new InlineKeyboardMarkup(keyboard));

            bot.send(sendMessage);
            return;
        }
        else if (previousState != LocalChatStates.MEMBER_DATE
                && previousState != LocalChatStates.MEMBER_QUESTION) {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }

        if (callbackData == null) {
            return;
        }

        final EventQuestionEntity currentQuestion = member.getCurrentQuestion();
        if (currentQuestion != null) {
            member = memberAnswerService.setAnswer(member, callbackData);
            context.getHeaders().put("member", member);
        }

        final Optional<EventQuestionEntity> optUnansweredQuestion = eventQuestionService.getUnansweredQuestion(member);
        if (optUnansweredQuestion.isEmpty()) {
            context.setNextState(LocalChatStates.MEMBER_CONFIRM);
            return;
        }

        final EventQuestionEntity unansweredQuestion = optUnansweredQuestion.get();
        member = memberService.setCurrentQuestion(member, unansweredQuestion);
        context.getHeaders().put("member", member);

        final SendMessage editMessage = new SendMessage();
        editMessage.setChatId(chatId);
//        editMessage.setMessageId(messageId);
        editMessage.setText(unansweredQuestion.getQuestion());

        final List<List<InlineKeyboardButton>> keyboard = unansweredQuestion.getAnswers().stream()
                .map(EventQuestionAnswerEntity::getAnswer)
                .map(answer -> KeyboardUtils.buttonOf(answer, answer))
                .map(Collections::singletonList)
                .collect(Collectors.toList());
        editMessage.setReplyMarkup(new InlineKeyboardMarkup(keyboard));

        bot.send(editMessage);
    }

}
