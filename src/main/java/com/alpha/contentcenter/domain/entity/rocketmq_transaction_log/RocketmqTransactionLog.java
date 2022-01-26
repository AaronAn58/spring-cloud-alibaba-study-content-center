package com.alpha.contentcenter.domain.entity.rocketmq_transaction_log;

import javax.persistence.*;

import lombok.*;

@Getter
@Setter
@ToString
@Table(name = "rocketmq_transaction_log")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RocketmqTransactionLog {
    /**
     * id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 事务id
     */
    @Column(name = "transaction_Id")
    private String transactionId;

    /**
     * 日志
     */
    private String log;
}