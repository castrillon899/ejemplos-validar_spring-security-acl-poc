package com.conductor.acl.poc.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionCacheOptimizer;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;

@Configuration
public class AclConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    RedisConnectionFactory connectionFactory;

    @Bean
    public RedisTemplate<ObjectIdentity, MutableAcl> aclRedisTemplate() {
        RedisTemplate<ObjectIdentity, MutableAcl> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Configurar el ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixIn(MutableAcl.class, MutableAclMixin.class);

        // Configurar los serializadores con el ObjectMapper
        Jackson2JsonRedisSerializer<ObjectIdentity> keySerializer = new Jackson2JsonRedisSerializer<>(ObjectIdentity.class);
        keySerializer.setObjectMapper(mapper);

        Jackson2JsonRedisSerializer<MutableAcl> valueSerializer = new Jackson2JsonRedisSerializer<>(MutableAcl.class);
        valueSerializer.setObjectMapper(mapper);

        template.setKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);

        template.afterPropertiesSet(); // Importante: llamar a afterPropertiesSet para inicializar el template

        return template;
    }

    // Define a Mixin to ignore the problematic properties
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    abstract static class MutableAclMixin {
        @JsonIgnore
        abstract List<AccessControlEntry> getEntries();
    }

    @Bean
    public AclCache aclCache() {
        return new RedisAclCache(aclRedisTemplate());
    }

    @Bean
    public MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        AclPermissionEvaluator permissionEvaluator = new AclPermissionEvaluator(aclService());
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        expressionHandler.setPermissionCacheOptimizer(new AclPermissionCacheOptimizer(aclService()));
        return expressionHandler;
    }

    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    @Bean
    public LookupStrategy lookupStrategy() {
        return new BasicLookupStrategy(dataSource, aclCache(), aclAuthorizationStrategy(), permissionGrantingStrategy());
    }

    @Bean
    public JdbcMutableAclService aclService() {
        return new JdbcMutableAclService(dataSource, lookupStrategy(), aclCache());
    }
}