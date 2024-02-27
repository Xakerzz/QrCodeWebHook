package com.xakerz.QrCode;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
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
import java.util.HashMap;
import java.util.List;


public class QrCode extends TelegramLongPollingBot {

    int count = 0;
HashMap<Long, Integer> counterList = new HashMap<>();



    @Override
    public String getBotUsername() {
        return "@QrCoadeBo";
    }

    @Override
    public String getBotToken() {
        return "7132557561:AAFmHtQM0lJWxM2sgGF9oyhFfWTlBbZH3zk";
    }
    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
Client client = new Client();
client.setId(message.getChatId());
if (counterList.containsKey(client.getId())) {
    client.setCounter(counterList.get(client.getId()));
} else {
    counterList.put(client.getId(), client.getCounter());
}

        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals("/start")) {



            sendTextMessage(client.getId(), "Теперь можно отправить ссылку и бот сделает QrCode для неё.");


        } else if (update.hasMessage() && update.getMessage().hasText() && !update.getMessage().getText().equals("/start")) {
            long id = message.getChatId();
            sendTextMessage(client.getId(), " ✅QrCode успешно создан");
            doQrCode(client.getId(), update.getMessage().getText());
            sendTextMessage(id, "Создадим еще один❔");
            client.counter++;
            counterList.put(client.getId(), client.getCounter());
            if ((client.getCounter() & 2) == 0){
                sendTextMessage(client.getId(), "В благодарность ты можешь подписаться на два моих канала \n ---> \uD83D\uDC49 https://t.me/CalmHorizons \uD83D\uDC48 \n и  ---> \uD83D\uDC49 https://t.me/BraveSails \uD83D\uDC48");
            }
        }
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

}

