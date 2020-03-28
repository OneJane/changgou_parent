package com.changgou.framework.exception;
import entity.Result;
import entity.StatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
/*****
 * @Author: www.itheima.com
 * @Description: com.changgou.framework.exception
 * 全局异常处理
 ****/
@ControllerAdvice  // 全局捕获异常类，只要作用在@RequestMapping上，所有的异常都会被捕获。
public class BaseExceptionHandler {

    /***
     * 全局异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result error(Exception  e){
        return new Result(false,StatusCode.REMOTEERROR,e.getMessage());
    }
}
