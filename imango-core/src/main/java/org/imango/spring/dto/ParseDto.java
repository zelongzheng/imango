package org.imango.spring.dto;

import lombok.Data;

import java.util.List;

/**
 * 解析后返回的DTO
 */
@Data
public class ParseDto {

    //集合名称
    private String collection;

    //返回字段
    private List<String> columnsNames;

    //操作类型  1:新增 2:删除 3:修改 4:删除
    private String operation;

    //条件字段
    private List<LimitDto> queryParams;

    //排序字段
    private String orderBy;

    //分组字段
    private String groupBy;

}
