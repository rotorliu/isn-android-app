package com.isn.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.isn.models.Friend;
import com.isn.models.IdResult;
import com.isn.models.Message;
import com.isn.models.MessageComment;
import com.isn.models.MessageLock;
import com.isn.models.Page;
import com.isn.models.User;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class Client {

    private static Client instance = new Client();

    private static final String BASE_URL = "http://192.168.31.219:8080";
    private UserService userService;
    private MessageService messageService;

    private Client() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_URL)
                .setConverter(new GsonConverter(gson))
                .build();

        userService = restAdapter.create(UserService.class);
        messageService = restAdapter.create(MessageService.class);
    }

    public static synchronized Client getInstance() {
        return instance;
    }

    //User Service APIs
    public void requestVerificationCode(String mobile) {
        userService.requestVerificationCode(mobile);
    }

    public long register(String mobile, String name, String password, String verificationCode) {
        return userService.register(mobile, name, password, verificationCode).getId();
    }

    public long login(String mobile, String password) {
        return userService.login(mobile, password).getId();
    }

    public User getUser(long userId) {
        return userService.get(userId);
    }

    public void deleteUser(long userId) {
        userService.delete(userId);
    }

    public User getUserByMobile(String mobile) {
        return userService.getUserByMobile(mobile);
    }

    public void updateUser(User user) {
        userService.update(user);
    }

    public Page<Friend> getFriendsOfUser(long userId, int page, int size) {
        return userService.getMyFriends(userId, page, size);
    }

    public Friend getFriendOfUser(long userId, long friendId) {
        return userService.getMyFriend(userId, friendId);
    }

    public long createFriendOfUser(long userId, Friend friend) {
        return userService.createMyFriend(userId, friend).getId();
    }

    public void updateFriendOfUser(long userId, Friend friend) {
        userService.updateMyFriend(userId, friend);
    }

    public void deleteFriendOfUser(long userId, long friendId) {
        userService.deleteMyFriend(userId, friendId);
    }

    public Page<Message> getUserReceivedMessages(long userId, int page, int size) {
        return userService.getMyReceivedMessages(userId, page, size);
    }

    public Page<Message> getUserSendMessages(long userId, int page, int size) {
        return userService.getMySendMessages(userId, page, size);
    }

    public Page<Message> getUserInBoxMessages(long userId, int page, int size) {
        return userService.getMyInBoxMessages(userId, page, size);
    }

    public Page<Message> getUserOutBoxMessages(long userId, int page, int size) {
        return userService.getMyOutBoxMessages(userId, page, size);
    }

    public void removeMessageFromUserInBox(long userId, long messageId) {
        userService.removeMessageFromMyInBox(userId, messageId);
    }

    public void removeMessageFromUserOutBox(long userId, long messageId) {
        userService.removeMessageFromMyOutBox(userId, messageId);
    }

    public Page<MessageComment> getCommentsOfUser(long userId, int page,  int size) {
        return userService.getMyComments(userId, page, size);
    }

    //Message Service APIs
    public Message getMessage(long messageId) {
        return messageService.get(messageId);
    }

    public IdResult createMessage(Message message) {
        return messageService.create(message);
    }

    public void deleteMessage(long messageId) {
        messageService.delete(messageId);
    }

    public void setMessageSender(long messageId, long senderUserId) {
        messageService.setSender(messageId, senderUserId);
    }

    public void addMessageReceiver(long messageId, long receiverFriendId) {
        messageService.addReceiver(messageId, receiverFriendId);
    }

    public IdResult createMessageLock(long messageId, MessageLock lock) {
        return messageService.createMyLock(messageId, lock);
    }

    public void deleteMessageLock(long messageId, long lockId) {
        messageService.deleteMyLock(messageId, lockId);
    }

    public List<MessageLock> getMessageLocks(long messageId) {
        return messageService.getMyLocks(messageId);
    }

    public IdResult createMessageComment(long messageId, long commenterUserId, MessageComment comment) {
        return messageService.createMyComment(messageId, commenterUserId, comment);
    }

    public void deleteMessageComment(long messageId, long commenterUserId, long commentId) {
        messageService.deleteMyComment(messageId, commenterUserId, commentId);
    }

    public Page<MessageComment> getMessageComments(long messageId, int page, int size) {
        return messageService.getMyComments(messageId, page, size);
    }
}
