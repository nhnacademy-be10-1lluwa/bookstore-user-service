package com.nhnacademy.illuwa.domain.memberaddress.utils;

import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.memberaddress.dto.MemberAddressRequest;
import com.nhnacademy.illuwa.domain.memberaddress.dto.MemberAddressResponse;
import com.nhnacademy.illuwa.domain.memberaddress.entity.MemberAddress;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-30T18:04:26+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class MemberAddressMapperImpl implements MemberAddressMapper {

    @Override
    public MemberAddress toEntity(MemberAddressRequest request, Member member) {
        if ( request == null && member == null ) {
            return null;
        }

        MemberAddress.MemberAddressBuilder memberAddress = MemberAddress.builder();

        if ( request != null ) {
            memberAddress.postCode( request.getPostCode() );
            memberAddress.addressName( request.getAddressName() );
            memberAddress.address( request.getAddress() );
            memberAddress.detailAddress( request.getDetailAddress() );
            memberAddress.defaultAddress( request.isDefaultAddress() );
            memberAddress.recipientName( request.getRecipientName() );
            memberAddress.recipientContact( request.getRecipientContact() );
        }
        memberAddress.member( member );

        return memberAddress.build();
    }

    @Override
    public MemberAddressResponse toDto(MemberAddress memberAddress) {
        if ( memberAddress == null ) {
            return null;
        }

        MemberAddressResponse.MemberAddressResponseBuilder memberAddressResponse = MemberAddressResponse.builder();

        memberAddressResponse.memberAddressId( memberAddress.getMemberAddressId() );
        memberAddressResponse.addressName( memberAddress.getAddressName() );
        memberAddressResponse.recipientName( memberAddress.getRecipientName() );
        memberAddressResponse.recipientContact( memberAddress.getRecipientContact() );
        memberAddressResponse.postCode( memberAddress.getPostCode() );
        memberAddressResponse.address( memberAddress.getAddress() );
        memberAddressResponse.detailAddress( memberAddress.getDetailAddress() );
        memberAddressResponse.defaultAddress( memberAddress.isDefaultAddress() );

        return memberAddressResponse.build();
    }

    @Override
    public MemberAddress updateMemberAddress(MemberAddress target, MemberAddressRequest request) {
        if ( request == null ) {
            return target;
        }

        if ( request.getPostCode() != null ) {
            target.setPostCode( request.getPostCode() );
        }
        if ( request.getAddressName() != null ) {
            target.setAddressName( request.getAddressName() );
        }
        if ( request.getAddress() != null ) {
            target.setAddress( request.getAddress() );
        }
        if ( request.getDetailAddress() != null ) {
            target.setDetailAddress( request.getDetailAddress() );
        }
        target.setDefaultAddress( request.isDefaultAddress() );
        if ( request.getRecipientName() != null ) {
            target.setRecipientName( request.getRecipientName() );
        }
        if ( request.getRecipientContact() != null ) {
            target.setRecipientContact( request.getRecipientContact() );
        }

        return target;
    }
}
