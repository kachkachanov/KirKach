package ru.KirillKachanov.tgBot;

import org.springframework.stereotype.Service;

@Service
public class ToolService {

    public String getOrderDetails(String orderId) {
        return "Ваш заказ #" + orderId + " сейчас в пути и будет доставлен в течение часа.";
    }

    public String submitPositiveFeedback(String message) {
        return "Спасибо за положительный отзыв! Мы рады, что вы довольны.";
    }

    public String submitNegativeFeedback(String message) {
        return "Спасибо за ваш отзыв. Мы обязательно разберёмся и постараемся стать лучше.";
    }

    public String fallbackHelp(String message) {
        return "С вами свяжется оператор в ближайшее время.";
    }
}
