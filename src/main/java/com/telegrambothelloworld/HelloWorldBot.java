package com.telegrambothelloworld;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class HelloWorldBot extends TelegramLongPollingBot {
    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            switch (messageText) {
                case "/start":
                    showMainMenu(chatId);
                    break;
                case "Кнопка 1":
                case "Кнопка 2":
                    sendMessage(chatId, messageText);
                    break;
                case "Далі":
                    showSecondMenu(chatId);
                    break;
                case "Назад":
                    showMainMenu(chatId);
                    break;
                default:
                    sendMessage(chatId, "Невідома команда");
            }
        }
    }

    private void showMainMenu(String chatId) {
        SendMessage message = createMenuMessage(chatId, "Головне меню", List.of(
                List.of("Кнопка 1", "Кнопка 2"),
                List.of("Далі")
        ));
        sendMenuMessage(message);
    }

    private void showSecondMenu(String chatId) {
        SendMessage message = createMenuMessage(chatId, "Друге меню", List.of(
                List.of("Кнопка 1", "Кнопка 2"),
                List.of("Назад")
        ));
        sendMenuMessage(message);
    }

    private SendMessage createMenuMessage(String chatId, String text, List<List<String>> buttons) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();

        for (List<String> rowButtons : buttons) {
            KeyboardRow row = new KeyboardRow();
            for (String button : rowButtons) {
                row.add(new KeyboardButton(button));
            }
            keyboard.add(row);
        }
        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }

    private void sendMenuMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
