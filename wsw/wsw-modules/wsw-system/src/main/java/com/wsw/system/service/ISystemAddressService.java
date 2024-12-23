package com.wsw.system.service;


import com.wsw.system.domain.dao.SystemAddress;
import com.wsw.system.domain.po.SystemAddressPo;
import com.wsw.system.domain.qo.SystemAddressQo;
import com.wsw.system.domain.vo.SystemAddressVo;

import java.util.List;


/**
 * 地址管理 服务层
 *
 * @author chenzhongxin
 * @date 2024/12/02
 */
public interface ISystemAddressService {

    /**
     * 查询地址列表
     * @author chenzhongxin
     * @date 2024/12/02
     * */
    List<SystemAddressVo> selectList(SystemAddressQo qo);

    /**
     * 查询地址列表(排出自身节点)
     * @author chenzhongxin
     * @date 2024/12/03
     * */
    List<SystemAddressVo> selectExcludeList(String code);

    /**
     * 通过code列表获取系统地址列表
     *
     * @author chenzhongxin
     * @date 2024/12/05 11:54
     */

    List<SystemAddress> selectListByCodes (List<String> codes);

    /**
     * 通过 addressCode 获取地址信息
     *
     * @author chenzhongxin
     * @date 2024/12/02
     * */
    SystemAddressVo getInfo(String addressCode);

    void insert(SystemAddressPo entity);

    void update(SystemAddressPo entity);

    /**
     * 更新地址状态
     *
     * @author chenzhongxin
     * @date 2024/12/02
     */
    void updateStatusByCode(String code, String addressStatus);

    void deleteByCode(String addressCode);

}
