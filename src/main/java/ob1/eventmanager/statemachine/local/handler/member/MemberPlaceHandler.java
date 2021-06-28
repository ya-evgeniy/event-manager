package ob1.eventmanager.statemachine.local.handler.member;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.service.MemberAnswerService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Component("memberPlaceHandler")
public class MemberPlaceHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;
    @Autowired
    private EventService eventService;
    @Autowired
    private MemberAnswerService memberAnswerService;


    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {

        System.out.println(context.getPreviousState() + " -> " + context.getCurrentState());
        final String text = context.get("text");
        final String chatId = context.get("chatId");
        final String callbackQuery = context.get("callbackData");
        final int messageId = context.get("messageId");
        final MemberEntity member = new MemberEntity();//fixme добавить getMemberEntity()
        String userPlace = eventService.getEvent(Long.parseLong(chatId)).getPlace();

        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == LocalChatStates.MEMBER_INFO) {

            final SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Мероприятие будет проходить здесь: " + userPlace + ". Устраивает ли вас место проведения?");
            sendMessage.setChatId(chatId);
            sendMessage.setReplyMarkup(MemberConfirmationButtonBuilder.build());
            memberAnswerService.setAnswer(member, text);
            bot.send(sendMessage);

        } else if (previousState == LocalChatStates.MEMBER_PLACE) {
            if (callbackQuery.equals("confirm")) {

                final EditMessageText editMessage = new EditMessageText();
                editMessage.setMessageId(messageId);
                editMessage.setChatId(chatId);
                editMessage.setText("Подтверждено!");

                bot.send(editMessage);
                context.setNextState(LocalChatStates.MEMBER_DATE);

            } else if (callbackQuery.equals("edit")) {
                context.setNextState(LocalChatStates.MEMBER_PLACE_EDIT);
            }
        } else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}
