package com.byb.tools.doc.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @Author baiyanbing
 * @Date 2022/8/23 6:45 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {

    private String code;

    private Object data;

    private String msg;


    public static Result ok(Object data) {
        return new Result("200", data, "success");
    }
}
