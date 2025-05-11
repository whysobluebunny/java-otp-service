package ru.mephi.abondarenko.otpapp.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class TelegramNotificationService {

    private final String botToken = "YOUR_BOT_TOKEN";
    private final String telegramApiUrl = "https://api.telegram.org/bot" + botToken;

    public void sendCode(String destination, String code) {
        String message = String.format(destination + ", your confirmation code is: %s", code);
        String url = String.format("%s/sendMessage?chat_id=%s&text=%s",
                telegramApiUrl,
                destination,
                urlEncode(message));

        sendTelegramRequest(url);
    }

    private void sendTelegramRequest(String url) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    log.error("Telegram API error. Status code: {}", statusCode);
                } else {
                    log.info("Telegram message sent successfully");
                }
            }
        } catch (IOException e) {
            log.error("Error sending Telegram message: {}", e.getMessage());
        }
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
