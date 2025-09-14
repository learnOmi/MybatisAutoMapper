package com.autoMapper.entity.po;

import java.io.Serializable;
/**
 * 靓号表
 * @author 'Tong'
 * @since 2025/09/14
 */
public class UserInfoBeauty implements Serializable {
	/**
	 * 
	 */
	private Integer id;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 用户id
	 */
	private Integer userId;

	/**
	 * 0: 未启用 1:启用
	 */
	private Byte status;

}