package com.alpha.contentcenter.feignclient;

import com.alpha.contentcenter.configuration.UserCenterFeignConfiguration;
import com.alpha.contentcenter.domain.dto.user.UserAddBonusDTO;
import com.alpha.contentcenter.domain.dto.user.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

//@FeignClient(name = "user-center", configuration = UserCenterFeignConfiguration.class)
@FeignClient(name = "user-center")
public interface UserCenterFeignClient {
    @GetMapping("/users/{id}")
    UserDTO findById(@PathVariable Integer id);

    @PutMapping("/users/add-bonus")
    UserDTO addBonus(@RequestBody UserAddBonusDTO userAddBonusDTO);
}
