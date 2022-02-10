package com.alpha.contentcenter.controller.content;

import com.alpha.contentcenter.auth.CheckLogin;
import com.alpha.contentcenter.domain.dto.content.ShareDTO;
import com.alpha.contentcenter.domain.entity.content.Share;
import com.alpha.contentcenter.service.content.ShareService;
import com.alpha.contentcenter.util.JwtOperator;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/share")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareController {

    private final ShareService shareService;

    private final JwtOperator jwtOperator;

    @GetMapping("/{id}")
    @CheckLogin
    public ShareDTO findById(@PathVariable Integer id) {
        return this.shareService.findById(id);
    }

    @GetMapping("/gen-token")
    public String genToken() {
        // 颁发token
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", "1");
        userInfo.put("wxNickName", "testNickname");
        userInfo.put("role", "admin");
        return jwtOperator.generateToken(userInfo);
    }

    @GetMapping("q")
    public PageInfo<Share> q(
            @RequestParam(required = false) String title,
            @RequestParam(required = false, defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {

        // pageSize要做限制
        if (pageSize > 100) {
            pageSize = 100;
        }

        return this.shareService.q(title, pageNo, pageSize);

    }

    @GetMapping("/test-redis")
    public String testRedis(){
        return this.shareService.testRedis();
    }

    @GetMapping("/exchange/{id}")
    @CheckLogin
    public Share exchangeById(@PathVariable Integer id, HttpServletRequest request) {
        return this.shareService.exchangeById(id, request);
    }
}
