package com.xakerz.QrCode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QrCode extends TelegramWebhookBot {
    String botPath;
    String botUsername;
    String botToken;


    int count = 0;

    private final TelegramBotConfig telegramBotConfig = new TelegramBotConfig();

    private TelegramFacade telegramFacade;
    @Autowired
    public QrCode(TelegramFacade telegramFacade, DefaultBotOptions options, SetWebhook setWebhook) {
        super(options, String.valueOf(setWebhook));
        this.telegramFacade = telegramFacade;
    }
    @Autowired
    public QrCode(TelegramFacade telegramFacade, SetWebhook setWebhook) {
        super(String.valueOf(setWebhook));
        this.telegramFacade = telegramFacade;
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
        Message message = update.getMessage();


        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals("/start")) {
            long id = message.getChatId();


            sendTextMessage(id, "Теперь можно отправить ссылку и бот сделает QrCode для неё.");


        } else if (update.hasMessage() && update.getMessage().hasText() && !update.getMessage().getText().equals("/start")) {
            long id = message.getChatId();
            sendTextMessage(id, " ✅QrCode успешно создан");
            doQrCode(id, update.getMessage().getText());
            sendTextMessage(id, "Создадим еще один❔");
            count++;
            if ((count & 3) == 0){
                sendTextMessage(id, "В благодарность ты можешь подписаться на два моих канала ---> https://t.me/CalmHorizons  и  ");
            }
        }
        return null;
    }

    private void messageText(Long chatId, String newTextForMessage, String newTextForButtonOne, String newTextForCallbackOne, String newTextForButtonTwo, String newTextForCallbackTwo) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        sendMessage.setText(newTextForMessage);
        sendMessage.setReplyMarkup(getInlineKeyboard(newTextForButtonOne, newTextForButtonTwo, newTextForCallbackOne, newTextForCallbackTwo));
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private InlineKeyboardMarkup getInlineKeyboard(String newTextForButtonOne, String newTextForButtonTwo, String newTextForCallbackOne, String newTextForCallbackTwo) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();


        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(newTextForButtonOne);
        inlineKeyboardButton.setCallbackData(String.valueOf(newTextForCallbackOne));
        row.add(inlineKeyboardButton);

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText(newTextForButtonTwo);
        inlineKeyboardButton1.setCallbackData(String.valueOf(newTextForCallbackTwo));
        row.add(inlineKeyboardButton1);


        keyboard.add(row);
        keyboard.add(row1);


        markup.setKeyboard(keyboard);
        return markup;
    }

    private void sendTextMessage(long chatId, String textMessage) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(textMessage);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    private void sendAudio(long chatId, String pathToAudioFile) {
        SendAudio sendAudio = new SendAudio();
        sendAudio.setChatId(chatId);
        InputFile audio = new InputFile(pathToAudioFile);
        sendAudio.setAudio(audio);
        try {
            execute(sendAudio);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private SendPhoto sendPhotoMessage(long chatId, File file) {
        SendPhoto sendPhoto = new SendPhoto();
        InputFile inputFile = new InputFile(String.valueOf(file));
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(inputFile);

        return sendPhoto;
    }

    public void doQrCode(long id, String str) {

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            QRCode.from(str).withCharset(StandardCharsets.UTF_8.name()).withSize(250, 250).to(ImageType.PNG).writeTo(outputStream);

            File file = File.createTempFile("qrcode", ".png");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                outputStream.writeTo(fos);
            }

            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(id);
            sendPhoto.setPhoto(new InputFile(file));

            try {
                execute(sendPhoto);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            // Удаляем временный файл после отправки
            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    @Override
    public String getBotPath() {
        return telegramBotConfig.getWebHookPath();
    }


    public void setBotToken(String botToken) {
    }

    public void setBotUsername(String userName) {
    }

    public void setBotPath(String webHookPath) {
    }
}

