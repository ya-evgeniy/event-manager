package ob1.eventmanager.statemachine.local.handler.member;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.EventQuestionEntity;
import ob1.eventmanager.entity.MemberAnswerEntity;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.service.MemberAnswerService;
import ob1.eventmanager.service.MemberService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import ob1.eventmanager.utils.KeyboardUtils;
import ob1.eventmanager.utils.MemberStatus;
import ob1.eventmanager.utils.ObjectsToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.Objects;
import java.util.Optional;

@Component("localMemberConfirmHandler")
public class MemberConfirmHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Autowired
    private MemberAnswerService memberAnswerService;

    @Autowired
    private MemberService memberService;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        final String chatId = context.get("chatId");
        final Integer messageId = context.get("messageId");
        EventEntity event = context.get("event");
        MemberEntity member = context.get("member");
        final String callbackData = context.get("callbackData");

        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == LocalChatStates.MEMBER_QUESTION) {
            final SendMessage editMessage = new SendMessage();
            editMessage.setChatId(chatId);
//            editMessage.setMessageId(messageId);
            editMessage.setText(getEventInfo(event, member));

            editMessage.setReplyMarkup(KeyboardUtils.inlineOf(
                    KeyboardUtils.buttonOf("Участвую", "success"),
                    KeyboardUtils.buttonOf("Не участвую", "cancel"),
                    KeyboardUtils.buttonOf("Возможно буду участвовать", "think")
//                    KeyboardUtils.buttonOf("Изменить", "edit")
            ));

            bot.send(editMessage);
        } else if (previousState == LocalChatStates.MEMBER_CONFIRM) {
            if (callbackData == null) {
                bot.send("Выбери из того что есть", chatId);
                return;
            }
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

                memberService.setStatus(member, MemberStatus.CONFIRM);
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

                memberService.setStatus(member, MemberStatus.CANCEL);
                context.setNextState(LocalChatStates.WAIT_COMMANDS);
            }
            else if (Objects.equals(callbackData, "think")) {
                final EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setMessageId(messageId);

                editMessage.setText(String.format(
                        "%s\n\n<b>Участие возможно!</b>",
                        getEventInfo(event, member)
                ));
                editMessage.enableHtml(true);

                bot.send(editMessage);

                memberService.setStatus(member, MemberStatus.THINK);
                context.setNextState(LocalChatStates.WAIT_COMMANDS);
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
        builder.append("Место: ").append(event.getPlace());
        builder.append("Дата: ").append(ObjectsToString.date(event.getDate())).append("\n");
        builder.append("Время: ").append(ObjectsToString.time(event.getTime())).append("\n\n");

        if (member.getComfortPlace() != null || member.getComfortDate() != null || member.getComfortTime() != null) {

            builder.append("Удобные для тебя параметры:\n");

            if (member.getComfortPlace() != null) {
                builder.append("    Место: ").append(member.getComfortPlace()).append("\n");
            }
            if (member.getComfortDate() != null) {
                builder.append("    Дата: ").append(ObjectsToString.date(member.getComfortDate())).append("\n");
            }
            if (member.getComfortTime() != null) {
                builder.append("    Время: ").append(ObjectsToString.time(member.getComfortTime())).append("\n");
            }

            builder.append("\n");
        }

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
