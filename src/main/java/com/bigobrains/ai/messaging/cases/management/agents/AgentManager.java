package com.bigobrains.ai.messaging.cases.management.agents;

import com.bigobrains.ai.messaging.cases.Case;
import com.bigobrains.ai.utils.RandomId;

import java.util.Map;
import java.util.Random;

public interface AgentManager {

    Random RANDOM = new Random();
    Map<Integer, Case.Agent> AGENTS = Map.of(
            0, new Case.Agent(RandomId.nextId(), "Lee Chambers"),
            1, new Case.Agent(RandomId.nextId(), "Lincoln Hickman"),
            2, new Case.Agent(RandomId.nextId(), "Stacy Patterson")
    );

    default Case.Agent findNext() {
        return AGENTS.get(RANDOM.nextInt(2));
    }
}
