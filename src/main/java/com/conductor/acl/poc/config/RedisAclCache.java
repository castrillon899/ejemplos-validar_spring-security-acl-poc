package com.conductor.acl.poc.config;

import java.io.Serializable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.ObjectIdentity;

public class RedisAclCache implements AclCache {

    private final RedisTemplate<ObjectIdentity, MutableAcl> redisTemplate;

    public RedisAclCache(RedisTemplate<ObjectIdentity, MutableAcl> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public MutableAcl getFromCache(ObjectIdentity objectIdentity) {
        return redisTemplate.opsForValue().get(objectIdentity);
    }

    @Override
    public MutableAcl getFromCache(Serializable pk) {
        return null;
    }

    @Override
    public void putInCache(MutableAcl acl) {
        redisTemplate.opsForValue().set(acl.getObjectIdentity(), acl);
    }

    @Override
    public void evictFromCache(Serializable pk) {

    }

    @Override
    public void evictFromCache(ObjectIdentity objectIdentity) {
        redisTemplate.delete(objectIdentity);
    }

    @Override
    public void clearCache() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }
}