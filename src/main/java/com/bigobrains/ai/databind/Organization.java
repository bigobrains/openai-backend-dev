package com.bigobrains.ai.databind;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Organization {

    private String name;
    private String description;
    private Headquarters headquarters;
    private List<Department> departments;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Headquarters {

        private String country;
        private String city;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Department {

        private String name;
        private String description;
        private List<Service> services;

        @Data
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Service {

            private String type;
            private String description;
            private List<Work> works;

            @Data
            @NoArgsConstructor
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Work {

                private String workFlowId;
                private String workId;
                private String conditional;
                private String userText;

                @JsonDeserialize(using = SchemaSerializer.class)
                private String schema;
            }
        }
    }
}
