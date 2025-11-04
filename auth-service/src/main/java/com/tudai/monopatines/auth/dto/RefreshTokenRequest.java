package com.tudai.monopatines.auth.dto;

/**
 * DTO para solicitud de refresh token.
 * El refresh token se envia mediante cookie HTTP-only.
 * Este DTO puede estar vacio ya que el token viene en la cookie.
 * 
 */
public class RefreshTokenRequest {

    public RefreshTokenRequest() {
    }
}

