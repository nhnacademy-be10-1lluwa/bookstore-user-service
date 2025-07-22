package com.nhnacademy.illuwa.domain.guest.service.impl;

import com.nhnacademy.illuwa.common.exception.InvalidInputException;
import com.nhnacademy.illuwa.domain.guest.dto.GuestLoginRequest;
import com.nhnacademy.illuwa.domain.guest.dto.GuestOrderRequest;
import com.nhnacademy.illuwa.domain.guest.dto.GuestResponse;
import com.nhnacademy.illuwa.domain.guest.entity.Guest;
import com.nhnacademy.illuwa.domain.guest.exception.GuestNotFoundException;
import com.nhnacademy.illuwa.domain.guest.repo.GuestRepository;
import com.nhnacademy.illuwa.domain.guest.service.GuestService;
import com.nhnacademy.illuwa.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class GuestServiceImpl implements GuestService {
    private final GuestRepository guestRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageService messageService;

    @Override
    public GuestResponse createGuest(GuestOrderRequest request){
        Guest guest = Guest.builder()
                .guestId(request.getGuestId())
                .orderId(request.getOrderId())
                .orderNumber(request.getOrderNumber())
                .orderPassword(passwordEncoder.encode(request.getOrderPassword()))
                .name(request.getName())
                .email(request.getEmail())
                .contact(request.getContact())
                .build();

        if(Objects.isNull(guest)){
            throw new GuestNotFoundException("주문자 정보 등록에 실패했습니다");
        }
        messageService.sendOrderMessage(request.getName(), request.getOrderNumber());
        return GuestResponse.from(guestRepository.save(guest));
    }

    @Transactional(readOnly = true)
    @Override
    public GuestResponse getGuest(GuestLoginRequest request) {
        Guest guest = guestRepository.findGuestByOrderNumber(request.getOrderNumber())
                .orElseThrow(() -> new GuestNotFoundException("해당 주문번호에 대한 정보를 찾을 수 없습니다."));
        if (!passwordEncoder.matches(request.getOrderPassword(), guest.getOrderPassword())){
            throw new InvalidInputException("주문조회 비밀번호가 일치하지 않습니다.");
        }
        return GuestResponse.from(guest);
    }
}
