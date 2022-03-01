package com.alpha.contentcenter.domain.entity.mid_user_share;

import javax.persistence.*;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MidUserShare {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * share.id
     */
    @Column(name = "share_id")
    private Integer shareId;

    /**
     * user.id
     */
    @Column(name = "user_id")
    private Integer userId;
}