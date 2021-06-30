package ob1.eventmanager.bot.handler;

import ob1.eventmanager.bot.TelegramUpdateHandler;
import ob1.eventmanager.bot.ann.LocalCommand;
import ob1.eventmanager.bot.command.LocalCommandHandler;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.service.MemberService;
import ob1.eventmanager.service.MessageStateMachineService;
import ob1.eventmanager.service.UserService;
import ob1.eventmanager.statemachine.MessageStateMachine;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component("telegramLocalUpdateHandler")
public class LocalUpdateHandler implements TelegramUpdateHandler {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MessageStateMachineService stateMachineService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private MemberService memberService;

    private Map<String, LocalCommandHandler> localCommands = new HashMap<>();

    @PostConstruct
    private void init() {
        final Set<Map.Entry<String, LocalCommandHandler>> beans = applicationContext.getBeansOfType(LocalCommandHandler.class)
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().getClass().isAnnotationPresent(LocalCommand.class))
                .collect(Collectors.toSet());

        for (Map.Entry<String, LocalCommandHandler> entry : beans) {
            localCommands.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void handle(Update update) {
        if (update.hasMessage()) {
            final Boolean isBot = update.getMessage().getFrom().getIsBot();
            if (isBot) return;

            handleMessage(update);
        }
        else if (update.hasCallbackQuery()) {
            final Boolean isBot = update.getCallbackQuery().getFrom().getIsBot();
            if (isBot) return;

            handleQuery(update);
        }
    }

    private void handleMessage(Update update) {
        final Message message = update.getMessage();
        final Chat chat = message.getChat();
        final User user = message.getFrom();
        final String text = message.getText();

        UserEntity userEntity = userService.getUserByTelegramId(user.getId())
                .orElseGet(() -> userService.createUser(user.getId(), user.getUserName()));

        Map<String, Object> headers = new HashMap<>();
        headers.put("messageId", message.getMessageId());
        headers.put("chatId", String.valueOf(message.getChatId()));
        headers.put("chatIdLong", message.getChatId());
        headers.put("userId", user.getId());
        headers.put("user", userEntity);
        headers.put("text", text);

        final MessageStateMachine<LocalChatStates> stateMachine = stateMachineService.createLocal(userEntity);

        if (text.startsWith("/")) {
            handleCommand(text.substring(1), stateMachine, headers);

            stateMachineService.save((UserEntity) headers.get("user"), stateMachine);
            return;
        }

        if (stateMachine.getCurrentState().ordinal() >= LocalChatStates.EVENT_CREATE.ordinal()
                && stateMachine.getCurrentState().ordinal() <= LocalChatStates.EVENT_CONFIRM.ordinal()) {
            headers.put("event", userEntity.getSelectedEvent());
        }

        if (stateMachine.getCurrentState().ordinal() >= LocalChatStates.MEMBER_INFO.ordinal()
                && stateMachine.getCurrentState().ordinal() <= LocalChatStates.MEMBER_CONFIRM.ordinal()) {
            final EventEntity event = userEntity.getSelectedEvent();
            headers.put("event", event);
            headers.put("member", memberService.getMember(userEntity, event));
        }

        stateMachine.handle(headers);
        stateMachineService.save((UserEntity) headers.get("user"), stateMachine);
    }

    private void handleQuery(Update update) {
        final CallbackQuery callbackQuery = update.getCallbackQuery();
        final Message message = callbackQuery.getMessage();
        final Chat chat = message.getChat();
        final User user = callbackQuery.getFrom();
        final String callbackData = callbackQuery.getData();

        UserEntity userEntity = userService.getUserByTelegramId(user.getId())
                .orElseGet(() -> userService.createUser(user.getId(), user.getUserName()));

        Map<String, Object> headers = new HashMap<>();
        headers.put("messageId", message.getMessageId());
        headers.put("chatId", String.valueOf(message.getChatId()));
        headers.put("chatIdLong", message.getChatId());
        headers.put("userId", user.getId());
        headers.put("user", userEntity);
        headers.put("callbackData", callbackData);

        final MessageStateMachine<LocalChatStates> stateMachine = stateMachineService.createLocal(userEntity);

        if (callbackData.startsWith("/")) {
            handleCommand(callbackData.substring(1), stateMachine, headers);
            stateMachineService.save((UserEntity) headers.get("user"), stateMachine);
            return;
        }

        if (stateMachine.getCurrentState().ordinal() >= LocalChatStates.EVENT_CREATE.ordinal()
                && stateMachine.getCurrentState().ordinal() <= LocalChatStates.EVENT_CONFIRM.ordinal()) {
            headers.put("event", userEntity.getSelectedEvent());
        }

        if (stateMachine.getCurrentState().ordinal() >= LocalChatStates.MEMBER_INFO.ordinal()
                && stateMachine.getCurrentState().ordinal() <= LocalChatStates.MEMBER_CONFIRM.ordinal()) {
            final EventEntity event = userEntity.getSelectedEvent();
            headers.put("event", event);
            headers.put("member", memberService.getMember(userEntity, event));
        }

        stateMachine.handle(headers);
        stateMachineService.save((UserEntity) headers.get("user"), stateMachine);
    }

    private void handleCommand(String command, MessageStateMachine<LocalChatStates> stateMachine, Map<String, Object> headers) {
        final String[] args = command.trim().split(" ");
        if (args.length == 0) return;

        String commandId = args[0];
        String[] commandArgs = new String[args.length - 1];
        System.arraycopy(args, 1, commandArgs, 0, args.length - 1);

        headers.put("commandId", commandId);
        headers.put("commandArgs", commandArgs);

        final LocalCommandHandler commandHandler = localCommands.get(commandId.toLowerCase());
        if (commandHandler != null) {
            commandHandler.handle(stateMachine, headers);
        }
    }

}