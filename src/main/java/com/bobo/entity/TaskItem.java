package com.bobo.entity;

import lombok.Data;

/**
 * @author by bobo
 * @Description 消息实体
 * @Date 2023/3/7 15:28
 */
@Data
public class TaskItem<T> {
    private String id;
    private T msg;
}
