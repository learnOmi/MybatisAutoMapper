package com.autoMapper.controller;

import com.autoMapper.enums.ResponseCodeEnum;

import com.autoMapper.entity.vo.ResponseVO;

public class ABaseController {
    protected static final String STATUS_SUCCESS = "success";
    protected static final String STATUS_ERROR = "error";

    protected <T> ResponseVO getSuccessResponse(T t) {
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setStatus(STATUS_SUCCESS);
        responseVO.setData(t);
        responseVO.setMessage(ResponseCodeEnum.CODE_200.getMsg());
        responseVO.setCode(ResponseCodeEnum.CODE_200.getCode());
        return responseVO;
    }
}
