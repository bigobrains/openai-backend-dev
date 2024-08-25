package com.bigobrains.ai.messaging.cases;

import com.bigobrains.ai.messaging.cases.evaluation.flow.WorkFlow;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.binary.Base64;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Case {

    public static final String STANDARD = "REQUEST";
    public static final String ALIAS = "case";

    @Schema(example = "92534270")
    private String caseId;

    @Schema(example = "ADDRESS_CHANGE")
    private String caseType;

    @Schema(example = "barvind88@gmail.com")
    private String solicitorId;

    @Schema(example = "Fw: Address change request")
    private String subject;

    @Schema(example = "2024-08-15T14:41:01.042547100")
    private String createdAt;

    @Schema(example = "DQpJIHdvdWxkIGxpa2UgdG8gY2hhbmdlIHRoZSBhZGRyZXNzIG9uIHRoZSBmaWxlIHRvIGJlbG93LiBNZXJjaGFudElEOiAgMDAxNDcyOTk5MzM1ODk3NDk3MQ0KDQpOZXcgQWRkcmVzcw0KPT09PT09PT09PT09PT09PQ0KMTUwIFcgTWFpbiBTdCwgQXB0I3MiwgV2F1a2VzaGEuIFdJIC0gNTMxODYuDQoNClRoYW5rcw0KQXJ2aW5kIEINCg==")
    private String data;

    @Schema(example = "ACKNOWLEDGED")
    private String status;

    @Schema(example = "Lee Chambers")
    private String assignedTo;

    private WorkFlow workFlow;

    private Set<Clearance> clearances;

    public enum Status {
        ACKNOWLEDGED,
        HOLD,
        ACCEPTED,
        PROCESSED,
        FAILED
    }

    @Override
    public String toString() {
        return "\n" +
                "[caseId=" + caseId + "\n" +
                "caseType=" + caseType + "\n" +
                "solicitorId=" + solicitorId + "\n" +
                "subject=" + subject + "\n" +
                "createdAt=" + createdAt + "\n" +
                "data=" + new String(Base64.decodeBase64(data)) + "\n" +
                "status=" + status + "\n" +
                "assignedTo=" + assignedTo + "\n" +
                "workflow=" + workFlow + "\n" +
                "clearances=" + clearances + "]";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Agent {

        private String id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Clearance {

        private String departmentName;
        private String status;

        @Override
        public String toString() {
            return "Clearance[departmentName=" + departmentName + "\n" +
                    "status=" + status + "]";
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            Clearance clearance = (Clearance) o;
            return this.departmentName.equals(clearance.departmentName);
        }

        @Override
        public int hashCode() {
            return -1;
        }
    }
}
