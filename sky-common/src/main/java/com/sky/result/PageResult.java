package com.sky.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 封装分页查询结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "分页查询返回的数据格式")
// 本包内没有引入Swagger小刀框架,无法使用API,故响应参数没有对应注解
public class PageResult implements Serializable {

    @ApiModelProperty("总记录数")
    private long total; //总记录数

    @ApiModelProperty("当前页数据集合")
    private List records; //当前页数据集合

}
