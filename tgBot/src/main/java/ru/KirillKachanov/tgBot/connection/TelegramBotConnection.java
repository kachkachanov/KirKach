package ru.KirillKachanov.tgBot.connection;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.KirillKachanov.tgBot.entity.Category;
import ru.KirillKachanov.tgBot.entity.Client;
import ru.KirillKachanov.tgBot.entity.ClientOrder;
import ru.KirillKachanov.tgBot.entity.OrderProduct;
import ru.KirillKachanov.tgBot.entity.Product;
import ru.KirillKachanov.tgBot.service.EntitiesService;
import ru.KirillKachanov.tgBot.service.OpenAiService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Service
public class TelegramBotConnection {

    private static final Logger LOGGER = Logger.getLogger(TelegramBotConnection.class.getName());

    private final EntitiesService entitiesService;
    private final OpenAiService openAiService;
    private TelegramBot bot;

    @Value("${telegram.bot.token}")
    private String botToken;

    // Карта для хранения текущего контекста категории для каждого пользователя
    private final ConcurrentHashMap<Long, Long> userCategoryContext = new ConcurrentHashMap<>();

    // Карта для отслеживания режима поддержки для каждого пользователя
    // key: chatId, value: boolean (true = в режиме поддержки, false = обычный режим)
    private final ConcurrentHashMap<Long, Boolean> userInSupportMode = new ConcurrentHashMap<>();


    public TelegramBotConnection(EntitiesService entitiesService, OpenAiService openAiService) {
        this.entitiesService = entitiesService;
        this.openAiService = openAiService;
    }

    @PostConstruct
    public void createConnection() {
        if (botToken == null || botToken.equals("your_bot_token") || botToken.isEmpty()) {
            LOGGER.severe("Ошибка: Токен Telegram бота не настроен или пуст. Проверьте application.properties");
            return;
        }
        bot = new TelegramBot(botToken);
        bot.setUpdatesListener(new TelegramUpdatesListener());
        LOGGER.info("Telegram Bot connection created. Listening for updates...");
    }

    private class TelegramUpdatesListener implements UpdatesListener {
        @Override
        public int process(List<Update> updates) {
            updates.forEach(this::processUpdate);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }

        private void processUpdate(Update update) {
            if (update.message() != null) {
                processMessage(update.message());
            } else if (update.callbackQuery() != null) {
                processCallbackQuery(update.callbackQuery());
            }
        }

        private void processMessage(Message message) {
            Long chatId = message.chat().id();
            String text = message.text();

            LOGGER.info("Получено сообщение от чата " + chatId + ": " + text);

            Client client = entitiesService.getOrCreateClient(
                    chatId,
                    message.from().firstName() + (message.from().lastName() != null ? " " + message.from().lastName() : ""),
                    "N/A", // Заглушка для номера телефона
                    "N/A"  // Заглушка для адреса
            );

            ClientOrder activeOrder = entitiesService.getOrCreateActiveOrder(client);

            if (text == null) {
                LOGGER.warning("Получено пустое текстовое сообщение. Пропускаем обработку.");
                return;
            }

            // Проверяем, находится ли пользователь в режиме поддержки [cite: 119, 122]
            boolean inSupportMode = userInSupportMode.getOrDefault(chatId, false);


            if ("/start".equals(text) || "Вернуться к заказам".equals(text)) { // [cite: 124, 125]
                userInSupportMode.put(chatId, false); // Выходим из режима поддержки [cite: 126]
                openAiService.clearMessageHistory(chatId); // Очищаем историю AI диалога
                userCategoryContext.remove(chatId); // Сбрасываем контекст категории
                sendInitialMenu(chatId);
            } else if ("Оформить заказ".equals(text)) {
                handleCheckout(chatId, activeOrder);
            } else if ("Поддержка".equals(text)) { // [cite: 120]
                userInSupportMode.put(chatId, true); // Входим в режим поддержки [cite: 121]
                openAiService.clearMessageHistory(chatId); // Очищаем историю AI диалога при входе
                bot.execute(new SendMessage(chatId, "Вы вошли в режим поддержки. Задайте свой вопрос или нажмите 'Вернуться к заказам' для выхода."));
                sendSupportMenu(chatId); // Отправляем меню для режима поддержки [cite: 124]
            } else {
                // Если пользователь НЕ в режиме поддержки, сначала пытаемся обработать как команду меню.
                // Это нужно, чтобы кнопки меню (например, "Бургеры") работали как обычные кнопки,
                // а не отправлялись в AI как запрос.
                boolean isHandledAsMenuCommand = false;
                if (!inSupportMode) {
                    isHandledAsMenuCommand = handleCategorySelectionByText(chatId, text);
                }

                // Если сообщение не было обработано как команда меню ИЛИ мы в режиме поддержки,
                // то отправляем его в AI для обработки естественного языка.
                if (!isHandledAsMenuCommand || inSupportMode) { // [cite: 5]
                    handleAiSupportRequest(chatId, text); // [cite: 6, 7, 8, 9, 10]
                }
                // Если сообщение было обработано как команда меню, то тут ничего делать не нужно,
                // т.к. handleCategorySelectionByText уже отправил соответствующее меню.
            }
        }

        private void processCallbackQuery(CallbackQuery callbackQuery) {
            Long chatId = callbackQuery.message().chat().id();
            String data = callbackQuery.data();

            LOGGER.info("Получен callback query от чата " + chatId + ": " + data);

            // Если пользователь в режиме поддержки, игнорируем callbackQuery (или обрабатываем по-особому)
            if (userInSupportMode.getOrDefault(chatId, false)) {
                bot.execute(new SendMessage(chatId, "Inline-кнопки не активны в режиме поддержки. Используйте текстовый ввод или 'Вернуться к заказам'."));
                return;
            }

            Client client = entitiesService.getOrCreateClient(
                    chatId,
                    callbackQuery.from().firstName() + (callbackQuery.from().lastName() != null ? " " + callbackQuery.from().lastName() : ""),
                    "N/A",
                    "N/A"
            );
            ClientOrder activeOrder = entitiesService.getOrCreateActiveOrder(client);

            if (data.startsWith("product:")) {
                Long productId = Long.parseLong(data.substring("product:".length()));
                entitiesService.addProductToOrder(activeOrder, productId);
                Product addedProduct = entitiesService.getProductById(productId);
                if (addedProduct != null) {
                    bot.execute(new SendMessage(chatId, "'" + addedProduct.getName() + "' добавлен в заказ.")); //
                }
                Long currentCategoryId = userCategoryContext.get(chatId);
                if (currentCategoryId != null) {
                    sendMenuForCategory(chatId, currentCategoryId);
                } else {
                    sendInitialMenu(chatId);
                }
            }
        }

        private void sendInitialMenu(Long chatId) {
            List<Category> categories = entitiesService.getCategoriesByParentId(null);
            LOGGER.info("EntitiesService вернул " + categories.size() + " категорий верхнего уровня для начального меню.");

            // Создаем ReplyKeyboardMarkup. Теперь добавляем кнопку "Поддержка" [cite: 120]
            ReplyKeyboardMarkup markup = createReplyKeyboardMarkup(categories, false, true); // Включаем "Поддержку", не включаем "В основное меню"

            bot.execute(new SendMessage(chatId, "Выберите категорию:") //
                    .replyMarkup(markup));

            if (categories.isEmpty()) {
                bot.execute(new SendMessage(chatId, "Категории пока не настроены в системе.")); //
            }
        }

        private void sendSupportMenu(Long chatId) {
            ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(
                    new KeyboardButton("Вернуться к заказам") // [cite: 123, 124]
            ).resizeKeyboard(true);

            bot.execute(new SendMessage(chatId, "Вы в режиме поддержки. Задайте свой вопрос или нажмите 'Вернуться к заказам' для выхода.")
                    .replyMarkup(markup));
        }

        private void handleAiSupportRequest(Long chatId, String userText) {
            // Отправляем запрос в OpenAiService и получаем ответ
            String aiResponse = openAiService.send(chatId, userText);
            bot.execute(new SendMessage(chatId, aiResponse));
            sendSupportMenu(chatId); // Показываем меню поддержки снова
        }

        /**
         * Обрабатывает текстовый ввод как выбор категории.
         *
         * @param chatId       Идентификатор чата.
         * @param categoryName Название категории из сообщения пользователя.
         * @return true, если сообщение было обработано как выбор категории, false в противном случае.
         */
        private boolean handleCategorySelectionByText(Long chatId, String categoryName) {
            Long currentContextCategoryId = userCategoryContext.get(chatId);

            List<Category> categoriesToCheck;
            if (currentContextCategoryId == null) {
                categoriesToCheck = entitiesService.getCategoriesByParentId(null);
            } else {
                categoriesToCheck = entitiesService.getCategoriesByParentId(currentContextCategoryId);
            }

            Optional<Category> selectedCategory = categoriesToCheck.stream()
                    .filter(c -> c.getName().equalsIgnoreCase(categoryName))
                    .findFirst();

            if (selectedCategory.isPresent()) {
                userCategoryContext.put(chatId, selectedCategory.get().getId());
                sendMenuForCategory(chatId, selectedCategory.get().getId());
                return true; // Обработано как категория
            } else {
                // Это не категория, возможно, это запрос для AI
                return false; // Не обработано как категория
            }
        }

        private void sendMenuForCategory(Long chatId, Long categoryId) {
            List<Category> subcategories = entitiesService.getCategoriesByParentId(categoryId);
            List<Product> products = entitiesService.getProductsByCategoryIdForDisplay(categoryId);

            ReplyKeyboardMarkup replyMarkup = createReplyKeyboardMarkup(subcategories, true, false); // Включаем "В основное меню", не включаем "Поддержку"

            bot.execute(new SendMessage(chatId, "Выберите подкатегорию или товар:")
                    .replyMarkup(replyMarkup));

            if (!products.isEmpty()) {
                InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
                for (Product product : products) {
                    InlineKeyboardButton button = new InlineKeyboardButton(String.format("%s. Цена: %.2f руб.", product.getName(), product.getPrice()))
                            .callbackData("product:" + product.getId());
                    inlineMarkup.addRow(button);
                }
                bot.execute(new SendMessage(chatId, "Для выбора товара нажмите на кнопку ниже:") //
                        .replyMarkup(inlineMarkup));
            } else if (subcategories.isEmpty() && products.isEmpty()) {
                bot.execute(new SendMessage(chatId, "В данной категории пока нет ни подкатегорий, ни товаров. Возвращаемся в основное меню."));
                userCategoryContext.remove(chatId);
                sendInitialMenu(chatId);
            }
        }

        // Вспомогательный метод для создания ReplyKeyboardMarkup
        private ReplyKeyboardMarkup createReplyKeyboardMarkup(List<Category> categories, boolean includeGoToMainMenu, boolean includeSupportButton) {
            ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(new KeyboardButton[][]{})
                    .resizeKeyboard(true);

            // Добавляем кнопки категорий по 3 в ряд
            if (!categories.isEmpty()) {
                int buttonsPerRow = 3;
                for (int i = 0; i < categories.size(); i += buttonsPerRow) {
                    List<KeyboardButton> row = new ArrayList<>();
                    for (int j = 0; j < buttonsPerRow && (i + j) < categories.size(); j++) {
                        row.add(new KeyboardButton(categories.get(i + j).getName()));
                    }
                    markup.addRow(row.toArray(new KeyboardButton[0]));
                }
            }

            // Добавляем основные кнопки: "Оформить заказ", "В основное меню" (если требуется), "Поддержка" (если требуется)
            List<KeyboardButton> utilityButtonsRow1 = new ArrayList<>();
            utilityButtonsRow1.add(new KeyboardButton("Оформить заказ"));
            if (includeGoToMainMenu) {
                utilityButtonsRow1.add(new KeyboardButton("В основное меню"));
            }
            if (includeSupportButton) {
                utilityButtonsRow1.add(new KeyboardButton("Поддержка")); // [cite: 120]
            }
            if (!utilityButtonsRow1.isEmpty()) {
                markup.addRow(utilityButtonsRow1.toArray(new KeyboardButton[0]));
            }

            return markup;
        }

        private void handleCheckout(Long chatId, ClientOrder activeOrder) {
            List<OrderProduct> orderProducts = entitiesService.getOrderProducts(activeOrder);

            if (orderProducts.isEmpty()) {
                bot.execute(new SendMessage(chatId, "Ваш заказ пуст. Добавьте товары перед оформлением."));
                Long currentCategoryId = userCategoryContext.get(chatId);
                if (currentCategoryId != null) {
                    sendMenuForCategory(chatId, currentCategoryId);
                } else {
                    sendInitialMenu(chatId);
                }
                return;
            }

            StringBuilder orderSummary = new StringBuilder("Ваш заказ:\n"); //
            for (OrderProduct op : orderProducts) {
                orderSummary.append(String.format("%s %dx%.2f=%.2f руб.\n",
                        op.getProduct().getName(),
                        op.getCountProduct(),
                        op.getProduct().getPrice(),
                        op.getProduct().getPrice() * op.getCountProduct()));
            }
            Double total = entitiesService.calculateOrderTotal(activeOrder);
            orderSummary.append(String.format("Итого %.2f руб.", total));

            bot.execute(new SendMessage(chatId, orderSummary.toString())); //

            try {
                entitiesService.closeOrder(activeOrder);
                Client client = entitiesService.getClientById(activeOrder.getClient().getId());
                if (client != null) {
                    entitiesService.getOrCreateActiveOrder(client);
                }

                bot.execute(new SendMessage(chatId, "Заказ №" + activeOrder.getId() + " подтвержден. Курьер уже едет к Вам по адресу 4. Приблизительное время доставки 90 мин.")); //
                userCategoryContext.remove(chatId);
                userInSupportMode.put(chatId, false);
                openAiService.clearMessageHistory(chatId);
                sendInitialMenu(chatId);
            } catch (IllegalStateException e) {
                bot.execute(new SendMessage(chatId, "Ошибка при оформлении заказа: " + e.getMessage()));
                Long currentCategoryId = userCategoryContext.get(chatId);
                if (currentCategoryId != null) {
                    sendMenuForCategory(chatId, currentCategoryId);
                } else {
                    sendInitialMenu(chatId);
                }
            }
        }
    }
}