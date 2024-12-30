package com.wsw.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wsw.common.core.utils.bean.BeanV1Utils;
import com.wsw.common.security.utils.SecurityUtils;
import com.wsw.system.api.domain.SysUser;
import com.wsw.system.api.model.LoginUser;
import com.wsw.system.domain.dao.DeliveryAddress;
import com.wsw.system.domain.dao.SystemAddress;
import com.wsw.system.domain.po.DeliveryAddressPo;
import com.wsw.system.domain.qo.DeliveryAddressQo;
import com.wsw.system.domain.vo.DeliveryAddressVo;
import com.wsw.system.mapper.DeliveryAddressMapper;
import com.wsw.system.service.DeliveryAddressService;
import com.wsw.system.service.ISysUserService;
import com.wsw.system.service.ISystemAddressService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 收货地址 Service业务层处理
 *
 * @author chenzhongxin
 * @description 版权所有 万事屋
 * @date 2024/12/05 10:42
 */

@Service
public class DeliveryAddressServiceImpl implements DeliveryAddressService {

    @Autowired
    private DeliveryAddressMapper deliveryAddressMapper;

    /**
     * 地址管理Service
     */
    @Resource
    private ISystemAddressService systemAddressService;

    @Resource
    private ISysUserService userService;


    @Override
    public Page<DeliveryAddressVo> selectPageList(DeliveryAddressQo qo) {
        Page<DeliveryAddress> pageList = deliveryAddressMapper.selectPageList(qo);
        Page<DeliveryAddressVo> deliveryAddressVoPage = BeanV1Utils.toBean(pageList, DeliveryAddressVo.class);
        List<DeliveryAddressVo> records = deliveryAddressVoPage.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return deliveryAddressVoPage;
        }
        // 地址数据准备 (关联的地址信息)
        List<String> systemAddressCodeList = records.stream().map(DeliveryAddressVo::getSystemAddressCode).collect(Collectors.toList());
        List<SystemAddress> systemAddresseList = systemAddressService.selectListByCodes(systemAddressCodeList);
        Map<String, String> systemAddresseMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(systemAddresseList)) {
            systemAddresseMap = systemAddresseList.stream().collect(Collectors.toMap(SystemAddress::getCode, SystemAddress::getFullName));
        }

        // 用户数据
        List<String> userNames = records.stream().map(DeliveryAddressVo::getUserName).collect(Collectors.toList());
        List<SysUser> sysUsers = userService.selectUserListByUserNames(userNames);
        Map<String, String> sysUserMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(sysUsers)) {
            sysUserMap = sysUsers.stream().collect(Collectors.toMap(SysUser::getUserName, SysUser::getNickName));
        }

        // 填充展示数据
        for (DeliveryAddressVo vo : records) {
            // 选择地址全称
            vo.setSystemAddressFullName(systemAddresseMap.getOrDefault(vo.getSystemAddressCode(), "未知类型地址"));
            // 用户账号信息
            vo.setUserNameStr(sysUserMap.getOrDefault(vo.getUserName(), "未知用户"));
        }
        return deliveryAddressVoPage;
    }

    @Override
    public List<DeliveryAddressVo> selectListByQo(DeliveryAddressQo qo) {
        List<DeliveryAddress> deliveryAddresses = deliveryAddressMapper.selectList(qo);
        if (CollectionUtils.isEmpty(deliveryAddresses)) {
            return Collections.emptyList();
        }
        List<DeliveryAddressVo> deliveryAddressVos = BeanV1Utils.toBean(deliveryAddresses, DeliveryAddressVo.class);

        // 地址数据准备 (关联的地址信息)
        List<String> systemAddressCodeList = deliveryAddressVos.stream().map(DeliveryAddressVo::getSystemAddressCode).collect(Collectors.toList());
        List<SystemAddress> systemAddresseList = systemAddressService.selectListByCodes(systemAddressCodeList);
        Map<String, String> systemAddresseMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(systemAddresseList)) {
            systemAddresseMap = systemAddresseList.stream().collect(Collectors.toMap(SystemAddress::getCode, SystemAddress::getFullName));
        }

        // 用户数据
        List<String> userNames = deliveryAddressVos.stream().map(DeliveryAddressVo::getUserName).collect(Collectors.toList());
        List<SysUser> sysUsers = userService.selectUserListByUserNames(userNames);
        Map<String, String> sysUserMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(sysUsers)) {
            sysUserMap = sysUsers.stream().collect(Collectors.toMap(SysUser::getUserName, SysUser::getNickName));
        }

        // 填充展示数据
        for (DeliveryAddressVo vo : deliveryAddressVos) {
            // 选择地址全称
            vo.setSystemAddressFullName(systemAddresseMap.getOrDefault(vo.getSystemAddressCode(), "未知类型地址"));
            // 用户账号信息
            vo.setUserNameStr(sysUserMap.getOrDefault(vo.getUserName(), "未知用户"));
        }
        return deliveryAddressVos;
    }

    @Override
    public DeliveryAddressVo selectInfoByCode(String code) {
        return BeanV1Utils.toBean(deliveryAddressMapper.selectOneByCode(code), DeliveryAddressVo.class);
    }

    @Override
    public List<DeliveryAddressVo> selectInfoByCodes(List<String> codes) {
        return BeanV1Utils.toBean(deliveryAddressMapper.selectListByCodes(codes), DeliveryAddressVo.class);
    }

    @Override
    public void deleteByCode(String code) {
        deliveryAddressMapper.deleteByCode(code);
    }

    @Override
    public List<DeliveryAddressVo> selectByCreateBy() {
        String username = SecurityUtils.getUsername();
        if (StringUtils.isBlank(username)) {
            throw new SecurityException("当前登录用户为空");
        }
        List<DeliveryAddress> deliveryAddresses = deliveryAddressMapper.selectList(new QueryWrapper<DeliveryAddress>().eq("create_by", username));
        List<DeliveryAddressVo> deliveryAddressVos = BeanV1Utils.toBean(deliveryAddresses, DeliveryAddressVo.class);
        if (CollectionUtils.isEmpty(deliveryAddresses)) {
            return Collections.emptyList();
        }

        // 地址数据准备 (关联的地址信息)
        List<String> systemAddressCodeList = deliveryAddressVos.stream().map(DeliveryAddressVo::getSystemAddressCode).collect(Collectors.toList());
        List<SystemAddress> systemAddresseList = systemAddressService.selectListByCodes(systemAddressCodeList);
        Map<String, String> systemAddresseMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(systemAddresseList)) {
            systemAddresseMap = systemAddresseList.stream().collect(Collectors.toMap(SystemAddress::getCode, SystemAddress::getFullName));
        }

        // 用户数据
        List<String> userNames = deliveryAddressVos.stream().map(DeliveryAddressVo::getUserName).collect(Collectors.toList());
        List<SysUser> sysUsers = userService.selectUserListByUserNames(userNames);
        Map<String, String> sysUserMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(sysUsers)) {
            sysUserMap = sysUsers.stream().collect(Collectors.toMap(SysUser::getUserName, SysUser::getNickName));
        }

        // 填充展示数据
        for (DeliveryAddressVo vo : deliveryAddressVos) {
            // 选择地址全称
            vo.setSystemAddressFullName(systemAddresseMap.getOrDefault(vo.getSystemAddressCode(), "未知类型地址"));
            // 用户账号信息
            vo.setUserNameStr(sysUserMap.getOrDefault(vo.getUserName(), "未知用户"));
        }
        return deliveryAddressVos;
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return deliveryAddressMapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional
    public int insert(DeliveryAddressPo po) {
        // 数据转换
        DeliveryAddress record = BeanV1Utils.toBean(po, DeliveryAddress.class);
        // 获取基础数据
        String isDefaultAddress = record.getIsDefaultAddress();
        // 默认用户名为当前登录用户
        if (StringUtils.isBlank(record.getUserName())) {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            record.setUserName(SecurityUtils.getUsername());
        }
        // 修改之前的默认地址
        if (StringUtils.isNotBlank(isDefaultAddress) && "1".equals(isDefaultAddress)) {
            DeliveryAddress oldDefaultAddress = deliveryAddressMapper.selectOneByIsDefaultAddressAndUserName(record);
            // 设置为非默认地址状态
            if (null != oldDefaultAddress) {
                oldDefaultAddress.setIsDefaultAddress("0");
                deliveryAddressMapper.updateIsDefaultAddressByCode(oldDefaultAddress);
            }
        }
        // 添加新的地址
        record.setCode(UUID.randomUUID().toString().replace("-", ""));
        record.setCreateBy(SecurityUtils.getUsername());
        record.setCreateTime(new Date());
        return deliveryAddressMapper.insert(record);
    }

    /**
     * 更新收货地址数据
     *
     * @author chenzhongxin
     * @date 2024/12/05
     */
    @Override
    public int update(DeliveryAddressPo po) {
        // 数据转换
        DeliveryAddress record = BeanV1Utils.toBean(po, DeliveryAddress.class);
        // 获取基础数据
        String isDefaultAddress = record.getIsDefaultAddress();
        // 修改之前的默认地址 (多个地址只能有一个默认地址)
        if (StringUtils.isNotBlank(isDefaultAddress) && "1".equals(isDefaultAddress)) {
            DeliveryAddress oldDefaultAddress = deliveryAddressMapper.selectOneByIsDefaultAddressAndUserName(record);
            // 设置为非默认地址状态
            if (null != oldDefaultAddress) {
                oldDefaultAddress.setIsDefaultAddress("0");
                deliveryAddressMapper.updateIsDefaultAddressByCode(oldDefaultAddress);
            }
        }
        record.setUpdateBy(SecurityUtils.getUsername());
        record.setUpdateTime(new Date());
        return deliveryAddressMapper.updateByCode(record);
    }


    @Override
    public int insertSelective(DeliveryAddress record) {
        return deliveryAddressMapper.insertSelective(record);
    }

    @Override
    public DeliveryAddress selectByPrimaryKey(Long id) {
        return deliveryAddressMapper.selectByPrimaryKey(id);
    }
}
