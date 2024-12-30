package com.wsw.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wsw.common.core.exception.ServiceException;
import com.wsw.common.core.utils.bean.BeanV1Utils;
import com.wsw.common.security.utils.SecurityUtils;
import com.wsw.system.domain.dao.PickUpAddress;
import com.wsw.system.domain.po.PickUpAddressPo;
import com.wsw.system.domain.qo.PickUpAddressQo;
import com.wsw.system.domain.vo.PickUpAddressVo;
import com.wsw.system.mapper.PickUpAddressMapper;
import com.wsw.system.service.IPickUpAddressService;
import com.wsw.system.service.ISysDictDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 取货地址 服务层实现
 *
 * @author chenzhongxin
 * @date 2024/11/30
 */

@Service
public class PickUpAddressServiceImpl implements IPickUpAddressService {

    @Resource
    private PickUpAddressMapper pickUpAddressMapper;

    /**
     * 词典Server
     * */
    @Resource
    private ISysDictDataService sysDictDataService;

    @Override
    public Page<PickUpAddressVo> selectPageList(PickUpAddressQo qo) {
        Page<PickUpAddress> pageList = pickUpAddressMapper.selectPageList(qo);
        Page<PickUpAddressVo> newPageList = BeanV1Utils.toBean(pageList, PickUpAddressVo.class);

        // 查询字典数据Map
        Map<String, String> dictDataMap = sysDictDataService.selectDictDataMap("server_address_status");

        newPageList.getRecords().forEach(pickUpAddressVo -> {
            String canValidStr = dictDataMap.getOrDefault(pickUpAddressVo.getCanValid().toString(), "");
            pickUpAddressVo.setCanValidText(canValidStr);
        });
        return newPageList;
    }

    @Override
    public PickUpAddressVo getInfo(String addressCode) {
        PickUpAddress pickUpAddress = pickUpAddressMapper.selectOneByAddressCode(addressCode);
        return BeanV1Utils.toBean(pickUpAddress, PickUpAddressVo.class);
    }

    @Override
    public List<PickUpAddressVo> getInfoListByCodes(List<String> codes) {
        List<PickUpAddress> pickUpAddresses = pickUpAddressMapper.selectListByCodes(codes);
        return BeanV1Utils.toBean(pickUpAddresses, PickUpAddressVo.class);
    }

    @Override
    public void insert(PickUpAddressPo entity) {
        // 校验取货地址是否唯一
        PickUpAddress pickUpAddress = pickUpAddressMapper.selectOneByAddressName(entity.getAddressName());
        if (null != pickUpAddress) {
            throw new ServiceException("取货地址已存在!");
        }
        // 产生code
        String code = UUID.randomUUID().toString().replace("-", "");

        PickUpAddress daoEntity = BeanV1Utils.toBean(entity, PickUpAddress.class);
        daoEntity.setAddressCode(code);
        // 创建地址默认开启 使用词典server_address_status
        daoEntity.setCanValid(1);
        daoEntity.setCreateBy(SecurityUtils.getUsername());
        daoEntity.setCreateTime(new Date());
        pickUpAddressMapper.insertAddress(daoEntity);
    }

    @Override
    public void update(PickUpAddressPo entity) {
        PickUpAddress pickUpAddress = pickUpAddressMapper.selectOneByAddressName(entity.getAddressName());
        if (null != pickUpAddress && !pickUpAddress.getAddressCode().equals(entity.getAddressCode())) {
            throw new ServiceException("取货地址已存在! 请重新修改.");
        }

        PickUpAddress daoEntity = BeanV1Utils.toBean(entity, PickUpAddress.class);
        daoEntity.setUpdateBy(SecurityUtils.getUsername());
        daoEntity.setUpdateTime(new Date());
        pickUpAddressMapper.updateAddressByCode(daoEntity);
    }

    @Override
    public void updateStatusByCode(String code, Integer canValid) {
        pickUpAddressMapper.updateStatusByCode(code, canValid);
    }

    @Override
    public void deleteByCode(String addressCode) {
        pickUpAddressMapper.deleteAddressByCode(addressCode);
    }

    @Override
    public void deleteByCodes(List<String> addressCodes) {
        pickUpAddressMapper.deleteAddressByCodes(addressCodes);
    }

    @Override
    public List<PickUpAddressVo> selectAll() {
        List<PickUpAddress> pickUpAddresses = pickUpAddressMapper.selectList(new QueryWrapper<>());
        return BeanV1Utils.toBean(pickUpAddresses, PickUpAddressVo.class);
    }
}
