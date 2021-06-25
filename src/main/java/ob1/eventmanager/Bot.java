package ob1.eventmanager;

import ob1.eventmanager.categoryChooser.CategoryButtonBuilder;
import ob1.eventmanager.categoryChooser.TemplateAcceptButtonBuilder;
import ob1.eventmanager.categoryChooser.TemplateButtonBuilder;
import ob1.eventmanager.entity.CategoryEntity;
import ob1.eventmanager.entity.TemplateEntity;
import ob1.eventmanager.entity.TemplateQuestionEntity;
import ob1.eventmanager.service.CategoryServices;
import ob1.eventmanager.service.TemplateService;
import ob1.eventmanager.service.impl.CategoryServicesImpl;
import ob1.eventmanager.service.impl.TemplateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class Bot extends TelegramLongPollingBot {
    private CategoryEntity selectedCategory;
    private TemplateEntity selectedTemplate;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Autowired
    CategoryServices categoryServices;

    @Autowired
    TemplateService templateService;


    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        InlineKeyboardMarkup categoryKeyboardMarkup;
        InlineKeyboardMarkup templateKeyboardMarkup;
        InlineKeyboardMarkup templateAcceptKeyboardMarkup;

        if (update.hasMessage()) {
            final SendMessage sendMessage = new SendMessage();
            final Message message = update.getMessage();
            final String text = message.getText();
            if (text.equals("/setCategory")){
                sendMessage.setText("Список представленных категорий:");
                sendMessage.setChatId(String.valueOf(message.getChatId()));

                categoryKeyboardMarkup = new CategoryButtonBuilder().buildCategoryButtons(categoryServices.getCategories());
                sendMessage.setReplyMarkup(categoryKeyboardMarkup);
            } else {
                sendMessage.setText("Ну вот ты написал \"" + text + "\", и что?");
                sendMessage.setChatId(String.valueOf(message.getChatId()));
            }

            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.hasCallbackQuery()) {
            final EditMessageText editMessageText = new EditMessageText();
            final Message message = update.getCallbackQuery().getMessage();

            editMessageText.setChatId(String.valueOf(message.getChatId()));

            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();

            /* Псевдокодик
              А - выбранная категория
              Б - выбранный шаблон

              Если А = null, то
                   Для всех категорий из списка категорий(а)
                       Если data.equals(cat + а.getId), то
                           А = текущая категория
                           Изменить текст сообщения на выбранную категорию
                           Сгенерировать кнопки шаблонов для выбранной категории
                       конец если
                   конец для всех категорий
              Иначе если А != null и Б = null, то
                   Если data.equals(backToCat), то
                       А = null
                       Б = null
                       Изменить текст сообщения на выбор категорий
                       Сгенерировать кнопки категорий
                   Иначе
                   Для всех шаблонов из списка шаблонов по категории(б)
                       Если data.equals(temp + б.getId), то
                           Б = текущий шаблон
                           Изменить текст сообщения на выбранный шаблон
                           Сгенерировать кнопки подтверждения шаблона
                       конец если
                   конец для всех шаблонов
              Иначе
                  Если data.equals(backToTemp), то
                       Б = null
                       Изменить текст сообщения на выбранную категорию
                       Сгенерировать кнопки шаблонов для выбранной категории
                  Иначе если data.equals(accept), то
                       Вывести сообщение о подтверждении шаблона
             */

            if (selectedCategory == null){
                for (CategoryEntity category: categoryServices.getCategories()) {
                    if (data.equals("cat" + category.getId())){
                        selectedCategory = category;
                        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                        editMessageText.setText("Выбрана категория: " + category.getName() + ". Список доступных шаблонов:");

                        List<TemplateEntity> templateEntities =  templateService.getTemplatesByCategory(category);
                        System.out.println("Количество шаблонов внутри" + templateEntities.size());

                        templateKeyboardMarkup = new TemplateButtonBuilder().buildTemplateButtons(templateEntities);
                        editMessageText.setReplyMarkup(templateKeyboardMarkup);
                        break;
                    }
                }
                if (selectedCategory == null) {
                    editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                    editMessageText.setText("Ошибка. Не найдена категория с ID = " + data);
                }
            } else if (selectedTemplate == null){
                if (data.equals("backToCategoryList")){
                    selectedCategory = null;
                    editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                    editMessageText.setText("Список представленных категорий:");

                    categoryKeyboardMarkup = new CategoryButtonBuilder().buildCategoryButtons(categoryServices.getCategories());
                    editMessageText.setReplyMarkup(categoryKeyboardMarkup);
                } else {
                    for (TemplateEntity template : selectedCategory.getTemplates()) {
                        if (data.equals("temp" + template.getId())) {
                            selectedTemplate = template;
                            editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());

                            StringBuilder questionList = new StringBuilder();
                            for (TemplateQuestionEntity question : template.getQuestions()) {
                                questionList.append(question.getQuestion()).append("\n");
                            }

                            editMessageText.setText("Выбран шаблон: " + template.getName() + ".\nСписок вопросов: \n" + questionList);

                            templateAcceptKeyboardMarkup = new TemplateAcceptButtonBuilder().buildTemplateAcceptButtons();
                            editMessageText.setReplyMarkup(templateAcceptKeyboardMarkup);
                            break;
                        }
                    }
                    if (selectedTemplate == null) {
                        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                        editMessageText.setText("Ошибка. Не найден шаблон с ID = " + data);
                    }
                }
            } else if (data.equals("backToTemplateList")){
                selectedTemplate = null;
                editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                editMessageText.setText("Выбрана категория: " + selectedCategory.getName() + ". Список доступных шаблонов:");

                templateKeyboardMarkup = new TemplateButtonBuilder().buildTemplateButtons(selectedCategory.getTemplates());
                editMessageText.setReplyMarkup(templateKeyboardMarkup);
            } else if (data.equals("acceptTemplate")){
                editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                editMessageText.setText("Выбрана категория: " + selectedCategory.getName() + ". \nВыбран шаблон: " + selectedTemplate.getName() + ".");
            }
            /* Конец реализации псевдокодика */

            try {
                execute(editMessageText);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

}
