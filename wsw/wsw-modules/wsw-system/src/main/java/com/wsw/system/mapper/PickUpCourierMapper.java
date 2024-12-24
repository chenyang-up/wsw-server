package com.wsw.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wsw.system.domain.dao.PickUpCourier;
import com.wsw.system.domain.qo.PickUpCourierQo;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

/**
 * 代取快递管理 Mapper
 *
 * @description 版权所有 wsw
 * @author chenzhongxin
 * @date 2024/12/23 04:32
 */

@Mapper
public interface PickUpCourierMapper extends BaseMapper<PickUpCourier> {

    /**
     * 代取快递-分页查询
     *
     * @author chenzhongxin
     * @date 2024/12/23 04:44
     */
    default Page<PickUpCourier> selectPageList(PickUpCourierQo qo) {
        Page<PickUpCourier> page = new Page<>(qo.getPageNo(), qo.getPageSize());
        // 查询条件（可选）
        QueryWrapper<PickUpCourier> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(qo.getUserName())) {
            queryWrapper.like("user_name", qo.getUserName());
        }
        if (StringUtils.isNotBlank(qo.getOrderTakersStatus())) {
            queryWrapper.eq("order_takers_status", qo.getOrderTakersStatus());
        }
        queryWrapper.orderByDesc("create_time");
        // 执行分页查询
        return selectPage(page, queryWrapper);
    }


    int deleteByPrimaryKey(Long id);

    @Override
    int insert(PickUpCourier record);

    int insertSelective(PickUpCourier record);

    PickUpCourier selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PickUpCourier record);

    int updateByPrimaryKey(PickUpCourier record);

    /**
     * 通过code去修改快递代取信息
     * */
    default int updateByCode(PickUpCourier record) {
        return update(record, new QueryWrapper<PickUpCourier>().eq("code", record.getCode()));
    }

    /**
     * 通过code去删除快递代取信息
     * */
    default void deleteByCode(String code) {
        delete(new QueryWrapper<PickUpCourier>().eq("code", code));
    }

    /**
     * 通过code去查询快递代取信息
     * */
    default PickUpCourier selectByCode(String code) {
        return selectOne(new QueryWrapper<PickUpCourier>().eq("code", code));
    }
}