package com.bigobrains.ai.messaging.cases.function;

import com.bigobrains.ai.messaging.cases.management.merchants.Merchant;
import com.bigobrains.ai.messaging.cases.management.merchants.MerchantRepository;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Function;

public class GetMerchantDetailsByMerchantId implements Function<GetMerchantDetailsByMerchantId.RequestEntity, GetMerchantDetailsByMerchantId.ResponseEntity> {

    private final MerchantRepository merchantRepository;

    public GetMerchantDetailsByMerchantId(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    @Override
    public ResponseEntity apply(RequestEntity requestEntity) {
        return new ResponseEntity(merchantRepository.findById(requestEntity.merchantId));
    }

    @JsonClassDescription("merchantId: MerchantId or MID.")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestEntity {
        private String merchantId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonClassDescription("""
            merchant: Object describing the Merchant Account Details
            merchant.id: Merchant's id
            merchant.name: Merchant Name
            merchant.status: Merchant's Account status
            merchant.zipCode: Zipcode
            merchant.reasonCode: Reason for the last status change
            merchant.updatedBy: The department that updated the status last
            """)
    public static class ResponseEntity {
        private Merchant merchant;
    }
}
