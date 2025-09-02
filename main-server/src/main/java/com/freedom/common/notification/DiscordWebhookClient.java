package com.freedom.common.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordWebhookClient {

    private final WebClient webClient = WebClient.create();

    @Value("${discord.webhook-url}")
    private String webhookUrl;

    public void sendErrorMessage(String title, String errorMessage) {
        if (!isWebhookEnabled()) {
            return;
        }

        try {
            Map<String, Object> embed = createEmbed(title, errorMessage, "15158332"); // 빨간색
            sendToDiscord(embed);

        } catch (Exception e) {
            log.error("디스코드 오류 알림 전송 실패", e);
        }
    }

    private Map<String, Object> createEmbed(String title, String description, String color) {
        return Map.of(
                "title", title,
                "description", description,
                "color", Integer.parseInt(color),
                "timestamp", LocalDateTime.now().toString()
        );
    }

    private void sendToDiscord(Map<String, Object> embed) {
        Map<String, Object> payload = Map.of("embeds", List.of(embed));

        webClient.post()
                .uri(webhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(10))
                .block();
    }

    private boolean isWebhookEnabled() {
        return StringUtils.hasText(webhookUrl) && !webhookUrl.contains("${");
    }
}
