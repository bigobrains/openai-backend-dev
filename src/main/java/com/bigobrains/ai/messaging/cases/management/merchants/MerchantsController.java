package com.bigobrains.ai.messaging.cases.management.merchants;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MerchantsController {

    private final MerchantRepository merchantRepository;


    public MerchantsController(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    @GetMapping(value = "/merchants")
    public List<Merchant> getMerchants() {
        return merchantRepository.findAll();
    }
}
