package com.xakerz.QrCode;

import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Configuration
public class QrCode extends TelegramWebhookBot {





    private final TelegramBotConfig telegramBotConfig;

    public QrCode(TelegramBotConfig telegramBotConfig) {
        this.telegramBotConfig = telegramBotConfig;
    }

    @Override
    public String getBotUsername() {
        return telegramBotConfig.getUserName();
    }

    @Override
    public String getBotToken() {
        return telegramBotConfig.getBotToken();
    }

    @Override
    public  BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        System.out.println("привет1");
        if (update.hasMessage() && update.getMessage().hasText()) {




        }
        return null;
    }

    public void sendAudio(long chatId, String pathToAudioFile) {
        SendAudio sendAudio = new SendAudio();
        sendAudio.setChatId(chatId);
        sendAudio.setAudio(new InputFile("https://cdn.muzpan.net/?h=JGraYpdVSDQYtq9_hytYL8Pudc6lcqB3I8KLbiwE84hKFpkyoFJsTtR6pn6SOV7ylwB64qZydcr1zJFogDD0XDlo5lM\\"));
        sendAudio.setTitle("Название аудио"); // Необязательно: название аудио
        sendAudio.setCaption("Подпись к аудио"); // Необязательно: подпись к аудио

        // Отправляем аудио
        try {
            execute(sendAudio);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotPath() {
        return telegramBotConfig.getWebHookPath();
    }
}
