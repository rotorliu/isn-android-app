package com.isn.services;

import com.isn.models.Friend;
import com.isn.models.IdResult;
import com.isn.models.Message;
import com.isn.models.MessageComment;
import com.isn.models.Page;
import com.isn.models.User;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;


/**
 * User Service Interface
 */
public interface UserService {
    @DELETE("/users/{userId}")
    void delete(@Path("userId") long userId);

    @GET("/users/{userId}")
    User get(@Path("userId") long userId);

    @POST("/users/register")
    IdResult register(@Query("mobile") String mobile,
                         @Query("name") String name,
                         @Query("password") String password,
                         @Query("verificationCode") String verificationCode);

    @GET("/users/login")
    IdResult login(@Query("mobile") String mobile, @Query("password") String password);

    @GET("/users/getUserByMobile")
    User getUserByMobile(@Query("mobile") String mobile);

    @PUT("/users")
    void update(@Body User user);

    @GET("/users/requestVerificationCode")
    void requestVerificationCode(@Query("mobile") String mobile);

    @GET("/users/{userId}/friends")
    Page<Friend> getMyFriends(@Path("userId") long userId, @Query("page") int page,  @Query("size") int size);

    @GET("/users/{userId}/friends/{friendId}")
    Friend getMyFriend(@Path("userId") long userId, @Path("friendId") long friendId);

    @POST("/users/{userId}/friends")
    IdResult createMyFriend(@Path("userId") long userId, @Body Friend friend);

    @PUT("/users/{userId}/friends")
    void updateMyFriend(@Path("userId") long userId, @Body Friend friend);

    @DELETE("/users/{userId}/friends/{friendId}")
    void deleteMyFriend(@Path("userId") long userId, @Path("friendId") long friendId);

    @GET("/users/{userId}/inmessages")
    Page<Message> getMyReceivedMessages(@Path("userId") long userId, @Query("page") int page, @Query("size") int size);

    @GET("/users/{userId}/outmessages")
    Page<Message> getMySendMessages(@Path("userId") long userId, @Query("page") int page, @Query("size") int size);

    @GET("/users/{userId}/inbox")
    Page<Message> getMyInBoxMessages(@Path("userId") long userId, @Query("page") int page, @Query("size") int size);

    @GET("/users/{userId}/outbox")
    Page<Message> getMyOutBoxMessages(@Path("userId") long userId, @Query("page") int page, @Query("size") int size);

    @DELETE("/users/{userId}/inbox/{messageId}")
    void removeMessageFromMyInBox(@Path("userId") long userId, @Path("messageId") long messageId);

    @DELETE("/users/{userId}/outbox/{messageId}")
    void removeMessageFromMyOutBox(@Path("userId") long userId, @Path("messageId") long messageId);

    @GET("/users/{userId}/comments")
    Page<MessageComment> getMyComments(@Path("userId") long userId, @Query("page") int page, @Query("size") int size);
}
