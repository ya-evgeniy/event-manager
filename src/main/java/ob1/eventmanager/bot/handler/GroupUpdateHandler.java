package ob1.eventmanager.bot.handler;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.bot.TelegramUpdateHandler;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.service.MemberService;
import ob1.eventmanager.service.MessageStateMachineService;
import ob1.eventmanager.service.UserService;
import ob1.eventmanager.statemachine.MessageStateMachine;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import ob1.eventmanager.utils.MemberStatus;
import ob1.eventmanager.utils.ObjectsToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("telegramGroupUpdateHandler")
public class GroupUpdateHandler implements TelegramUpdateHandler {

    @Autowired
    private TelegramBot bot;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MessageStateMachineService stateMachineService;

    @Override
    public void handle(Update update) {
        if (!update.hasMessage()) return;

        final Message message = update.getMessage();
        final User from = message.getFrom();
        final Chat chat = message.getChat();

        if (from.getIsBot()) return;

        boolean isNewChannel = message.getGroupchatCreated() != null && message.getGroupchatCreated();

        if (isNewChannel) {
            handleMeInvited(update);
            return;
        }

        boolean hasInvitedUsers = message.getNewChatMembers() != null && !message.getNewChatMembers().isEmpty();
        if (hasInvitedUsers) {
            final Optional<Integer> optId = bot.getSelfId();
            if (optId.isEmpty()) return;

            final int id = optId.get();
            boolean meIsInvited = message.getNewChatMembers().stream()
                    .filter(User::getIsBot)
                    .anyMatch(user -> user.getId() == id);

            if (meIsInvited) {
                handleMeInvited(update);
            }

            if (!meIsInvited || message.getNewChatMembers().size() > 1) {
                handleUserInvite(update);
            }
            return;
        }

        boolean hasLeftUsers = message.getLeftChatMember() != null;
        if (hasLeftUsers) {
            final Optional<Integer> optId = bot.getSelfId();
            if (optId.isEmpty()) return;

            final int id = optId.get();
            boolean meIsInvited = message.getNewChatMembers().stream()
                    .filter(User::getIsBot)
                    .anyMatch(user -> user.getId() == id);

            boolean meIsLeft = message.getLeftChatMember() != null
                    && message.getLeftChatMember().getIsBot()
                    && message.getLeftChatMember().getId() == id;

            if (meIsLeft) {
                handleMeLeft(update);
            }
            else {
                handleUserLeft(update);
            }
            return;
        }

        boolean fromIsBot = from.getIsBot();
        if (!fromIsBot) {
            handleMessage(update);
            return;
        }
    }

    private void handleMeInvited(Update update) {
        final Message message = update.getMessage();
        final User from = message.getFrom();
        final Chat chat = message.getChat();
        final String chatIdString = String.valueOf(chat.getId());

        final int invitedUserId = from.getId();
        final Optional<UserEntity> optInvitedUser = userService.getUserByTelegramId(invitedUserId);

        if (optInvitedUser.isEmpty() || optInvitedUser.get().getSelectedEvent() == null) {
            bot.leaveFromChat(chatIdString);
            return;
        }

        final UserEntity invitedUser = optInvitedUser.get();
        EventEntity selectedEvent = invitedUser.getSelectedEvent();

        selectedEvent = eventService.setEventChatId(selectedEvent, chat.getId());
        selectedEvent = eventService.verifyEvent(selectedEvent);

        String builder =
                "Название: " + selectedEvent.getName() + "\n" +
                "Место: " + selectedEvent.getPlace() + "\n" +
                "Дата: " + ObjectsToString.date(selectedEvent.getDate()) + "\n" +
                "Время: " + ObjectsToString.time(selectedEvent.getTime()) +
                "\n\nПишите + в чат, кто зашел в чат до меня";

        final Message sentMessage = bot.send(builder, chatIdString);
        bot.pinMessage(chatIdString, sentMessage);
    }

    private void handleUserInvite(Update update) {
        final Message message = update.getMessage();
        final User from = message.getFrom();
        final Chat chat = message.getChat();
        final String chatIdString = String.valueOf(chat.getId());

        final Optional<EventEntity> optEvent = eventService.getGroupEvent(chat.getId());
        if (optEvent.isEmpty()) return;
        final EventEntity event = optEvent.get();

        handleUserInvite(event, chatIdString, update.getMessage().getNewChatMembers());
    }

    private void handleUserInvite(EventEntity event, String chatIdString, List<User> users) {
        final List<UserEntity> invitedUsers = users.stream()
                .filter(user -> !user.getIsBot())
                .filter(user -> user.getId() != event.getOwner().getTelegramId())
                .map(user -> {
                    final Optional<UserEntity> optUserEntity = userService.getUserByTelegramId(user.getId());
                    return optUserEntity.orElseGet(() -> userService.createUser(user.getId(), user.getUserName()));
                })
                .collect(Collectors.toList());

        for (UserEntity invitedUser : invitedUsers) {
            if (memberService.hasMember(invitedUser, event)) {
                MemberEntity member = memberService.getMember(invitedUser, event);
                member = memberService.setStatus(member, MemberStatus.WAIT_PRIVATE_MESSAGE);
                member = memberService.setAnnounceDate(member, null);
                member = memberService.setAnnounceCount(member, 1);
            }
            else {
                memberService.createMember(invitedUser, event);
            }
        }

        final List<UserEntity> invitedUsersWithChat = invitedUsers.stream()
                .filter(bot::hasChat)
                .collect(Collectors.toList());

        final List<UserEntity> invitedUsersWithoutChat = invitedUsers.stream()
                .filter(user -> !invitedUsersWithChat.contains(user))
                .collect(Collectors.toList());

        if (!invitedUsersWithoutChat.isEmpty()) {
            final SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatIdString);
            sendMessage.setParseMode(ParseMode.MARKDOWNV2);

            if (invitedUsersWithoutChat.size() == 1) {
                final UserEntity user = invitedUsersWithoutChat.get(0);

                sendMessage.setText(bot.getMarkdownMention(user) + ", приветствую тебя в чате мероприятия\\. Общаться тут неудобно, давай продолжим у меня в личном диалоге, я задам тебе пару вопросов\\.");
            }
            else {
                final StringBuilder builder = new StringBuilder();

                for (UserEntity user : invitedUsersWithoutChat) {
                    builder.append(bot.getMarkdownMention(user)).append(", ");
                }

                builder.append("приветствую вас в чате мероприятия\\. Общаться в групповом чате неудобно, давайте продолжим у меня в личном диалоге, я задам вам пару вопросов\\.");
                sendMessage.setText(builder.toString());
            }

            final LocalDateTime now = LocalDateTime.now();
            for (UserEntity user : invitedUsersWithoutChat) {
                final MemberEntity member = memberService.getMember(user, event);
                memberService.setAnnounceDate(member, now);
            }
            bot.send(sendMessage);
        }

        for (UserEntity user : invitedUsersWithChat) {
            final MessageStateMachine<LocalChatStates> stateMachine = stateMachineService.createLocal(user);
            if (stateMachine.getCurrentState() == LocalChatStates.WAIT_COMMANDS) {
                try {
                    final MemberEntity member = memberService.getMember(user, event);
                    memberService.setStatus(member, MemberStatus.FILL_QUESTIONS);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                bot.send("Привет, я заметил что ты присоеденился к мероприятию. Пиши /actual_events, выбирай мероприятие и отвечай на мои вопросы.", String.valueOf(user.getChatId()));
//                stateMachine.setCurrentState(LocalChatStates.CHECK_ACTUAL_EVENTS);
//
//                final Map<String, Object> headers = new HashMap<>();
//                headers.put("userId", user.getTelegramId());
//                headers.put("chatId", String.valueOf(user.getChatId()));
//
//                stateMachine.handle(headers);
            }
        }
    }

    private void handleMeLeft(Update update) {

    }

    private void handleUserLeft(Update update) {

    }

    private void handleMessage(Update update) {
        final Message message = update.getMessage();
        if (message == null) return;

        final User from = message.getFrom();
        final Chat chat = message.getChat();
        final String chatIdString = String.valueOf(chat.getId());

        final Optional<EventEntity> optEvent = eventService.getGroupEvent(chat.getId());
        if (optEvent.isEmpty()) return;
        final EventEntity event = optEvent.get();

        final Optional<UserEntity> optUserEntity = userService.getUserByTelegramId(from.getId());
        final UserEntity user = optUserEntity.orElseGet(() -> userService.createUser(from.getId(), from.getUserName()));

        if (memberService.hasMember(user, event)) {
            final MemberEntity member = memberService.getMember(user, event);
            if (member.getStatus() == MemberStatus.WAIT_PRIVATE_MESSAGE && member.getAnnounceCount() == 0) {
                handleUserInvite(event, chatIdString, Collections.singletonList(from));
            }
        }
        else {
            handleUserInvite(event, chatIdString, Collections.singletonList(from));
        }
    }

}
