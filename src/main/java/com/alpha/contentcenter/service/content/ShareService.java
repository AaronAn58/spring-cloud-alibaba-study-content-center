package com.alpha.contentcenter.service.content;

import com.alpha.contentcenter.dao.content.ShareMapper;
import com.alpha.contentcenter.dao.rocketmq_transaction_log.RocketmqTransactionLogMapper;
import com.alpha.contentcenter.domain.dto.content.ShareAuditDTO;
import com.alpha.contentcenter.domain.dto.content.ShareDTO;
import com.alpha.contentcenter.domain.dto.message.UserAddBonusMsgDTO;
import com.alpha.contentcenter.domain.dto.user.UserDTO;
import com.alpha.contentcenter.domain.entity.content.Share;
import com.alpha.contentcenter.domain.entity.rocketmq_transaction_log.RocketmqTransactionLog;
import com.alpha.contentcenter.domain.enums.AuditStatusEnum;
import com.alpha.contentcenter.feignclient.UserCenterFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.handler.ExceptionHandlingWebHandler;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareService {

    private final ShareMapper shareMapper;

    private final RestTemplate restTemplate;

    private final UserCenterFeignClient userCenterFeignClient;

    private final RocketMQTemplate rocketMQTemplate;

    private final RocketmqTransactionLogMapper rocketmqTransactionLogMapper;

//    private final DiscoveryClient discoveryClient; DiscoveryClient由ribbon代替

    public ShareDTO findById(Integer id) {
        Share share = this.shareMapper.selectByPrimaryKey(id);
        Integer userId = share.getUserId();

/*
        //服务发现
        List<ServiceInstance> instances = discoveryClient.getInstances("user-center");
        String targetUrl = instances.stream()
                // 数据转换，map返回值是一个List
                .map(instance -> instance.getUri().toString() + "/users/{id}")
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("当前没有实例"));

        //以下实现负载均衡
        List<String> tarURLs = instances.stream()
                .map(instance -> instance.getUri().toString() + "/users/{id}")
                .collect(Collectors.toList());
        // i为随机下标
        int i = ThreadLocalRandom.current().nextInt(tarURLs.size());
        targetUrl = tarURLs.get(i);

        log.info("当前请求的地址为：{}", targetUrl);
        UserDTO userDTO = this.restTemplate.getForObject(
                targetUrl,
                UserDTO.class,
                userId
        );
        */


        /*UserDTO userDTO = this.restTemplate.getForObject(
                "http://user-center/users/{id}",
                UserDTO.class,
                userId
        );*/
        UserDTO userDTO = this.userCenterFeignClient.findById(userId);

        ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickName(userDTO.getWxNickname());
        return shareDTO;
    }

    // 用于解决事务问题
    @Transactional(rollbackFor = Exception.class)
    public Share auditById(Integer id, ShareAuditDTO shareAuditDTO) {
        // 1.查询share是否存在，不存在或者当前的audit_status != audit_status
        Share share = this.shareMapper.selectByPrimaryKey(id);
        if (share == null) {
            throw new IllegalArgumentException("参数非法");
        }
        if (!Objects.equals(share.getAuditStatus(), "NOT_YET")) {
            throw new IllegalArgumentException("该分享已审核通过或者审核不通过");
        }
        // 2. 审核资源，将状态设为PASS或者REJECT
        // command + option + M 可以将逻辑合并成一个方法
        auditByIdInDB(id, shareAuditDTO);
        // 3. 如果是PASS，则发放积分
        /**
         * 此段代码直接发送可投递的消息，可能导致事务问题，下面的代码实现分布式事务
         */
        /*
        this.rocketMQTemplate.convertAndSend("add-bonus",
                UserAddBonusMsgDTO.builder()
                        .userId(share.getUserId())
                        .bonus(50)
                        .build());
                        */
        /**
         * 实现分布式事务
         */
        if (AuditStatusEnum.PASS.equals(shareAuditDTO.getAuditStatusEnum())) {
            String transactionId = UUID.randomUUID().toString();
            // 发送办消息
            // sendMessageInTransaction的第四个参数，主要用于将参数传递至执行和回查的方法，详见src/main/java/com/alpha/contentcenter/rocketmq/AddBonusTransactionListener.java
            this.rocketMQTemplate.sendMessageInTransaction(
                    "add-bonus",
                    MessageBuilder
                            .withPayload(UserAddBonusMsgDTO.builder()
                                    .userId(share.getUserId())
                                    .bonus(50)
                                    .build())
                            .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                            .setHeader("share_id", id)
                            .build(),
                    shareAuditDTO
            );
        } else {
            this.auditByIdInDB(id, shareAuditDTO);
        }
        return share;
    }

    public void auditByIdInDB(Integer id, ShareAuditDTO shareAuditDTO) {
        Share share = Share.builder()
                .id(id)
                .auditStatus(shareAuditDTO.getAuditStatusEnum().toString())
                .reason(shareAuditDTO.getReason())
                .build();
        this.shareMapper.updateByPrimaryKeySelective(share);
    }

    @Transactional(rollbackFor = Exception.class)
    public void auditByIdWithRocketMQLog(Integer id, ShareAuditDTO shareAuditDTO, String transactionId) {
        this.auditByIdInDB(id, shareAuditDTO);
        this.rocketmqTransactionLogMapper.insertSelective(
                RocketmqTransactionLog.builder()
                        .transactionId(transactionId)
                        .log("审核分享")
                        .build()
        );
    }
}
