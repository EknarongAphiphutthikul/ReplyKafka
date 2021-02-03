package com.example.ReplyKafka.redis;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Protocol;

public abstract class JedisPoolConfiguration {
	
	private static JedisPoolConfig poolConfig = null;
	private static JedisSentinelPool sentinelPool = null;
	private static String password = null;
	private static boolean redisAuthenFlag = true;
	
	JedisPoolConfiguration() {
		buildPoolConfig();
	}

	protected Jedis getJedisConnectionPool() {
		return sentinel();
	}

	private Jedis sentinel() {
		if (null == JedisPoolConfiguration.password) {
			JedisPoolConfiguration.password = "P@ssw0rd";
		}

		if (null == JedisPoolConfiguration.sentinelPool) {
			String masterName = "mymaster";
			if (JedisPoolConfiguration.redisAuthenFlag) {
				JedisPoolConfiguration.sentinelPool = new JedisSentinelPool(masterName, getSentinelsAddress(), poolConfig,
						Protocol.DEFAULT_TIMEOUT, Protocol.DEFAULT_TIMEOUT, password, Protocol.DEFAULT_DATABASE, null,
						Protocol.DEFAULT_TIMEOUT, Protocol.DEFAULT_TIMEOUT, password, null);
			} else {
				JedisPoolConfiguration.sentinelPool = new JedisSentinelPool(masterName, getSentinelsAddress(), poolConfig);
			}
		
		}
		return JedisPoolConfiguration.sentinelPool.getResource();
	}

	private Set<String> getSentinelsAddress() {
		String sentinelsStr = "localhost:32514,localhost:32515,localhost:32516";
		return new HashSet<>(Arrays.asList(StringUtils.split(sentinelsStr, ",")));
	}

	private void buildPoolConfig() {
		if (null == JedisPoolConfiguration.poolConfig) {
			int maxTotal = 10;
			int maxIdle = 10;
			int minIdle = 10;
			int minEvictableIdleTimeMillis = 60000;
			int timeBetweenEvictionRunsMillis = 30000;
			int numTestsPerEvictionRun = 3;
	
			JedisPoolConfiguration.poolConfig = new JedisPoolConfig();
			JedisPoolConfiguration.poolConfig.setMaxTotal(maxTotal);
			JedisPoolConfiguration.poolConfig.setMaxIdle(maxIdle);
			JedisPoolConfiguration.poolConfig.setMinIdle(minIdle);
			JedisPoolConfiguration.poolConfig.setTestOnBorrow(true);
			JedisPoolConfiguration.poolConfig.setTestOnReturn(true);
			JedisPoolConfiguration.poolConfig.setTestWhileIdle(true);
			JedisPoolConfiguration.poolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
			JedisPoolConfiguration.poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
			JedisPoolConfiguration.poolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
			JedisPoolConfiguration.poolConfig.setBlockWhenExhausted(true);
		}
	}
}
