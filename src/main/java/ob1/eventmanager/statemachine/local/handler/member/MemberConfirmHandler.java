package ob1.eventmanager.statemachine.local.handler.member;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.EventQuestionEntity;
import ob1.eventmanager.entity.MemberAnswerEntity;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.service.MemberAnswerService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import ob1.eventmanager.utils.KeyboardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.Objects;
import java.util.Optional;

@Component("localMemberConfirmHandler")
public class MemberConfirmHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Autowired
    private MemberAnswerService memberAnswerService;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        final String chatId = context.get("chatId");
        final Integer messageId = context.get("messageId");
        EventEntity event = context.get("event");
        final MemberEntity member = context.get("member");
        final String callbackData = context.get("callbackData");

        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == LocalChatStates.MEMBER_QUESTION) {
            final EditMessageText editMessage = new EditMessageText();
            editMessage.setChatId(chatId);
            editMessage.setMessageId(messageId);
            editMessage.setText(getEventInfo(event, member));

            editMessage.setReplyMarkup(KeyboardUtils.inlineOf(
                    KeyboardUtils.buttonOf("Участвую", "success"),
                    KeyboardUtils.buttonOf("Не участвую", "cancel"),
                    KeyboardUtils.buttonOf("Подумаю", "think"),
                    KeyboardUtils.buttonOf("Изменить", "edit")
            ));

            bot.send(editMessage);
        } else if (previousState == LocalChatStates.MEMBER_CONFIRM) {
            if (Objects.equals(callbackData, "success")) {
                final EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setMessageId(messageId);

                editMessage.setText(String.format(
                        "%s\n\n<b>Участие подтверждено!</b>",
                        getEventInfo(event, member)
                ));
                editMessage.enableHtml(true);

                bot.send(editMessage);

                context.setNextState(LocalChatStates.WAIT_COMMANDS);
            }
            else if (Objects.equals(callbackData, "cancel")) {
                final EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setMessageId(messageId);

                editMessage.setText(String.format(
                        "%s\n\n<b>Участие отклонено!</b>",
                        getEventInfo(event, member)
                ));
                editMessage.enableHtml(true);

                bot.send(editMessage);

                final KickChatMember kickMember = new KickChatMember(
                        String.valueOf(event.getChatId()),
                        member.getUser().getTelegramId()
                );
                bot.send(kickMember);

                context.setNextState(LocalChatStates.WAIT_COMMANDS);
            }
            else if (Objects.equals(callbackData, "think")) {
                throw new UnsupportedOperationException("not impl yet");
            }
            else if (Objects.equals(callbackData, "edit")) {
                throw new UnsupportedOperationException("not impl yet");
            }
            else {
                throw new UnsupportedOperationException(callbackData);
            }
        } else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

    private String getEventInfo(EventEntity event, MemberEntity member) {
        final StringBuilder builder = new StringBuilder();
        builder.append("Название: ").append(event.getName()).append("\n");
        builder.append("Место: ").append(event.getPlace()).append("\n");
        builder.append("Время: ").append(event.getDate()).append("\n\n");
        builder.append("Вопросы: ");

        for (EventQuestionEntity question : event.getQuestions()) {
            builder.append("\n").append(question.getQuestion());
            final Optional<MemberAnswerEntity> optAnswer = memberAnswerService.getAnswer(member, question);
            if (optAnswer.isPresent()) {
                final MemberAnswerEntity answer = optAnswer.get();
                builder.append("\n").append(" - ").append(answer.getAnswer());
            }
        }
        return builder.toString();
    }

}
