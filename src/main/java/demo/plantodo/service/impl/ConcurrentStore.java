package demo.plantodo.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentStore {
    private Map<String, String> store;

    public Map<String, String> getStore() {
        if (store == null) {
            store = new ConcurrentHashMap<>();
        }
        return store;
    }
}
