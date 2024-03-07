package com.sky.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "菜品连接数据库的数据格式")
public class Dish implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键值")
    private Long id;

    //菜品名称
    @ApiModelProperty("菜品名称")
    private String name;

    //菜品分类id
    @ApiModelProperty("菜品分类id")
    private Long categoryId;

    //菜品价格
    @ApiModelProperty("菜品价格")
    private BigDecimal price;

    //图片
    @ApiModelProperty("图片")
    private String image;

    //描述信息
    @ApiModelProperty("描述信息")
    private String description;

    //0 停售 1 起售
    @ApiModelProperty("起售状态")
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;

}
