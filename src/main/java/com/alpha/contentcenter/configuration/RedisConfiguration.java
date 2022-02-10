package com.alpha.contentcenter.configuration;

import com.sun.tools.javac.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.util.LinkedList;

/**
 * @author anzihao
 */
@Configuration
@PropertySource(value = {"classpath:redis/redis.properties"})
public class RedisConfiguration {

    @Value("${redis.node.maxTotal}")
    private Integer maxTotal;

    @Value("${redis.node.host}")
    private String host;


    @Value("${redis.node.port}")
    private String port;

    @Value("${redis.node.password}")
    private String password;

    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);

        return jedisPoolConfig;
    }

    @Bean
    public ShardedJedisPool jedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        JedisShardInfo jedisShardInfo = new JedisShardInfo(host, port);
        jedisShardInfo.setPassword(password);

        LinkedList<JedisShardInfo> list = new LinkedList<JedisShardInfo>();
        list.add(jedisShardInfo);

        return new ShardedJedisPool(jedisPoolConfig, list);
    }
}
