package com.tokenmigration.app.config;


import org.redisson.api.RedissonClient;

import org.redisson.codec.JsonJacksonCodec;

import org.redisson.config.Config;
import org.redisson.Redisson;

import org.redisson.config.SingleServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RedisConfig {

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress("redis://localhost:6379")
                .setPassword("mysecurepassword")
                .setDatabase(2);
        config.setCodec(new JsonJacksonCodec());

        return Redisson.create(config);

    }

}