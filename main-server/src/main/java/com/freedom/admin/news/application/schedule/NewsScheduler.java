package com.freedom.admin.news.application.schedule;

import com.freedom.common.logging.Loggable;
import com.freedom.admin.news.application.facade.NewsFacade;
import com.freedom.common.notification.DiscordWebhookClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;

@Component
@RequiredArgsConstructor
public class NewsScheduler {
    
    private final NewsFacade newsFacade;
    private final DiscordWebhookClient discordWebhookClient;
    
    @Scheduled(cron = "${news.scheduler.cron}")
    @Loggable("뉴스 수집 스케줄러")
    public void scheduleNewsCollection() {
        try {
            newsFacade.newsCollection();
        } catch (Exception e) {
            String stackTrace = getStackTraceAsString(e);

            discordWebhookClient.sendErrorMessage(
                    "🚨 뉴스 수집 스케줄러 오류",
                    "**오류 메시지:** " + e.getMessage() + 
                    "\n\n**스택 트레이스:**\n```" + 
                    (stackTrace.length() > 1500 ? stackTrace.substring(0, 1500) + "..." : stackTrace) + 
                    "```"
            );
        }
    }
    
    private String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
