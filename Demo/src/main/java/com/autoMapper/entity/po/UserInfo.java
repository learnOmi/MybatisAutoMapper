package com.autoMapper.entity.po;

import java.io.Serializable;
import java.util.Date;
/**
 * 用户信息表
 * @author 'Tong'
 * @since 2025/09/14
 */
public class UserInfo implements Serializable {
	/**
	 * 用户id
	 */
	private Integer user_id;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 昵称
	 */
	private String nick_name;

	/**
	 * 0:直接加入 1:同意后加入
	 */
	private Byte join_type;

	/**
	 * 性别 0:女 1:男
	 */
	private Byte sex;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 个性签名
	 */
	private String personal_signature;

	/**
	 * 状态
	 */
	private Byte status;

	/**
	 * 创建时间
	 */
	private Date create_time;

	/**
	 * 最后登录时间
	 */
	private Date last_login_time;

	/**
	 * 地区
	 */
	private String area_name;

	/**
	 * 地区编号
	 */
	private String area_code;

	/**
	 * 最后离开时间
	 */
	private Long last_off_time;

}