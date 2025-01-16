package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.example.Main.bot;

public class BotService {

    public static void hendle(Update update) {
        try{
            if(update.message()!=null){
                Long chatId = update.message().chat().id();
                String text = update.message().text();
                TgUser user = getOrCreat(chatId);
                if(text.equals("/start")){
                    SendMessage message = new SendMessage(chatId,"Users: ");
                    message.replyMarkup(generateMarkUpForUsers());
                    user.setState(State.CONTINUE);
                    SendResponse execute = bot.execute(message);
                    user.setMessageId(execute.message().messageId());
                }
            } else if (update.callbackQuery() != null) {
                Long chatId = update.callbackQuery().from().id();
                String data = update.callbackQuery().data();
                TgUser user = getOrCreat(chatId);

                if(user.getState().equals(State.CONTINUE)){
                    if(!(data.length()>2)){
                        user.setChosenUserId(Integer.parseInt(data));
                        bot.execute(new DeleteMessage(chatId, user.getMessageId()));
                         User user1 = getUser(user.getChosenUserId().toString());
                        SendMessage message = new SendMessage(chatId, user1.getName());
                        message.replyMarkup(generateMarkUpForUser());
                        user.setState(State.USER_BUTTON);
                        SendResponse execute = bot.execute(message);
                        user.setMessageId(execute.message().messageId());
                    }
                } else if (user.getState().equals(State.USER_BUTTON)) {
                    if(data.equals("back")){
                        bot.execute(new DeleteMessage(chatId, user.getMessageId()));
                        SendMessage message = new SendMessage(chatId,"Users: ");
                        message.replyMarkup(generateMarkUpForUsers());
                        user.setState(State.CONTINUE);
                        SendResponse execute = bot.execute(message);
                        user.setMessageId(execute.message().messageId());
                    }else if (data.equals("albums")){
                        bot.execute(new DeleteMessage(chatId, user.getMessageId()));
                        SendMessage message = new SendMessage(chatId,"Albums: ");
                        message.replyMarkup(generateMarkUpForAlbums(user.getChosenUserId()));
                        user.setState(State.ALBUM);
                        SendResponse execute = bot.execute(message);
                        user.setMessageId(execute.message().messageId());
                    } else if (data.equals("todo")) {
                        bot.execute(new DeleteMessage(chatId, user.getMessageId()));
                        SendMessage message = new SendMessage(chatId,"Todos: ");
                        message.replyMarkup(generateMarkUpForTodos(user.getChosenUserId()));
                        user.setState(State.TODO);
                        SendResponse execute = bot.execute(message);
                        user.setMessageId(execute.message().messageId());
                    } else if (data.equals("post")) {
                        bot.execute(new DeleteMessage(chatId, user.getMessageId()));
                        SendMessage message = new SendMessage(chatId,"Posts: ");
                        message.replyMarkup(generateMarkUpForPosts(user.getChosenUserId()));
                        user.setState(State.POST);
                        SendResponse execute = bot.execute(message);
                        user.setMessageId(execute.message().messageId());
                    }
                } else if (user.getState().equals(State.ALBUM)) {
                    if(data.equals("back")){
                        bot.execute(new DeleteMessage(chatId, user.getMessageId()));
                        User user1 = getUser(user.getChosenUserId().toString());
                        SendMessage message = new SendMessage(chatId, user1.getName());
                        message.replyMarkup(generateMarkUpForUser());
                        user.setState(State.USER_BUTTON);
                        SendResponse execute = bot.execute(message);
                        user.setMessageId(execute.message().messageId());
                    }else {
                        user.setChosenAlbumId(Integer.parseInt(data));
                        bot.execute(new DeleteMessage(chatId, user.getMessageId()));
                        SendMessage message = new SendMessage(chatId,"Photos: ");
                        message.replyMarkup(generateMarkUpForPhotos(user.getChosenAlbumId()));
                        user.setState(State.PHOTOS);
                        SendResponse execute = bot.execute(message);
                        user.setMessageId(execute.message().messageId());
                    }
                }else if (user.getState().equals(State.PHOTOS)) {
                    if (data.equals("back")){
                        bot.execute(new DeleteMessage(chatId, user.getMessageId()));
                        SendMessage message = new SendMessage(chatId,"Albums: ");
                        message.replyMarkup(generateMarkUpForAlbums(user.getChosenUserId()));
                        user.setState(State.ALBUM);
                        SendResponse execute = bot.execute(message);
                        user.setMessageId(execute.message().messageId());
                    }else {
                        bot.execute(new DeleteMessage(chatId, user.getMessageId()));
                        SendMessage message = new SendMessage(chatId,"Chosen Photo: "+data);
                        message.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton("Back").callbackData("back")));
                        SendResponse execute = bot.execute(message);
                        user.setMessageId(execute.message().messageId());
                    }
                }else if (user.getState().equals(State.TODO)) {
                    if(data.equals("back")){
                        bot.execute(new DeleteMessage(chatId, user.getMessageId()));
                        User user1 = getUser(user.getChosenUserId().toString());
                        SendMessage message = new SendMessage(chatId, user1.getName());
                        message.replyMarkup(generateMarkUpForUser());
                        user.setState(State.USER_BUTTON);
                        SendResponse execute = bot.execute(message);
                        user.setMessageId(execute.message().messageId());
                    }else {
                        bot.execute(new DeleteMessage(chatId, user.getMessageId()));
                        SendMessage message = new SendMessage(chatId,"Chosen Todo: "+data);
                        message.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton("Back").callbackData("back")));
                        SendResponse execute = bot.execute(message);
                        user.setMessageId(execute.message().messageId());
                    }
                } else if (user.getState().equals(State.POST)) {
                    if(data.equals("back")){
                        bot.execute(new DeleteMessage(chatId, user.getMessageId()));
                        User user1 = getUser(user.getChosenUserId().toString());
                        SendMessage message = new SendMessage(chatId, user1.getName());
                        message.replyMarkup(generateMarkUpForUser());
                        user.setState(State.USER_BUTTON);
                        SendResponse execute = bot.execute(message);
                        user.setMessageId(execute.message().messageId());
                    }else if(data.length()<4) {
                        user.setChosenPostId(Integer.parseInt(data));
                        bot.execute(new DeleteMessage(chatId, user.getMessageId()));
                        SendMessage message = new SendMessage(chatId,"Comments: ");
                        message.replyMarkup(generateMarkUpForComments(user.getChosenPostId()));
                        user.setState(State.COMMENTS);
                        SendResponse execute = bot.execute(message);
                        user.setMessageId(execute.message().messageId());
                    }
                } else if (user.getState().equals(State.COMMENTS)) {
                    if(data.equals("back")){
                        bot.execute(new DeleteMessage(chatId, user.getMessageId()));
                        SendMessage message = new SendMessage(chatId,"Posts: ");
                        message.replyMarkup(generateMarkUpForPosts(user.getChosenUserId()));
                        user.setState(State.POST);
                        SendResponse execute = bot.execute(message);
                        user.setMessageId(execute.message().messageId());
                    }else {
                        bot.execute(new DeleteMessage(chatId, user.getMessageId()));
                        String body = getBody(data);
                        SendMessage message = new SendMessage(chatId,"Chosen Comment: "+body);
                        message.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton("Back").callbackData("back")));
                        SendResponse execute = bot.execute(message);
                        user.setMessageId(execute.message().messageId());
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static String getBody(String data) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/comments/" + data))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        Gson gson = new Gson();
        Comments comments = gson.fromJson(body, Comments.class);
        return comments.getBody();
    }

    private static Keyboard generateMarkUpForComments(Integer chosenPostId) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/comments?postId=" + chosenPostId))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        Gson gson = new Gson();
        Comments[] comments = gson.fromJson(body, Comments[].class);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (Comments comment : comments) {
            markup.addRow(
                    new InlineKeyboardButton(comment.getName()).callbackData(comment.getId().toString())
            );
        }
        markup.addRow(new InlineKeyboardButton("Back").callbackData("back"));
        return markup;
    }

    private static Keyboard generateMarkUpForPosts(Integer chosenUserId) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/posts?userId=" + chosenUserId))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        Gson gson = new Gson();
        Post[] posts = gson.fromJson(body, Post[].class);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (Post post : posts) {
            markup.addRow(
                    new InlineKeyboardButton(post.getTitle()).callbackData("post"),
                    new InlineKeyboardButton("Comments").callbackData(post.getId().toString())
            );
        }
        markup.addRow(new InlineKeyboardButton("Back").callbackData("back"));
        return markup;
    }

    private static Keyboard generateMarkUpForTodos(Integer chosenUserId) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/todos?userId=" + chosenUserId))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        Gson gson = new Gson();
        Todo[] todos = gson.fromJson(body, Todo[].class);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (Todo todo : todos) {
            markup.addRow(
                    new InlineKeyboardButton(todo.getTitle()).callbackData(todo.getTitle())
            );
        }
        markup.addRow(new InlineKeyboardButton("Back").callbackData("back"));
        return markup;
    }

    private static Keyboard generateMarkUpForPhotos(Integer chosenAlbumId) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/photos?albumId=" + chosenAlbumId))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        Gson gson = new Gson();
        Photo[] photos = gson.fromJson(body, Photo[].class);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (Photo photo : photos) {
            markup.addRow(
                    new InlineKeyboardButton(photo.getTitle()).callbackData(photo.getUrl())
            );
        }
        markup.addRow(new InlineKeyboardButton("Back").callbackData("back"));
        return markup;
    }

    private static Keyboard generateMarkUpForAlbums(Integer userId) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/albums?userId=" + userId))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        Gson gson = new Gson();
        Album[] albums = gson.fromJson(body, Album[].class);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (Album album : albums) {
            markup.addRow(
                    new InlineKeyboardButton(album.getTitle()).callbackData("album"),
                    new InlineKeyboardButton("Photos").callbackData(album.getId().toString())
            );
        }
        markup.addRow(new InlineKeyboardButton("Back").callbackData("back"));
        return markup;
    }

    private static Keyboard generateMarkUpForUser() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.addRow(
                new InlineKeyboardButton("Album").callbackData("albums"),
                new InlineKeyboardButton("Todos").callbackData("todo"),
                new InlineKeyboardButton("Post").callbackData("post")
        );
        markup.addRow(new InlineKeyboardButton("Back").callbackData("back"));
        return markup;
    }

    private static User getUser(String data) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/users/"+data))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        Gson gson = new Gson();
        return gson.fromJson(body, User.class);
    }

    private static Keyboard generateMarkUpForUsers() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/users"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        Gson gson = new Gson();
        User[] users = gson.fromJson(body, User[].class);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (User user : users) {
            markup.addRow(
                    new InlineKeyboardButton(user.getName()).callbackData("name"),
                    new InlineKeyboardButton("GO").callbackData(user.getId().toString())
            );
        }
        return markup;
    }

    private static TgUser getOrCreat(Long chatId) {
        for (TgUser user : DB.USERS) {
            if(user.getChatId().equals(chatId)) {
                return user;
            }
        }
        TgUser user1 = new TgUser();
        user1.setChatId(chatId);
        DB.USERS.add(user1);
        return user1;
    }

}
