package com.bobo;

import redis.clients.jedis.Jedis;

/**
 * @author by bobo
 * @Description 主启动类
 * @Date 2023/3/7 15:27
 */
public class MainApplication {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("10.16.34.241",6379);
        final RedisDelayQueue<Object> queue = new RedisDelayQueue<>(jedis, "q-demo");
        Thread producer = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    queue.delay("codehole" + i);
                }
            }
        };
        Thread consumer = new Thread() {
            @Override
            public void run() {
                queue.loop();
            }
        };
        producer.start();
        consumer.start();
        try {
            producer.join();
            Thread.sleep(6000);
            consumer.interrupt();
            consumer.join();
        } catch (InterruptedException e) {

        }


    }
}
