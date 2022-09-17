/*
 *
 * Code With Abel
 *
 * This code is free software; you can redistribute it and/or modify it
 *
 */
package com.codewithabel.TwitterClient.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Search {
    List<Tweets> data;
    Meta meta;
    Includes includes;
}
