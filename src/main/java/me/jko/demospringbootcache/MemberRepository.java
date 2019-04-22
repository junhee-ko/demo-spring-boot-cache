package me.jko.demospringbootcache;

public interface MemberRepository {
    Member findByNameNoCache(String name);
    Member findByNameCacheWithOneKey(String name);
    Member findByNameCacheWithAllKey(String name, String testKey);
    void refresh(String name);
}
