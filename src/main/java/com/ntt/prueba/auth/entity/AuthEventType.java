package com.ntt.prueba.auth.entity;

public enum AuthEventType {

    LOGIN,
    LOGOUT,
    TOKEN_REFRESH,
    TOKEN_EXPIRED,
    FAILED_LOGIN,
    TOKEN_REVOKED
}
