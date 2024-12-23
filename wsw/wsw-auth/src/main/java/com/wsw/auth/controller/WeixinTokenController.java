package com.wsw.auth.controller;

import com.wsw.auth.service.SysLoginService;
import com.wsw.common.core.domain.R;
import com.wsw.system.api.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 微信界面提供的接口
 * 
 * @author chenzhongxin
 * @date 2024/11/28
 */
@RestController
@RequestMapping("/wx")
public class WeixinTokenController {

    @Autowired
    private SysLoginService sysLoginService;

    @PostMapping("/register")
    public R<?> register(@RequestBody SysUser sysUser)
    {
        // 用户注册
        return R.ok(sysLoginService.registerFromWeixin(sysUser));
    }
}
