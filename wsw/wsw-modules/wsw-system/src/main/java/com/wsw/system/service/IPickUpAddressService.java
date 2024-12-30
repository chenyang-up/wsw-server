package com.wsw.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wsw.system.domain.po.PickUpAddressPo;
import com.wsw.system.domain.qo.PickUpAddressQo;
import com.wsw.system.domain.vo.PickUpAddressVo;

import java.util.List;


/**
 * 取货地址 服务层
 *
 * @author chenzhongxin
 * @date 2024/11/30
 */
public interface IPickUpAddressService {

    /**
     * 分页查询取货地址
     * */
    Page<PickUpAddressVo> selectPageList(PickUpAddressQo qo);

    /**
     * 通过 addressCode 获取取货地址信息
     * */
    PickUpAddressVo getInfo(String addressCode);

    /**
     * 通过code列表,获取取货地址列表信息
     * */
    List<PickUpAddressVo> getInfoListByCodes(List<String> codes);

    void insert(PickUpAddressPo entity);

    void update(PickUpAddressPo entity);

    /**
     * 更新取货地址状态
     *
     * @author chenzhongxin
     * @date 2024/11/30 05:03
     */
    void updateStatusByCode(String code, Integer canValid);

    void deleteByCode(String addressCode);

    /**
     * 批量删除
     * */
    void deleteByCodes(List<String> addressCodes);

    /**
     * 获取所有取货地址数据
     * */
    List<PickUpAddressVo> selectAll();

}
