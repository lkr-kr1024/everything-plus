package com.bittech.everything.core.model;

import lombok.Data;

@Data
public class Condition {

    private String name;

    private String fileType;

    private Integer limit;

    /**
     * 检索结果的文件信息depth排序规则
     * 1.默认是true -> asc
     * 2.false -> desc
     */
    private  Boolean orderByAsc;//若此处加了@Data注解还没生效，就在设置里启用Settings | Build, Execution, Deployment | Compiler | Annotation Processors
}
