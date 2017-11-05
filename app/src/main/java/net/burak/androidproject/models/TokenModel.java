package net.burak.androidproject.models;

import lombok.Data;

@Data
public class TokenModel {
    private String userId;
    private Long exp;
}
