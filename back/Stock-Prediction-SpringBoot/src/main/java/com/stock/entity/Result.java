package com.stock.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author admin
 * @date 2023/3/24 0004 15:16
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    public static final String SUCCESS = "200";
    public static final String ERROR = "500";

    private Object data;
    private String code;
    private String msg;

    public static Result success(Object data, String msg) {
        return new Result(data, SUCCESS, msg);
    }

    public static Result error(Object data, String msg) {
        return new Result(data, ERROR, msg);
    }
}
