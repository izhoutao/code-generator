package com.haili.framework.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class ModelResopnseResult<T> extends ResponseResult {
    T model;

    public ModelResopnseResult(ResultCode resultCode, T model) {
        super(resultCode);
        this.model = model;
    }
}
