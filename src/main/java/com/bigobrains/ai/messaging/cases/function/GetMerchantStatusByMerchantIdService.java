package com.bigobrains.ai.messaging.cases.function;

import com.bigobrains.ai.messaging.cases.management.merchants.Merchant;
import com.bigobrains.ai.messaging.cases.management.merchants.MerchantRepository;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class GetMerchantStatusByMerchantIdService implements Function<GetMerchantStatusByMerchantIdService.RequestEntity, GetMerchantStatusByMerchantIdService.ResponseEntity> {

    private final MerchantRepository merchantRepository;
    private static final Logger LOG = LoggerFactory.getLogger(GetMerchantStatusByMerchantIdService.class);

    public GetMerchantStatusByMerchantIdService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    @Override
    public GetMerchantStatusByMerchantIdService.ResponseEntity apply(GetMerchantStatusByMerchantIdService.RequestEntity requestEntity) {
        if (requestEntity.merchantId != null && !requestEntity.merchantId.isEmpty()) {
            Merchant merchant = merchantRepository.findById(requestEntity.merchantId);
            if (merchant != null) {
                LOG.info("MerchantId: {}, Status: {}", requestEntity.merchantId, merchant.getStatus());
                return new ResponseEntity(merchant.getStatus());
            }
        }

        LOG.info("MerchantId: {}, Status: {}", requestEntity.merchantId, Merchant.Status.NONE);
        return new ResponseEntity(Merchant.Status.NONE);
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
    @JsonClassDescription("status: Merchant status")
    public static class ResponseEntity {
        private Merchant.Status status;
    }
}
