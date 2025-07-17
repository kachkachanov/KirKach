package ru.KirillKachanov.tgBot;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/support")
public class SupportController {

    private final OpenAiService openAiService;

    public SupportController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody String message) {
        return openAiService.send(message);
    }
}
