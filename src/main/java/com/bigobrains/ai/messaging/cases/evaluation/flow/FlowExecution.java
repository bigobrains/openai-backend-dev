package com.bigobrains.ai.messaging.cases.evaluation.flow;

import java.util.Map;

public interface FlowExecution {

    Object getVariable(String key);
    void setVariable(String key, Object value);
    Map<String, Object> getVariables();

    static FlowExecution with(Variables variables) {
        return new FlowExecution() {

            private Variables variables;

            @Override
            public Object getVariable(String key) {
                return this.variables.getVariable(key);
            }

            @Override
            public void setVariable(String key, Object value) {
                this.variables.setVariable(key, value);
            }

            @Override
            public Map<String, Object> getVariables() {
                return this.variables.getVariables();
            }

            FlowExecution init(Variables variables) {
                this.variables = variables;
                return this;
            }
        }.init(variables);
    }

    final class Variables {

        private final Map<String, Object> variables;

        private Variables(Map<String, Object> variables) {
            this.variables = variables;
        }

        public static Variables of(Map<String, Object> variables) {
            return new Variables(variables);
        }

        public Object getVariable(String key) {
            return this.variables.get(key);
        }

        public Map<String, Object> getVariables() {
            return this.variables;
        }

        public void setVariable(String key, Object value) {
            this.variables.put(key, value);
        }
    }
}
