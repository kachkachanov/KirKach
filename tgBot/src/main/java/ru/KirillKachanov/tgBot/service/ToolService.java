package ru.KirillKachanov.tgBot.service;

import org.springframework.ai.tool.annotation.Tool; // Импортируем аннотацию @Tool
import org.springframework.stereotype.Service;
import ru.KirillKachanov.tgBot.entity.ClientOrder;
import ru.KirillKachanov.tgBot.entity.OrderProduct;

import java.util.List;
import java.util.logging.Logger;

@Service // Указывает Spring, что это компонент (бин), который будет управляться Spring-контейнером.
public class ToolService {

    private static final Logger LOGGER = Logger.getLogger(ToolService.class.getName());

    private final EntitiesService entitiesService;

    // Внедряем EntitiesService через конструктор. Он будет использоваться для доступа к данным.
    public ToolService(EntitiesService entitiesService) {
        this.entitiesService = entitiesService;
    }

    /**
     * Возвращает подробную информацию о заказе по его идентификатору.
     * @param orderId Идентификатор заказа.
     * @return Строка с подробной информацией о заказе.
     */
    @Tool(name = "getOrderDetails", description = "Возвращает подробную информацию о заказе по его идентификатору (orderId). Используйте, если пользователь спрашивает о статусе, содержимом или деталях своего заказа. Пример запроса: 'Где мой заказ 123?', 'Статус заказа 456'.")
    public String getOrderDetails(String orderId) {
        LOGGER.info("Вызван инструмент getOrderDetails для orderId: " + orderId);
        if (orderId == null || orderId.isEmpty()) {
            return "Не могу найти заказ без его идентификатора. Пожалуйста, укажите номер заказа.";
        }

        try {
            Long id = Long.parseLong(orderId);
            ClientOrder order = entitiesService.getOrderById(id);

            if (order == null) {
                return "Заказ с номером " + orderId + " не найден. Уточните номер заказа.";
            }

            StringBuilder details = new StringBuilder("Информация о заказе №" + order.getId() + ":\n");
            details.append("Статус: ").append(order.getStatus()).append("\n");

            List<OrderProduct> products = entitiesService.getOrderProducts(order);
            if (products.isEmpty()) {
                details.append("В заказе нет товаров.\n");
            } else {
                details.append("Состав заказа:\n");
                for (OrderProduct op : products) {
                    details.append("- ").append(op.getProduct().getName())
                            .append(" (").append(op.getCountProduct()).append(" шт.)\n");
                }
            }

            details.append("Общая сумма: ").append(entitiesService.calculateOrderTotal(order)).append(" руб.\n");
            // Добавим примеры информации, как в задании
            details.append("Адрес доставки: ").append(order.getClient().getAddress() != null ? order.getClient().getAddress() : "не указан").append("\n");
            details.append("Ожидаемое время доставки: 90 минут."); // Пример из

            return details.toString();
        } catch (NumberFormatException e) {
            return "Идентификатор заказа должен быть числом. Пожалуйста, проверьте номер.";
        } catch (Exception e) {
            LOGGER.severe("Ошибка при получении деталей заказа " + orderId + ": " + e.getMessage());
            return "Произошла ошибка при получении информации о заказе. Пожалуйста, попробуйте позже.";
        }
    }

    /**
     * Обрабатывает положительный отзыв от пользователя.
     * @param message Текст положительного отзыва.
     * @return Сообщение с благодарностью.
     */
    @Tool(name = "submitPositiveFeedback", description = "Используйте для обработки положительных отзывов, благодарностей или выражения удовлетворения пользователя. Например: 'Все отлично!', 'Спасибо за заказ!', 'Очень доволен вашей работой'.")
    public String submitPositiveFeedback(String message) {
        LOGGER.info("Получен положительный отзыв: " + message);
        // Здесь могла бы быть логика сохранения отзыва в базу данных или отправки уведомления
        return "Спасибо за положительный отзыв! Мы рады, что вы довольны. Ваше мнение помогает нам становиться лучше. 😊";
    }

    /**
     * Обрабатывает отрицательный отзыв или жалобу от пользователя.
     * @param message Текст отрицательного отзыва/жалобы.
     * @return Сообщение с подтверждением получения отзыва.
     */
    @Tool(name = "submitNegativeFeedback", description = "Используйте для обработки негативных отзывов, жалоб или выражения недовольства пользователя. Например: 'Заказ опоздал', 'Еда была холодной', 'Я недоволен обслуживанием'.")
    public String submitNegativeFeedback(String message) {
        LOGGER.info("Получен отрицательный отзыв: " + message);
        // Здесь могла бы быть логика сохранения жалобы и уведомления службы поддержки
        return "Спасибо за ваш отзыв. Мы обязательно разберёмся и постараемся стать лучше. Извините за возможные неудобства. 🙏";
    }

    /**
     * Обрабатывает нераспознанные сообщения или запросы на помощь.
     * @param userMessage Исходное сообщение пользователя, которое не удалось распознать.
     * @return Сообщение с предложением помощи или перенаправлением на оператора.
     */
    @Tool(name = "fallbackHelp", description = "Используйте, если бот не может определить намерение пользователя или классифицировать его сообщение. Возвращает сообщение о том, что свяжется оператор. Пример: 'Я не понял', 'Помогите', 'Как мне...', 'Что вы умеете?'.")
    public String fallbackHelp(String userMessage) {
        LOGGER.info("Получено нераспознанное сообщение: " + userMessage + ". Вызван fallbackHelp.");
        // Здесь можно было бы создать заявку в CRM или уведомить оператора.
        return "Извините, я не совсем понял ваш запрос. С вами свяжется оператор в ближайшее время, чтобы помочь. 🧑‍💻";
    }
}