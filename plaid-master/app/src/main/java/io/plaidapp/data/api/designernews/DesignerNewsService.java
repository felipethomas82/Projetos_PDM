/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.plaidapp.data.api.designernews;

import java.util.Map;

import io.plaidapp.data.api.designernews.model.AccessToken;
import io.plaidapp.data.api.designernews.model.Comment;
import io.plaidapp.data.api.designernews.model.NewStoryRequest;
import io.plaidapp.data.api.designernews.model.StoriesResponse;
import io.plaidapp.data.api.designernews.model.StoryResponse;
import io.plaidapp.data.api.designernews.model.UserResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Models the Designer News API.
 *
 * v1 docs: https://github.com/layervault/dn_api
 * v2 docs: https://github.com/DesignerNews/dn_api_v2
 */
public interface DesignerNewsService {

    String ENDPOINT = "https://www.designernews.co/";

    @GET("api/v1/stories")
    Call<StoriesResponse> getTopStories(@Query("page") Integer page);

    @GET("api/v1/stories/recent")
    Call<StoriesResponse> getRecentStories(@Query("page") Integer page);

    @GET("api/v1/stories/search")
    Call<StoriesResponse> search(@Query("query") String query,
                                 @Query("page") Integer page);

    @FormUrlEncoded
    @POST("oauth/token")
    Call<AccessToken> login(@FieldMap() Map<String, String> loginParams);

    @GET("api/v1/me")
    Call<UserResponse> getAuthedUser();

    @POST("api/v1/stories/{id}/upvote")
    Call<StoryResponse> upvoteStory(@Path("id") long storyId);

    @Headers("Content-Type: application/vnd.api+json")
    @POST("api/v2/stories")
    Call<StoriesResponse> postStory(@Body NewStoryRequest story);

    @FormUrlEncoded
    @POST("api/v1/stories/{id}/reply")
    Call<Comment> comment(@Path("id") long storyId,
                          @Field("comment[body]") String comment);

    @FormUrlEncoded
    @POST("api/v1/comments/{id}/reply")
    Call<Comment> replyToComment(@Path("id") long commentId,
                                 @Field("comment[body]") String comment);

    @POST("api/v1/comments/{id}/upvote")
    Call<Comment> upvoteComment(@Path("id") long commentId);

}
