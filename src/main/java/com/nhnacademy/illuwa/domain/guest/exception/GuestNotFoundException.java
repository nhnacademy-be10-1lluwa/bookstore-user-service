package com.nhnacademy.illuwa.domain.guest.exception;

public class GuestNotFoundException extends RuntimeException {
    public GuestNotFoundException(Long guestId) {
        super("해당 비회원을 찾을 수 없습니다: " + guestId);
    }
}