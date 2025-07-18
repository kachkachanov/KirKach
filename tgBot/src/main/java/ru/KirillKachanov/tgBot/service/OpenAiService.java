package ru.KirillKachanov.tgBot.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.Message; // Импортируем Message
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Service
public class OpenAiService {

    private static final Logger LOGGER = Logger.getLogger(OpenAiService.class.getName());

    private final ChatClient chatClient;
    private final ToolService toolService; // Внедряем ToolService

    // История сообщений для каждого чата. key: chatId, value: List<Message>
    private final Map<Long, List<Message>> messageHistories = new ConcurrentHashMap<>();

    public OpenAiService(ChatClient.Builder chatClientBuilder, ToolService toolService) {
        this.toolService = toolService;
        this.chatClient = chatClientBuilder
                .build();
    }



    public String send(Long chatId, String userText) {
        // Получаем или создаем историю сообщений для данного чата
        List<Message> history = messageHistories.computeIfAbsent(chatId, k -> new ArrayList<>());

        // Добавляем текущее сообщение пользователя в историю
        history.add(new UserMessage(userText));

        // Ограничиваем историю до последних 10 сообщений
        if (history.size() > 10) {
            // Удаляем старые сообщения, оставляя только последние 10
            history = new ArrayList<>(history.subList(history.size() - 10, history.size()));
            messageHistories.put(chatId, history); // Обновляем историю в мапе
        }

        // Системный промпт - задает поведение и стиль общения модели
        String systemPrompt = "Ты — чат-бот поддержки сервиса доставки еды. Отвечай кратко и по делу. Используй свои инструменты для помощи пользователю.";

        LOGGER.info("Отправляем запрос в OpenAI для chat ID " + chatId + " с сообщением: '" + userText + "'");

        // Отправка запроса и получение ответа от OpenAI API
        String response = chatClient
                .prompt()
                .system(systemPrompt)           // Системный промпт
                .messages(history)              // История сообщений для сохранения контекста
                .tools(toolService)             // Интеграция с функциями (ToolService)
                .call()                         // Выполнение запроса
                .content();                     // Получение только текстового ответа


        history.add(new org.springframework.ai.chat.messages.AssistantMessage(response));
        messageHistories.put(chatId, history); // Обновляем историю в мапе

        LOGGER.info("Получен ответ от OpenAI для chat ID " + chatId + ": '" + response + "'");
        return response;
    }

    public void clearMessageHistory(Long chatId) {
        messageHistories.remove(chatId);
        LOGGER.info("История сообщений для chat ID " + chatId + " очищена.");
    }


}