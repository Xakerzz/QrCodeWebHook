package com.xakerz.QrCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component // Добавьте аннотацию @Component
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramBotConfig {
    @Value("${telegrambot.webHookPath}")
    String webHookPath;
    @Value("${telegrambot.userName}")
    String userName;
    @Value("${telegrambot.botToken}")
    String botToken;

    public void setWebHookPath(String webHookPath) {
        this.webHookPath = webHookPath;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public String getWebHookPath() {
        return webHookPath;
    }

    public String getUserName() {
        return userName;
    }

    public String getBotToken() {
        return botToken;
    }
}
