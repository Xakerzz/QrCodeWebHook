package com.xakerz.QrCode;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
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
import java.util.Map;


public class QrCode extends TelegramLongPollingBot {


    static Map<Long, Integer> lastMessageIds = new HashMap<>();
    static final long channelId = -1002053569829L;


    @Override
    public String getBotUsername() {
        return "xakerz_Bot";
    }

    @Override
    public String getBotToken() {
        return "5994666145:AAF606nhEp2R5_EBcKgDPQqq2f_Vu_JgtMQ"; //"7132557561:AAFmHtQM0lJWxM2sgGF9oyhFfWTlBbZH3zk";
    }

    @Override
    public void onUpdateReceived(Update update) {
        Client client = new Client();
        String callBackData;
        if (update.hasMessage()) {
            Message message = update.getMessage();

            client.setId(message.getChatId());
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
             callBackData = callbackQuery.getData();
            client.setId(callbackQuery.getMessage().getChatId());
        }


        if (!lastMessageIds.isEmpty()) {
            deleteMessageText(client.getId(), lastMessageIds.get(client.getId()));
        }
        boolean check = checkChannelSubscription(String.valueOf(channelId), client.getId());
        if (check) {

            if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals("/start")) {


                sendTextMessage(client.getId(), "Теперь можно отправить ссылку и бот сделает QrCode для неё.");


            } else if (update.hasMessage() && update.getMessage().hasText() && !update.getMessage().getText().equals("/start")) {
                long id = client.getId();
                sendTextMessage(client.getId(), " ✅QrCode успешно создан");
                doQrCode(client.getId(), update.getMessage().getText());
                sendTextMessage(id, "Создадим еще один❔");
            }else if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                callBackData = callbackQuery.getData();
                if (callBackData.equals("didSub")){


                        sendTextMessage(client.getId(), "Теперь можно отправить ссылку и бот сделает QrCode для неё.");



                }
            }
        }  else if (!check){

            keyboardSub(client.id);


            System.out.println(check);
        }

    }

    private void keyboardSub(long id) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();


        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Подписаться на канал");
        inlineKeyboardButton.setUrl("https://t.me/psycholog_y_ya");

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Я подписалась(ся)");
        inlineKeyboardButton1.setCallbackData("didSub");


        rowInline.add(inlineKeyboardButton1);
        rowInline.add(inlineKeyboardButton);


        rowsInline.add(rowInline);

        markupInline.setKeyboard(rowsInline);

// Создаем объект SendMessage для отправки сообщения с кнопкой
        SendMessage messageRoll = new SendMessage();
        messageRoll.setChatId(id);
        messageRoll.setText("Чтобы сделать QrCode вам необходимо подписаться на наш канал");
        messageRoll.setReplyMarkup(markupInline);

        try {
            Message sentMessage = execute(messageRoll);
            lastMessageIds.put(id, sentMessage.getMessageId());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }

    private void deleteMessageText(Long chatId, int messageId) {

        DeleteMessage sendMessage = new DeleteMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setMessageId(messageId);


        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean checkChannelSubscription(String channelId, long userId) {
        GetChatMember getChatMemberRequest = new GetChatMember(channelId, userId);

        try {
            ChatMember chatMember = execute(getChatMemberRequest);
            return chatMember.getStatus().equals("member") || chatMember.getStatus().equals("administrator") || chatMember.getStatus().equals("creator");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return false;
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
            Message sentMessage = execute(sendMessage);
            lastMessageIds.put(chatId, sentMessage.getMessageId());
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

