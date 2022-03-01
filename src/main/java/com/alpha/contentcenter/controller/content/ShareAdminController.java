package com.alpha.contentcenter.controller.content;

import com.alpha.contentcenter.auth.CheckAuthorization;
import com.alpha.contentcenter.domain.dto.content.ShareAuditDTO;
import com.alpha.contentcenter.domain.entity.content.Share;
import com.alpha.contentcenter.service.content.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/shares")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareAdminController {

    private final ShareService shareService;

    @PutMapping("/audit/{id}")
    @CheckAuthorization("admin")
    public Share auditById(@PathVariable Integer id,
                           @RequestBody ShareAuditDTO auditDTO) {
        return this.shareService.auditById(id, auditDTO);
    }
}
