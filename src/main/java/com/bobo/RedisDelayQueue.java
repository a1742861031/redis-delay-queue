package com.bobo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bobo.entity.TaskItem;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.UUID;

/**
 * @author by bobo
 * @Description Redis延迟消息队列
 * @Date 2023/3/7 15:29
 */

public class RedisDelayQueue<T> {
    private final Jedis jedis;
    private final String quickKey;
    private final Type type = new TypeReference<TaskItem<T>>() {
    }.getType();

    public RedisDelayQueue(Jedis jedis, String quickKey) {
        this.jedis = jedis;
        this.quickKey = quickKey;
    }

    public void delay(T msg) {


        TaskItem<T> task = new TaskItem<T>();
        String uuid = UUID.randomUUID().toString();
        task.setId(uuid);
        task.setMsg(msg);
        String s = JSON.toJSONString(task);
        jedis.zadd(quickKey, System.currentTimeMillis() + 5000, s);
    }

    public void loop() {
        while (!Thread.interrupted()) {
            Set<String> values = jedis.zrangeByScore(quickKey, 0, System.currentTimeMillis(), 0, 1);
            if (values.isEmpty()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
                continue;
            }
            String s = values.iterator().next();
            if (jedis.zrem(quickKey, s) > 0) {
                TaskItem<T> task = JSON.parseObject(s, type);
                handleMsg(task);
            }
        }
    }

    public void handleMsg(Object o) {
        System.out.println(o);
    }
}
