package com.bigobrains.ai.messaging.cases;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface CaseRepository {

    Map<String, Case> CASES = new LinkedHashMap<>();

    default Case save(Case o) {
        CASES.put(o.getCaseId(), o);
        return o;
    }

    default List<Case> findAll() {
        return CASES.values().stream().toList();
    }

    default Case findById(String id) {
        return CASES.entrySet().stream().filter(o -> o.getKey().equals(id)).map(Map.Entry::getValue).findAny().orElse(null);
    }
}
