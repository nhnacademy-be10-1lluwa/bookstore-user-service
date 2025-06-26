package com.nhnacademy.illuwa.tmpfront;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberAddressViewController {
    @GetMapping("/address-form")
    public String addressPage() {
        return "address";
    }
}
