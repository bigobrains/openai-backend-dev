package com.bigobrains.ai.messaging;

import com.bigobrains.ai.messaging.cases.Case;
import com.bigobrains.ai.messaging.cases.CaseRepository;
import com.bigobrains.ai.messaging.cases.management.agents.AgentManager;
import com.bigobrains.ai.messaging.cases.management.merchants.Merchant;
import com.bigobrains.ai.messaging.cases.management.merchants.MerchantRepository;
import com.bigobrains.ai.messaging.cases.management.users.User;
import com.bigobrains.ai.messaging.cases.management.users.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RepositoryConfigurer {

    @Bean
    public CaseRepository caseRepository() {
        return new CaseRepository() {
            @Override
            public Case save(Case o) {
                return CaseRepository.super.save(o);
            }

            @Override
            public List<Case> findAll() {
                return CaseRepository.super.findAll();
            }

            @Override
            public Case findById(String id) {
                return CaseRepository.super.findById(id);
            }
        };
    }

    @Bean
    public AgentManager agentManager() {
        return new AgentManager() {
            @Override
            public Case.Agent findNext() {
                return AgentManager.super.findNext();
            }
        };
    }

    @Bean
    public UserRepository userRepository() {
        return new UserRepository() {
            @Override
            public User findByEmailId(String emailId) {
                return UserRepository.super.findByEmailId(emailId);
            }
        };
    }

    @Bean
    public MerchantRepository merchantRepository() {
        return new MerchantRepository() {
            @Override
            public Merchant findById(String id) {
                return MerchantRepository.super.findById(id);
            }

            @Override
            public List<Merchant> findAll() {
                return MerchantRepository.super.findAll();
            }
        };
    }
}
