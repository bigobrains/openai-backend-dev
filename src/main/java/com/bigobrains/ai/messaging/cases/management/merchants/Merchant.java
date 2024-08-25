package com.bigobrains.ai.messaging.cases.management.merchants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Merchant {

    private String id;
    private String name;
    private Merchant.Status status;
    private String zipCode;
    private String reasonCode;
    private String updatedBy;

    public enum Status {
        OPEN,
        HOLD,
        REOPEN,
        CLOSED,
        NONE
    }
}
