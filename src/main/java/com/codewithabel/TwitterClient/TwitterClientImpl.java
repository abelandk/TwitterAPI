/*
 *
 * Code With Abel
 *
 * This code is free software; you can redistribute it and/or modify it
 *
 */
package com.codewithabel.TwitterClient;

import com.codewithabel.TwitterClient.model.Like;
import com.codewithabel.TwitterClient.model.Retweet;
import com.codewithabel.TwitterClient.model.Search;
import com.codewithabel.TwitterClient.model.Token;
import com.codewithabel.TwitterClient.model.TweetedPost;
import org.springframework.http.ResponseEntity;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class TwitterClientImpl implements TwitterClient {

    String token;
    String userId;
    String consumerKey;
    String consumerSecret;
    String accessToken;
    String accessSecret;
    private final HttpRequests httpRequests;

    private TwitterClientImpl(HttpRequests httpRequests) {
        this.httpRequests = httpRequests;
    }

    @Override
    public void init(String token, String userId) {
        this.token = token;
        this.userId = userId;
    }

    @Override
    public void init(String consumerKey,
                     String consumerSecret,
                     String accessToken,
                     String accessSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.accessToken = accessToken;
        this.accessSecret = accessSecret;
    }

    @Override
    public void init(String token,
                     String userId,
                     String consumerKey,
                     String consumerSecret,
                     String accessToken,
                     String accessSecret) {
        this.token = token;
        this.userId = userId;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.accessToken = accessToken;
        this.accessSecret = accessSecret;
    }

    @Override
    public ResponseEntity<Retweet> retweet(String tweetId) {
        verifyUser();
        String requestJson = "{\"tweet_id\":\"" + tweetId + "\"}";
        String RETWEET_URL = "https://api.twitter.com/2/users/{id}/retweets";
        return httpRequests.postForEntity(
                token,
                RETWEET_URL,
                requestJson,
                Retweet.class,
                userId
        );
    }

    @Override
    public ResponseEntity<Like> like(String tweetId) {
        verifyUser();
        String requestJson = "{\"tweet_id\":\"" + tweetId + "\"}";
        String LIKE_URL = "https://api.twitter.com/2/users/{id}/likes";
        return httpRequests.postForEntity(
                token,
                LIKE_URL,
                requestJson,
                Like.class,
                userId
        );
    }

    @Override
    public ResponseEntity<TweetedPost> tweet(String text) {
        verifyUser();
        String requestJson = "{\"text\":\"" + text + "\"}";
        return postTweet(requestJson);
    }

    @Override
    public ResponseEntity<TweetedPost> comment(String tweetId, String text) {
        verifyUser();
        String requestJson = "{\"reply\":{\"in_reply_to_tweet_id\":\"" + tweetId + "\"},\"text\":\"" + text + "\"}";
        return postTweet(requestJson);
    }

    @Override
    public List<Tweet> searchOuth1(String query, boolean filterRetweets) {
        verifyApp();

        if(query.isEmpty()) {
            return new ArrayList<>();
        }

        if(filterRetweets) {
            query += " -filter:retweets";
        }

        TwitterTemplate template = new TwitterTemplate(consumerKey,
                consumerSecret,
                accessToken,
                accessSecret
        );

        List<Tweet> tweets = new ArrayList<>();
        SearchResults searchResults = template.searchOperations().search(query);
        if (searchResults.getTweets().isEmpty()) {
            return tweets;
        }

        tweets.addAll(searchResults.getTweets());

        int page = 1;
        while (searchResults.isLastPage()) {
            page++;
            searchResults = template.searchOperations().search(query, page);
            if (searchResults.getTweets().isEmpty()) {
                return tweets;
            }
            tweets.addAll(searchResults.getTweets());
        }

        return tweets;
    }

    @Override
    public ResponseEntity<Search> searchOuth2(String query, boolean filterRetweets) {
        verifyUser();
        String RECENT_SEARCH_URL = "https://api.twitter.com/2/tweets/search/recent";
        return httpRequests.getForEntity(
                token,
                RECENT_SEARCH_URL,
                Search.class
        );
    }

    @Override
    public String getAuthorizeUrl(String clientId,
                                  String redirectUrl,
                                  boolean readAccess,
                                  boolean writeAccess,
                                  boolean offlineAccess) {
        if(clientId.isEmpty() || redirectUrl.isEmpty()) {
            throw new IllegalArgumentException("Please Provide Valid ClientId and RedirectUrl");
        }
        if (!readAccess && !writeAccess) {
            throw new IllegalArgumentException("At Least One Scope Access Should be allowed");
        }

        String scope = "";
        String AUTHORIZE_URL = "https://twitter.com/i/oauth2/authorize";

        if(readAccess) {
            scope = "tweet.read users.read follows.read" ;
        }

        if(writeAccess) {
            scope = scope.isEmpty() ? "tweet.write like.write" : scope + " tweet.write like.write";
        }

        if(offlineAccess) {
            scope += " offline.access";
        }

        return AUTHORIZE_URL +
                "?response_type=code&client_id=" + clientId +
                "&redirect_uri=" + redirectUrl +
                "&scope=" + scope +
                "&state=state&code_challenge=challenge&code_challenge_method=plain";
    }
    @Override
    public ResponseEntity<Token> getAccessToken(String authorizationCode,
                                                String clientId,
                                                String clientSecret,
                                                String redirectUrl) {

        if(clientId.isEmpty() || clientSecret.isEmpty() || authorizationCode.isEmpty() || redirectUrl.isEmpty()) {
            throw new IllegalArgumentException("Please Provide Valid ClientId/ClientSecret, AuthorizationCode and redirectUrl");
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("code", authorizationCode);
        body.add("grant_type", "authorization_code");
        body.add("redirect_uri", redirectUrl);
        body.add("code_verifier", "challenge");

        String TOKEN_URL = "https://api.twitter.com/2/oauth2/token";

        return httpRequests.postForEntity(
                getEncodedClient(clientId, clientSecret),
                TOKEN_URL,
                body,
                Token.class);

    }

    @Override
    public ResponseEntity<Token> refreshToken(String clientId,
                                              String clientSecret,
                                              String refreshToken) {

        if(clientId.isEmpty() || clientSecret.isEmpty() || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("Please Provide Valid ClientId/ClientSecret and refreshToken");
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("refresh_token", refreshToken);
        body.add("grant_type", "refresh_token");

        String TOKEN_URL = "https://api.twitter.com/2/oauth2/token";

        return httpRequests.postForEntity(
                getEncodedClient(clientId, clientSecret),
                TOKEN_URL,
                body,
                Token.class);
    }

    private ResponseEntity<TweetedPost> postTweet(String requestJson) {
        String POST_URL = "https://api.twitter.com/2/tweets";
        return httpRequests.postForEntity(
                token,
                POST_URL,
                requestJson,
                TweetedPost.class
        );
    }

    private void verifyUser() {
        if (token.isEmpty() || userId.isEmpty()) {
            throw new IllegalArgumentException("User Token or User Id not set. Please first set them using init method");
        }
    }

    private void verifyApp() {
        if (accessSecret.isEmpty() || accessToken.isEmpty() || consumerKey.isEmpty() || consumerSecret.isEmpty()) {
            throw new IllegalArgumentException("APP Token/Secret or Consumer Token/Secret not set. Please first set them using init method");
        }
    }

    private String getEncodedClient(String clientId, String clientSecret) {
        byte[] base64 = Base64.getEncoder().encode((clientId + ":" + clientSecret).getBytes());
        return new String(base64);
    }
}
