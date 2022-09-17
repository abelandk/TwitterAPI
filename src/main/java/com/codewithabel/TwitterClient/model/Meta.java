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

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Meta {
    String newest_id;
    String oldest_id;
    String result_count;
}
