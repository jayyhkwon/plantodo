package demo.plantodo.service.impl;

import demo.plantodo.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements CacheService {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public String get(String key, String nv) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(String.valueOf(key))).orElse(nv);
    }

    @Override
    public void set(String key, String v) {
        redisTemplate.opsForValue().set(String.valueOf(key), v);
    }

}
