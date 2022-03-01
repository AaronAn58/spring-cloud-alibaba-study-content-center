package com.alpha.contentcenter.dao.content;

import com.alpha.contentcenter.domain.entity.content.Share;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


public interface ShareMapper extends Mapper<Share> {
    List<Share> selectByParam(@Param("title") String title);
}