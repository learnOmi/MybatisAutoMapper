package com.autoMapper.service;

import com.autoMapper.entity.po.UserInfoBeauty;
import com.autoMapper.entity.query.UserInfoBeautyQuery;
import com.autoMapper.entity.vo.PaginationResultVO;
import java.util.List;

/**
 * 靓号表Service
 * @author 'Tong'
 * @since 2025/09/28
 */
public interface UserInfoBeautyService {
	// 根据条件查询列表
	List<UserInfoBeauty> findListByParam(UserInfoBeautyQuery query);

	// 根据条件查询总数
	Integer findCountByParam(UserInfoBeautyQuery query);

	// 分页查询
	PaginationResultVO<UserInfoBeauty> findPageByParam(UserInfoBeautyQuery query);

	// 新增
	Integer add(UserInfoBeauty bean);

	// 批量新增
	Integer addBatch(List<UserInfoBeauty> listBean);

	// 批量新增或修改
	Integer addOrUpdateBatch(List<UserInfoBeauty> listBean);

	// 根据IdAndEmailAndUserId查询
	UserInfoBeauty getUserInfoBeautyByIdAndEmailAndUserId(Integer id, String email, Integer userId);

	// 根据IdAndEmailAndUserId更新
	Integer updateUserInfoBeautyByIdAndEmailAndUserId(UserInfoBeauty bean, Integer id, String email, Integer userId);

	// 根据IdAndEmailAndUserId删除
	Integer deleteUserInfoBeautyByIdAndEmailAndUserId(Integer id, String email, Integer userId);
	// 根据UserId查询
	UserInfoBeauty getUserInfoBeautyByUserId(Integer userId);

	// 根据UserId更新
	Integer updateUserInfoBeautyByUserId(UserInfoBeauty bean, Integer userId);

	// 根据UserId删除
	Integer deleteUserInfoBeautyByUserId(Integer userId);
	// 根据Email查询
	UserInfoBeauty getUserInfoBeautyByEmail(String email);

	// 根据Email更新
	Integer updateUserInfoBeautyByEmail(UserInfoBeauty bean, String email);

	// 根据Email删除
	Integer deleteUserInfoBeautyByEmail(String email);
}