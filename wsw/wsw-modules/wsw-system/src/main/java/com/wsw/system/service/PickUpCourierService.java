package com.wsw.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wsw.system.domain.dao.PickUpCourier;
import com.wsw.system.domain.po.PickUpCourierPo;
import com.wsw.system.domain.qo.PickUpCourierQo;
import com.wsw.system.domain.vo.PickUpCourierVo;

/**
 * 代取快递管理 Service
 *
 * @description 版权所有 wsw
 * @author chenzhongxin
 * @date 2024/12/23 04:33
 */

public interface PickUpCourierService{

    /**
     * 代取快递管理-分页查询
     *
     * @author chenzhongxin
     * @date 2024/12/23 04:41
     */
    Page<PickUpCourierVo> getPageList(PickUpCourierQo qo);

    int deleteByPrimaryKey(Long id);

    /**
     * 代取快递管理-新增
     *
     * @author chenzhongxin
     * @date 2024/12/23 04:56
     */
    int insert(PickUpCourierPo po);

    int insertSelective(PickUpCourier record);

    PickUpCourier selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PickUpCourier record);

    /**
     * 代取快递管理-修改(主键)
     *
     * @author chenzhongxin
     * @date 2024/12/23 04:56
     */
    int updateByPrimaryKey(PickUpCourier record);

    /**
     * 代取快递管理-修改(code)
     *
     * @author chenzhongxin
     * @date 2024/12/23 04:56
     */
    int updateByCode(PickUpCourierPo po);

    /**
     * 代取快递管理-查询(code)
     * */
    PickUpCourierVo getInfoByCode(String code);

    /**
     * 通过code去删除代取订单信息
     * */
    void deleteByCode(String code);

}
