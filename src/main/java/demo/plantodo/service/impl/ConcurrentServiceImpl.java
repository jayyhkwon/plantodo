package demo.plantodo.service.impl;

import demo.plantodo.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ConcurrentServiceImpl implements CacheService {

    private final Map<String, String> store;

    @Override
    public boolean hasKey(String key) {
        return store.containsKey(key);
    }

    @Override
    public String get(String key, String nv) {
        return hasKey(key) ? store.get(key) : nv;
    }

    @Override
    public void set(String key, String v) {
        store.remove(key);
        store.put(key, v);
    }
}
