package com.isn.services;

import com.isn.models.IdResult;
import com.isn.models.Message;
import com.isn.models.MessageComment;
import com.isn.models.MessageLock;
import com.isn.models.Page;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Message Service Interface
 */
public interface MessageService {

    @GET("/messages/{messageId}")
    Message get(@Path("messageId") long messageId);

    @POST("/messages")
    IdResult create(@Body Message message);

    @DELETE("/messages/{messageId}")
    void delete(@Path("messageId") long messageId);

    @PUT("/messages/{messageId}/sender/{senderUserId}")
    void setSender(@Path("messageId") long messageId, @Path("senderUserId") long senderUserId);

    @PUT("/messages/{messageId}/receivers/{receiverFriendId}")
    void addReceiver(@Path("messageId") long messageId, @Path("receiverFriendId") long receiverFriendId);

    @POST("/messages/{messageId}/locks")
    IdResult createMyLock(@Path("messageId") long messageId, @Body MessageLock lock);

    @DELETE("/messages/{messageId}/locks/{lockId}")
    void deleteMyLock(@Path("messageId") long messageId, @Path("lockId") long lockId);

    @GET("/messages/{messageId}/locks")
    List<MessageLock> getMyLocks(@Path("messageId") long messageId);

    @POST("/messages/{messageId}/commenters/{commenterUserId}/comments")
    IdResult createMyComment(@Path("messageId") long messageId, @Path("commenterUserId") long commenterUserId, @Body MessageComment comment);

    @DELETE("/messages/{messageId}/commenters/{commenterUserId}/comments/{commentId}")
    void deleteMyComment(@Path("messageId") long messageId, @Path("commenterUserId") long commenterUserId, @Path("commentId") long commentId);

    @GET("/messages/{messageId}/comments")
    Page<MessageComment> getMyComments(@Path("messageId") long messageId, @Query("page") int page, @Query("size") int size);
}
