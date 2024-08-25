package com.bigobrains.ai.messaging.cases.management.merchants;

import java.util.List;

public interface MerchantRepository {

    List<Merchant> MERCHANTS = List.of(
            new Merchant("46601189", "United Diagnostics", Merchant.Status.OPEN, "53186", "NA", "CUSTOMER_SERVICE"),
            new Merchant("75347815", "Titan Staffing Agency", Merchant.Status.OPEN, "66141", "NA", "CUSTOMER_SERVICE"),
            new Merchant("44133656", "Vertex Solutions Inc.", Merchant.Status.CLOSED, "27609", "FR", "COLLECTIONS")
    );

    default Merchant findById(String id) {
        return MERCHANTS.stream().filter(o -> o.getId().equals(id)).findAny().orElse(null);
    }

    default List<Merchant> findAll() {
        return MERCHANTS;
    }
}
