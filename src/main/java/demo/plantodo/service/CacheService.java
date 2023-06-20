package demo.plantodo.service;

public interface CacheService {
    boolean hasKey(String key);
    String get(String key, String nv);
    void set(String key, String v);
}
