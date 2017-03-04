package com.nokia.ucms.biz.entity;

import com.nokia.ucms.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by x36zhao on 2017/3/3.
 */
@Getter
@Setter
public class Project extends BaseEntity
{
    private String name;
    private String description;
    private String owner;
    private String tableName;
    private Integer state;
}
