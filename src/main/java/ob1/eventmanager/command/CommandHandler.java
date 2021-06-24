package ob1.eventmanager.command;

import ob1.eventmanager.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@Component
public class CommandHandler extends BotHandler implements Runnable, Consumer<String> {
    private EventCreator creator;
    private Consumer<? super String> consumer;
    private static String START_COMMAND = "/start";
    private static String CREATE_COMMAND = "/create";
    private static String action = "none";

    @Autowired
    public void setNewBot(Bot bot){
        setBot(bot);
    }

    @Autowired
    public void setCreator(EventCreator creator){
        this.creator = creator;
    }

    @Override
    public void run() {
    }

    @Override
    public void accept(String text) {
        if (text.startsWith(START_COMMAND)) {
            send("Привет! Я ваш личный менеджер мероприятий.\n Мои основные задачи:\n " +
                    "- хранить данные о вашем мероприятии;\n" +
                    "- собирать статистику о предпочтениях гостей;\n" +
                    "- напоминать о предстоящем событии\n" +
                    "- всегда держать в курсе текущего положения дел.\n" +
                    "Для того, чтобы ввести данные о своем мероприятии, воспользуйтесь командой /create");
        } else if (text.startsWith(CREATE_COMMAND)) {
            send("Что ж, приступим.\n" +
                    "Введите название вашего мероприятия.");
            Thread c = new Thread(creator);
            c.setDaemon(true);
            action = CREATE_COMMAND;
            consumer = creator;
            c.start();
           //while(creator.isWorking());
        }else if(action.startsWith(CREATE_COMMAND)){
            receiveData(text).subscribe(consumer);
        }
        else {
            send("Болтаете, болтаете, и все не понимаю я вас :(");
        }
    }

    public Flux<String> receiveData(String s) {
        return Flux.fromArray(new String[]{s});
    }
}
