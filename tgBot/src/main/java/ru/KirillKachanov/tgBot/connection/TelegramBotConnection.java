package ru.KirillKachanov.tgBot;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class TelegramBotConnection {

    private final EntitiesService entitiesService;
    private TelegramBot bot;

    @Value("${telegram.bot.token}")
    private String botToken;


    private final ConcurrentHashMap<Long, Long> userCategoryContext = new ConcurrentHashMap<>();

    public TelegramBotConnection(EntitiesService entitiesService) {
        this.entitiesService = entitiesService;
    }

    @PostConstruct
    public void createConnection() {
        if (botToken == null || botToken.equals("your_bot_token")) {
            System.err.println("Ошибка: Токен Telegram бота не настроен. Проверьте application.properties");
            return;
        }
        bot = new TelegramBot(botToken);
        bot.setUpdatesListener(new TelegramUpdatesListener());
        System.out.println("Telegram Bot connection created. Listening for updates...");
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

            System.out.println("Received message from chat " + chatId + ": " + text);

            Client client = entitiesService.getOrCreateClient(
                    chatId,
                    message.from().firstName() + (message.from().lastName() != null ? " " + message.from().lastName() : ""),
                    "N/A", // Заглушка для номера телефона
                    "N/A"  // Заглушка для адреса
            );

            // Создаем или получаем активный заказ для клиента (Требование 1)
            ClientOrder activeOrder = entitiesService.getOrCreateActiveOrder(client);

            if ("/start".equals(text) || "В основное меню".equals(text)) {
                // Сбрасываем контекст категории и показываем начальное меню
                userCategoryContext.remove(chatId);
                sendInitialMenu(chatId);
            } else if ("Оформить заказ".equals(text)) {
                handleCheckout(chatId, activeOrder);
            } else {
                // Если это не команда и не кнопка оформления заказа, пытаемся обработать как выбор категории
                handleCategorySelectionByText(chatId, text);
            }
        }

        private void processCallbackQuery(CallbackQuery callbackQuery) {
            Long chatId = callbackQuery.message().chat().id();
            String data = callbackQuery.data();

            System.out.println("Received callback query from chat " + chatId + ": " + data);

            Client client = entitiesService.getOrCreateClient(
                    chatId,
                    callbackQuery.from().firstName() + (callbackQuery.from().lastName() != null ? " " + callbackQuery.from().lastName() : ""),
                    "N/A",
                    "N/A"
            );
            ClientOrder activeOrder = entitiesService.getOrCreateActiveOrder(client);

            if (data.startsWith("product:")) {
                Long productId = Long.parseLong(data.substring("product:".length()));
                entitiesService.addProductToOrder(activeOrder, productId); // Добавляем продукт в заказ
                Product addedProduct = entitiesService.getProductById(productId);
                if (addedProduct != null) {
                    bot.execute(new SendMessage(chatId, "'" + addedProduct.getName() + "' добавлен в заказ."));
                }
                // После добавления продукта остаемся в той же категории, показывая ее меню снова
                Long currentCategoryId = userCategoryContext.get(chatId);
                if (currentCategoryId != null) {
                    sendMenuForCategory(chatId, currentCategoryId);
                } else {
                    sendInitialMenu(chatId); // Если контекст потерян (чего быть не должно), возвращаемся в главное меню
                }
            }
        }

        private void sendInitialMenu(Long chatId) {
            List<Category> categories = entitiesService.getCategoriesByParentId(null); // Категории верхнего уровня
            System.out.println("EntitiesService returned " + categories.size() + " top-level categories for initial menu.");

            ReplyKeyboardMarkup markup = createReplyKeyboardMarkup(categories, false); // false = не показывать "В основное меню"

            bot.execute(new SendMessage(chatId, "Выберите категорию:")
                    .replyMarkup(markup));

            if (categories.isEmpty()) {
                bot.execute(new SendMessage(chatId, "Категории пока не настроены в системе."));
            }
        }

        private void handleCategorySelectionByText(Long chatId, String categoryName) {
            Long currentContextCategoryId = userCategoryContext.get(chatId); // ID текущей отображаемой категории

            List<Category> categoriesToCheck;
            if (currentContextCategoryId == null) {
                // Если мы в основном меню, ищем среди категорий верхнего уровня
                categoriesToCheck = entitiesService.getCategoriesByParentId(null);
            } else {
                // Если мы внутри категории, ищем среди ее подкатегорий
                categoriesToCheck = entitiesService.getCategoriesByParentId(currentContextCategoryId);
            }

            Optional<Category> selectedCategory = categoriesToCheck.stream()
                    .filter(c -> c.getName().equalsIgnoreCase(categoryName))
                    .findFirst();

            if (selectedCategory.isPresent()) {
                // Пользователь выбрал существующую категорию. Обновляем контекст и показываем новое меню.
                userCategoryContext.put(chatId, selectedCategory.get().getId());
                sendMenuForCategory(chatId, selectedCategory.get().getId());
            } else {
                // Неизвестная команда или категория. Повторно отправляем текущее меню.
                bot.execute(new SendMessage(chatId, "Неизвестная команда или категория. Пожалуйста, используйте кнопки."));
                if (currentContextCategoryId != null) {
                    sendMenuForCategory(chatId, currentContextCategoryId);
                } else {
                    sendInitialMenu(chatId);
                }
            }
        }

        private void sendMenuForCategory(Long chatId, Long categoryId) {
            // Получаем подкатегории выбранной категории
            List<Category> subcategories = entitiesService.getCategoriesByParentId(categoryId);
            // Получаем товары для выбранной категории
            List<Product> products = entitiesService.getProductsByCategoryIdForDisplay(categoryId);

            // Создаем ReplyKeyboardMarkup для подкатегорий и основных кнопок
            // Здесь кнопка "В основное меню" должна присутствовать, так как это не начальное меню
            ReplyKeyboardMarkup replyMarkup = createReplyKeyboardMarkup(subcategories, true);

            bot.execute(new SendMessage(chatId, "Выберите подкатегорию или товар:")
                    .replyMarkup(replyMarkup));

            // Если есть продукты, отправляем их отдельно с InlineKeyboardMarkup
            if (!products.isEmpty()) {
                InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
                for (Product product : products) {
                    InlineKeyboardButton button = new InlineKeyboardButton(String.format("%s. Цена: %.2f руб.", product.getName(), product.getPrice()))
                            .callbackData("product:" + product.getId());
                    inlineMarkup.addRow(button);
                }
                bot.execute(new SendMessage(chatId, "Для выбора товара нажмите на кнопку ниже:")
                        .replyMarkup(inlineMarkup));
            } else if (subcategories.isEmpty() && products.isEmpty()) {
                // Если нет ни подкатегорий, ни продуктов в данной категории
                bot.execute(new SendMessage(chatId, "В данной категории пока нет ни подкатегорий, ни товаров. Возвращаемся в основное меню."));
                userCategoryContext.remove(chatId); // Сбрасываем контекст
                sendInitialMenu(chatId);
            }
        }

        // Вспомогательный метод для создания ReplyKeyboardMarkup
        private ReplyKeyboardMarkup createReplyKeyboardMarkup(List<Category> categories, boolean includeGoToMainMenu) {
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

            // Добавляем основные кнопки: "Оформить заказ" и "В основное меню" (если требуется)
            List<KeyboardButton> mainButtons = new ArrayList<>();
            mainButtons.add(new KeyboardButton("Оформить заказ"));
            if (includeGoToMainMenu) {
                mainButtons.add(new KeyboardButton("В основное меню"));
            }
            markup.addRow(mainButtons.toArray(new KeyboardButton[0]));

            return markup;
        }

        private void handleCheckout(Long chatId, ClientOrder activeOrder) {
            List<OrderProduct> orderProducts = entitiesService.getOrderProducts(activeOrder);

            if (orderProducts.isEmpty()) {
                bot.execute(new SendMessage(chatId, "Ваш заказ пуст. Добавьте товары перед оформлением."));
                // Возвращаем пользователя в текущее меню или в основное
                Long currentCategoryId = userCategoryContext.get(chatId);
                if (currentCategoryId != null) {
                    sendMenuForCategory(chatId, currentCategoryId);
                } else {
                    sendInitialMenu(chatId);
                }
                return;
            }

            StringBuilder orderSummary = new StringBuilder("Ваш заказ:\n");
            for (OrderProduct op : orderProducts) {
                orderSummary.append(String.format("%s %dx%.2f=%.2f руб.\n",
                        op.getProduct().getName(),
                        op.getCountProduct(),
                        op.getProduct().getPrice(),
                        op.getProduct().getPrice() * op.getCountProduct()));
            }
            Double total = entitiesService.calculateOrderTotal(activeOrder);
            orderSummary.append(String.format("Итого %.2f руб.", total));

            bot.execute(new SendMessage(chatId, orderSummary.toString()));

            try {
                entitiesService.closeOrder(activeOrder); // Закрываем заказ
                // Создаем новый активный заказ для этого клиента (Требование 4)
                Client client = entitiesService.getClientById(activeOrder.getClient().getId());
                if (client != null) {
                    entitiesService.getOrCreateActiveOrder(client);
                }

                bot.execute(new SendMessage(chatId, "Заказ №" + activeOrder.getId() + " подтвержден. Курьер уже едет к Вам по адресу 4. Приблизительное время доставки 90 мин."));
                userCategoryContext.remove(chatId); // Сбрасываем контекст после оформления заказа
                sendInitialMenu(chatId); // Возвращаемся в главное меню
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