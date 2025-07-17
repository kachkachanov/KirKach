package ru.KirillKachanov.tgBot;

import org.springframework.stereotype.Service;

@Service
public class OpenAiService {

    private final ToolService toolService;

    public OpenAiService(ToolService toolService) {
        this.toolService = toolService;
    }

    public String send(String userMessage) {
        if (userMessage == null) {
            return toolService.fallbackHelp("");
        }
        String message = userMessage.toLowerCase();

        if (message.contains("заказ")) {
            // Пример: просто захватываем цифры из сообщения, если есть
            String orderId = extractOrderId(message);
            return toolService.getOrderDetails(orderId);
        } else if (message.contains("понравилось") || message.contains("спасибо") || message.contains("хорошо")) {
            return toolService.submitPositiveFeedback(userMessage);
        } else if (message.contains("не так") || message.contains("плохо") || message.contains("жалоба")) {
            return toolService.submitNegativeFeedback(userMessage);
        } else {
            return toolService.fallbackHelp(userMessage);
        }
    }

    private String extractOrderId(String message) {
        // Простая попытка найти цифры в сообщении
        String digits = message.replaceAll("\\D+", "");
        return digits.isEmpty() ? "неизвестен" : digits;
    }
}
