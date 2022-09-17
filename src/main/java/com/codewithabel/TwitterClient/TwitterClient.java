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
import org.springframework.social.twitter.api.Tweet;

import java.util.List;

public interface TwitterClient {
    /**
     * @param token  User Token
     * @param userId User Id of the Twitter Account Searching, Retweeting, Liking, Replying
     */
    void init(String token, String userId);

    /**
     * @param token  User Token
     * @param userId User Id of the Twitter Account Searching, Retweeting, Liking, Replying
     * @param consumerKey The application's consumer key
     * @param consumerSecret The application's consumer secret
     * @param accessToken The access token granted after OAuth authorization
     * @param accessSecret The access token secret granted after OAuth authorization
     */
    void init(String token, String userId, String consumerKey, String consumerSecret, String accessToken, String accessSecret);

    /**
     * @param consumerKey The application's consumer key
     * @param consumerSecret The application's consumer secret
     * @param accessToken The access token granted after OAuth authorization
     * @param accessSecret The access token secret granted after OAuth authorization
     */
    void init(String consumerKey, String consumerSecret, String accessToken, String accessSecret);

    /**
     * Retweet Tweets (Endpoint: https://api.twitter.com/2/users/:id/retweets)
     *
     * @param tweetId The ID of the Tweet that you would like the user to Retweet.
     * @return Response Entity of The Retweet
     */
    ResponseEntity<Retweet> retweet(String tweetId);

    /**
     * Likes Tweets (Endpoint: https://api.twitter.com/2/users/:id/likes)
     * @param tweetId The ID of the Tweet that you would like the user to Like.
     * @return Response Entity of The Like
     */
    ResponseEntity<Like> like(String tweetId);

    /**
     * Tweet (Endpoint: https://api.twitter.com/2/tweets)
     * @param text Text to Tweet
     * @return Response Entity of The tweet
     */
    ResponseEntity<TweetedPost> tweet(String text);

    /**
     * Comment to a Tweet (Endpoint: https://api.twitter.com/2/tweets)
     * @param tweetId Tweet Id to comment to
     * @return ResponseEntity of The Commented Tweet
     */
    ResponseEntity<TweetedPost> comment(String tweetId, String text);

    /**
     * The Twitter Search API searches against a sampling of recent Tweets published in the past 7 days.
     * Auth: Twitter Oauth 1.0, app-only or app-user
     * <a href="https://developer.twitter.com/en/docs/twitter-api/v1/tweets/search/api-reference/get-search-tweets">Documentation</a>
     * @param query The search Query For example:
     *             "#Cats"
     *              <a href="https://developer.twitter.com/en/docs/twitter-api/v1/tweets/search/guides/standard-operators">
     *                  twitters official page For A complete Search Operators </a>
     *
     * @param filterRetweets True To filter out Retweets, False to include retweets in the result
     * @return List of Tweets published in the past 7 days satisfying the search query
     */
    List<Tweet> searchOuth1(String query, boolean filterRetweets);

    /**
     * The Twitter Search API searches against a sampling of recent Tweets published in the past 7 days.
     * Auth: Twitter Oauth 1.0, app-only or app-user
     * <a href="https://developer.twitter.com/en/docs/twitter-api/tweets/search/api-reference/get-tweets-search-recent">Documentation</a>
     *
     * @param query          The search Query For example:
     *                       "#Cats"
     *                       <a href="https://developer.twitter.com/en/docs/twitter-api/v1/tweets/search/guides/standard-operators">
     *                       twitters official page For A complete Search Operators </a>
     * @param filterRetweets True To filter out Retweets, False to include retweets in the result
     * @return ResponseEntity of The search Results
     */
    ResponseEntity<Search> searchOuth2(String query, boolean filterRetweets);

    /**
     * Gets the URL that redirects USERS to authorize your APP.
     * After the user has approved the app, Authorization code will be sent to the redirect URL, which can later be used to get Access Token
     *  Example URL with read, write and offline access:
     *
     *      <h3>https://twitter.com/i/oauth2/authorize?response_type=code&client_id=M1M5R3BMVy13QmpScXkzTUt5OE46MTpjaQ&redirect_uri=https://www.example.com&scope=tweet.read%20users.read%20follows.read%20follows.write%20offline.access&state=state&code_challenge=challenge&code_challenge_method=plain</h3>
     * @param clientId OAuth 2.0 Client ID
     *                 (If you have enabled OAuth 2.0 for your App you can find your Client ID inside your App’s “Keys and Tokens” page)
     * @param redirectUrl The Url where Twitter sends the Authorization code (example: http://127.0.0.1:8080/twitter/authorization/callback)
     * @param readAccess True to have read access, False otherwise
     * @param writeAccess True to have write access, False otherwise
     * @param offlineAccess True to have offline access, False otherwise
     * @return The URL That redirects USERS to authorize the APP
     */
    String getAuthorizeUrl(String clientId,
                           String redirectUrl,
                           boolean readAccess,
                           boolean writeAccess,
                           boolean offlineAccess);

    /**
     * Returns Response Entity of Access Token
     * @param authorizationCode Authorization Code
     * @param clientId OAuth 2.0 Client ID
     * @param clientSecret OAuth 2.0 Client SECRET
     * @param redirectUrl Redirect URL
     * @return Response Entity of Access Token
     */
    ResponseEntity<Token> getAccessToken(String authorizationCode,
                                         String clientId,
                                         String clientSecret,
                                         String redirectUrl);

    /**
     * Get Response Entity of Token
     * @param clientId OAuth 2.0 Client ID
     * @param clientSecret OAuth 2.0 Client SECRET
     * @param refreshToken Refresh Token fetched when getting access token.
     *                     offline access level should have been Granted for the app to get the refresh token
     * @return Response Entity of Access Token
     */
    ResponseEntity<Token> refreshToken(String clientId,
                                       String clientSecret,
                                       String refreshToken);
}
