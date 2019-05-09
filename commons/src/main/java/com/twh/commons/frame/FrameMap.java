package com.twh.commons.frame;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * @author tanwenhai
 */
public class FrameMap<K, V> implements Map<K, V> {
    private Map<K, V> in;

    private ReadWriteLock rwl = new ReentrantReadWriteLock(false);

    private int limit = 10;

    static JedisPool jedisPool = new JedisPool("10.23.157.235");

    String hmapKey;

    public FrameMap(Map<K, V> in, int incr) {
        this.in = in;
        hmapKey = "room" + incr;
    }

    @Override
    public int size() {
        Lock readLock = rwl.readLock();
        readLock.lock();
        try (Jedis jedis = jedisPool.getResource()) {
            return in.size() + jedis.hlen(hmapKey).intValue();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        Lock readLock = rwl.readLock();
        readLock.lock();
        try (Jedis jedis = jedisPool.getResource()) {
            return in.isEmpty() && jedis.hlen(hmapKey) == 0L;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        Lock readLock = rwl.readLock();
        readLock.lock();
        try (Jedis jedis = jedisPool.getResource()) {
            // 将key转换成string
            return in.containsKey(key) || jedis.hexists(hmapKey, key.toString());
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        throw new RuntimeException("not support");
//        Lock readLock = rwl.readLock();
//        try {
//            Jedis jedis = jedisPool.getResource();
//            return in.containsValue(value);
//        } finally {
//            readLock.unlock();
//        }
    }

    @Override
    public V get(Object key) {
        Lock readLock = rwl.readLock();
        readLock.lock();
        try (Jedis jedis = jedisPool.getResource()) {
            V v = in.get(key);
            if (v == null) {
                v = (V)jedis.hget(hmapKey, key.toString());
            }

            return v;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        Lock writeLock = rwl.writeLock();
        writeLock.lock();
        try {
            V v = in.put(key, value);
            if (in.size() >= limit) {
                saveToRedis();
            }
            return v;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public V remove(Object key) {
        Lock writeLock = rwl.writeLock();
        writeLock.lock();
        try (Jedis jedis = jedisPool.getResource()) {
            V v = in.remove(key);
            if (v == null) {
                v = (V)jedis.hget(hmapKey, key.toString());
                if (v != null) {
                    jedis.hdel(hmapKey, key.toString());
                }
            }

            return v;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        Lock writeLock = rwl.writeLock();
        writeLock.lock();
        try {
            in.putAll(m);
            if (in.size() >= limit) {
                saveToRedis();
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void clear() {
        Lock writeLock = rwl.writeLock();
        writeLock.lock();
        try (Jedis jedis = jedisPool.getResource()) {
            in.clear();
            jedis.del(hmapKey);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Set<K> keySet() {
        HashSet<K> allKeys = new HashSet<>(in.keySet());
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> hkeys = jedis.hkeys(hmapKey);
            allKeys.addAll(hkeys.stream().map(s -> (K) s).collect(Collectors.toSet()));
        }
        return allKeys;
    }

    @Override
    public Collection<V> values() {
        ArrayList<V> allValue = new ArrayList<>(in.values());
        try (Jedis jedis = jedisPool.getResource()) {
            // 数据过大使用hscan
            Map<String, String> stringMap = jedis.hgetAll(hmapKey);
            List<V> list = stringMap.values().stream().map(s -> (V) s).collect(Collectors.toList());
            allValue.addAll(list);
        }
        return allValue;
    }


    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new RuntimeException("not support");
//        return new HashSet<>(in.entrySet());
    }

    private void saveToRedis() {
        Lock wl = rwl.writeLock();
        wl.lock();
        try (Jedis jedis = jedisPool.getResource()) {
            // 转换成Map<String,String>
            int size = (int) ((float) in.size() / 0.75F + 1.0F);
            Map<String, String> stringMap = new HashMap<>(size);
            in.forEach((key, value) -> {
                stringMap.put(key.toString(), value.toString());
            });
            jedis.hset(hmapKey, stringMap);
            in.clear();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            wl.unlock();
        }
    }
}
