package com.zhenlong.darwinmall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.zhenlong.common.validator.annotation.ListValue;
import com.zhenlong.common.validator.group.AddGroup;
import com.zhenlong.common.validator.group.UpdateGroup;
import com.zhenlong.common.validator.group.UpdateStatusGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author zhenlong
 * @email yangzhenlong1993@gmail.com
 * @date 2022-12-27 16:06:55
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@Null(message = "append function cannot define id", groups = {AddGroup.class})
	@NotNull(message = "update function must define id", groups = {UpdateGroup.class})
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "brand name cannot be empty", groups = {AddGroup.class, UpdateGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@URL(message = "Logo must be a legal url address", groups = {AddGroup.class, UpdateGroup.class})
	@NotEmpty(message = "logo address cannot be null", groups = {AddGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@NotNull(groups = {AddGroup.class, UpdateStatusGroup.class})
	@ListValue(value = {0,1}, groups = {AddGroup.class, UpdateStatusGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@Pattern(regexp = "^[a-zA-Z]$", message = "the index capital letter must only be one English letter", groups = {AddGroup.class, UpdateGroup.class})
	@NotEmpty(message = "index letter cannot be null", groups = {AddGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@Min(value = 0, message = "sort must be an integer over than 0", groups = {AddGroup.class, UpdateGroup.class})
	@NotNull(message = "sort number cannot be null", groups = {AddGroup.class})
	private Integer sort;

}
