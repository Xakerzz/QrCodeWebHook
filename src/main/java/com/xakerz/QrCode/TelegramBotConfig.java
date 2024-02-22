package com.xakerz.QrCode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramBotConfig {
    String webHookPath;
    String userName;
    String botToken;

    public TelegramBotConfig() {
        this.webHookPath = "https://9c06-185-105-91-13.ngrok-free.app";
        this.userName = "@QrCoadeBot";
        this.botToken = "7132557561:AAFmHtQM0lJWxM2sgGF9oyhFfWTlBbZH3zk";
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