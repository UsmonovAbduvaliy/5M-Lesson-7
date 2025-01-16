package org.example;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    static TelegramBot bot = new TelegramBot("7700582874:AAF-dYlGjSfCsMp0WQ-LABLjR10X-VIgB80");

    public static void main(String[] args) {
        ExecutorService s = Executors.newFixedThreadPool(10);
        bot.setUpdatesListener(updates->{
            for (Update update : updates) {
                s.execute(() -> {
                    BotService.hendle(update);
                });
            }
            return -1;
        });
    }
}