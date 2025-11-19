package com.cherry.api.feign;

import com.cherry.pojo.Users;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author cherry
 */
@FeignClient(value = "auth-service")
public interface UserInfoMicroServiceFeign {

    @GetMapping("/passport/internal/user/me")
    Users getLoginUser(@RequestHeader("Authorization") String authorization);
    
    
}
