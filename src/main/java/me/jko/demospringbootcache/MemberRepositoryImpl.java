package me.jko.demospringbootcache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepositoryImpl implements MemberRepository {

    private static Logger logger = LoggerFactory.getLogger(MemberRepositoryImpl.class);

    @Override
    public Member findByNameNoCache(String name) {
        slowQuery(2000);
        return new Member(0, name + "@gmail.com", name);
    }

    @Override
    @Cacheable(value = "findMemberCache", key = "#name")
    // ehcache.xml에서 지정한 findMemberCache 캐시를 사용하겠다는 의미이며,
    // 여기서 key는 메소드 argument인 name을 사용하겠다는 의미이다.
    // 즉, name에 따라 별도로 캐시한다는 의미이다.
    // findByNameCache 메소드의 argument에 따라 캐시되기 때문에 name이 jko인지,
    // test1인지 등 name에 캐시 여부를 체크하여 캐시 안되어 있을 경우 캐시를 하고, 있으면 캐시된걸 전달하게 된다.
    public Member findByNameCacheWithOneKey(String name) {
        slowQuery(2000);
        return new Member(0, name + "@gmail.com", name);
    }

    @Override
    @Cacheable(value = "findMemberCache")
    // 두 인자값이 모두 같아야 같은 캐쉬값을 내보냄
    public Member findByNameCacheWithAllKey(String name, String testKey) {
        slowQuery(2000);
        return new Member(0, name + "@gmail.com", name + " " + testKey);
    }


    @Override
    @CacheEvict(value = "findMemberCache", key = "#name")
    // 해당 캐시 내용을 지우겠다는 의미이다.
    // 캐시 데이터가 갱신되어야 한다면 @CacheEvict가 선언된 메소드를 실행시키면 캐시 데이터는 삭제되고 새로운 데이터를 받아 캐시하게 된다.
    // @Cacheable과 마찬가지로 key에 따라 캐시를 선택해서 제거가 가능하다.
    public void refresh(String name) {
        logger.info(name + "의 Cache Clear!");
    }

    // 빅쿼리를 돌린다는 가정
    // 캐시와 비캐시 메소드들 간의 성능비교를 하기 위해 slowQuery라는 메소드를 추가하였다.
    // 엄청나게 많은 양의 데이터가 존재하여 한번 조회 할때마다 2초 이상의 시간이 필요하다고 가정 했다.
    // slowQuery가 2초간 thread를 sleep 시키기 때문에 findByNameNoCache와 findByNameCache 메소드는 최소 2초 이상의 시간이 수행 된다.
    private void slowQuery(long seconds) {
        try {
            Thread.sleep(seconds);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
