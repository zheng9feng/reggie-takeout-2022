package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.common.ResponseInfo;
import com.reggie.dto.LoginDto;
import com.reggie.entity.User;
import com.reggie.service.UserService;
import com.reggie.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

/**
 * @author m0v1
 * @date 2022年10月18日 00:51
 */
@Slf4j
@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 获取验证码
     *
     * @param user 登录用户
     * @return
     */
    @PostMapping("/sendMsg")
    public ResponseInfo<String> sendMsg(@RequestBody User user) {
        log.info("获取验证码,user={}.", user);
        //获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode4String(4);
            log.info("生成的验证码为:{}", code);

            //调用阿里云提供的短信服务API完成短信发送
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);

            // 将验证码保存到redis中,并且设置有效期为5分钟
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
            return ResponseInfo.success("短信发送成功");
        }
        return ResponseInfo.error("短信发送失败");
    }

    /**
     * 前台用户登录
     *
     * @param loginDto 登录DTO
     * @param session  会话
     * @return
     */
    @PostMapping("/login")
    public ResponseInfo<User> login(@RequestBody LoginDto loginDto, HttpSession session) {

        log.info("前台用户登录:{}.", loginDto);
        // 手机号
        String phone = loginDto.getPhone();
        // 验证码
        String code = loginDto.getCode();

        // 从缓存中获取验证码
        String cachedPhone = redisTemplate.opsForValue().get(phone);
        //进行验证码的对比（页面提交的验证码和session中保存的验证码）
        if (StringUtils.equals(code, cachedPhone)) {
            //如果比对成功，则登录成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);

            //判断当前手机号是否为新用户，如果是新用户则自动完成注入
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }

            // 将用户id保存到session中
            // 注:对于新用户,在执行完userService.save(user)后会将表数据回写给user对象,从而可以直接根据user获取id
            session.setAttribute("user", user.getId());
            return ResponseInfo.success(user);
        }

        return ResponseInfo.error("登录失败");
    }

}
