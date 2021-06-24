package ob1.eventmanager;

import ob1.eventmanager.command.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(
        exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
        scanBasePackages = {"ob1.eventmanager"}
)
public class EventManagerApplication {
    private static CommandHandler commandHandler;

    @Autowired
    public void setCommandHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    public static void main(String[] args) {
        Thread t = new Thread(commandHandler);
        t.setDaemon(true);
        t.start();
        SpringApplication.run(EventManagerApplication.class, args);
    }

}
