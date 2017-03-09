package com.nokia.ucms.biz.entity;

import com.nokia.ucms.common.entity.BaseEntity;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Created by x36zhao on 2017/3/7.
 */
@Data
@ToString(exclude = "id")
public class ProjectTrace extends BaseEntity
{
    private Integer projectId;
    private String operator;
    private String domain;
    private Date eventTime;
    private Integer eventType;
    private String identifier;
    private String category;
    private String message;
    private String oldData;
    private String newData;
}
