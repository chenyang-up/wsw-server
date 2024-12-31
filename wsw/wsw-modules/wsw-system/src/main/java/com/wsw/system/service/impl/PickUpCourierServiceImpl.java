package com.wsw.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wsw.common.core.exception.ServiceException;
import com.wsw.common.core.utils.bean.BeanV1Utils;
import com.wsw.common.redis.service.RedisService;
import com.wsw.common.security.utils.SecurityUtils;
import com.wsw.system.api.domain.SysUser;
import com.wsw.system.domain.dao.SystemAddress;
import com.wsw.system.domain.po.PickUpCourierPo;
import com.wsw.system.domain.qo.PickUpCourierQo;
import com.wsw.system.domain.vo.DeliveryAddressVo;
import com.wsw.system.domain.vo.PickUpAddressVo;
import com.wsw.system.domain.vo.PickUpCourierVo;
import com.wsw.system.domain.vo.SystemAddressVo;
import com.wsw.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wsw.system.mapper.PickUpCourierMapper;
import com.wsw.system.domain.dao.PickUpCourier;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 代取快递管理 Service业务层处理
 *
 * @description 版权所有 wsw
 * @author chenzhongxin
 * @date 2024/12/23 04:34
 */
@Slf4j
@Service
public class PickUpCourierServiceImpl implements PickUpCourierService {

    /** 订单状态 */
    private static final String ORDER_TAKERS_STATUS = "order_takers_status";

    /** 支付状态 */
    private static final String PAYMENT_STATUS = "payment_status";

    /** 时间类型 */
    private static final String TIME_TYPE = "wx_time_type";

    /** redis锁前缀 */
    private static final String ORDER_LOCK_PREFIX = "order_lock_:";

    @Resource
    private PickUpCourierMapper pickUpCourierMapper;

    /** 取货地址Service */
    @Resource
    private IPickUpAddressService pickUpAddressService;

    /** 收货地址Service */
    @Resource
    private DeliveryAddressService deliveryAddressService;

    /** 词典Service */
    @Resource
    private ISysDictDataService sysDictDataService;

    /** 用户Service */
    @Resource
    private ISysUserService userService;

    /** 系统地址Service */
    @Resource
    private ISystemAddressService systemAddressService;

    /** redis Service */
    @Resource
    private RedisService redisService;


    @Override
    public Page<PickUpCourierVo> getPageList(PickUpCourierQo qo) {
        Page<PickUpCourier> pageList = pickUpCourierMapper.selectPageList(qo);
        Page<PickUpCourierVo> newPageList = BeanV1Utils.toBean(pageList, PickUpCourierVo.class);

        // 列表信息
        List<PickUpCourierVo> pickUpInfoList = newPageList.getRecords();

        Map<String, PickUpAddressVo> pickUpAddressVoMap = new HashMap<>();
        Map<String, DeliveryAddressVo> deliveryAddressVoMap = new HashMap<>();
        Map<String, String> userNameMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(pickUpInfoList)) {
            // 取货地址信息列表
            List<String> pickUpCodeList = pickUpInfoList.stream().map(PickUpCourierVo::getPickUpCode).collect(Collectors.toList());
            List<PickUpAddressVo> pickUpAddressVoList = pickUpAddressService.getInfoListByCodes(pickUpCodeList);
            if (CollectionUtils.isNotEmpty(pickUpAddressVoList)) {
                pickUpAddressVoMap = pickUpAddressVoList.stream().collect(Collectors.toMap(PickUpAddressVo::getAddressCode, Function.identity()));
            }

            // 收货地址信息列表
            List<String> deliveryCodes = pickUpInfoList.stream().map(PickUpCourierVo::getDeliveryCode).collect(Collectors.toList());
            List<DeliveryAddressVo> deliveryAddressVos = deliveryAddressService.selectInfoByCodes(deliveryCodes);
            if (CollectionUtils.isNotEmpty(deliveryAddressVos)) {
                // 系统地址数据
                List<String> systemAddressCodeList = deliveryAddressVos.stream().map(DeliveryAddressVo::getSystemAddressCode).collect(Collectors.toList());
                Map<String, String> systemAddressMap= systemAddressService.selectListByCodes(systemAddressCodeList).stream().collect(Collectors.toMap(SystemAddress::getCode, SystemAddress::getFullName));
                // 数据填充
                for (DeliveryAddressVo vo : deliveryAddressVos) {
                    vo.setSystemAddressFullName(systemAddressMap.getOrDefault(vo.getSystemAddressCode(), "未知类型地址"));
                }
                deliveryAddressVoMap = deliveryAddressVos.stream().collect(Collectors.toMap(DeliveryAddressVo::getCode, Function.identity()));
            }

            // 发布订单用户
            List<String> userNameList = pickUpInfoList.stream().map(PickUpCourierVo::getUserName).collect(Collectors.toList());
            // 接单用户信息
            List<String> orderTakersUserNameList = pickUpInfoList.stream().map(PickUpCourierVo::getOrderTakersUserName).collect(Collectors.toList());

            List<String> allUserNameList = new ArrayList<>();
            allUserNameList.addAll(userNameList);
            allUserNameList.addAll(orderTakersUserNameList);
            List<SysUser> usersInfoList = userService.selectUserListByUserNames(allUserNameList);
            if (CollectionUtils.isNotEmpty(usersInfoList)) {
                userNameMap = usersInfoList.stream().collect(Collectors.toMap(SysUser::getUserName, SysUser::getNickName));
            }
        }

        // 词典数据
        Map<String, String> orderTakersStatusMap = sysDictDataService.selectDictDataMap(ORDER_TAKERS_STATUS);
        Map<String, String> paymentStatusMap = sysDictDataService.selectDictDataMap(PAYMENT_STATUS);
        Map<String, String> timeTypeMap = sysDictDataService.selectDictDataMap(TIME_TYPE);

        // 填充数据
        for (PickUpCourierVo vo : pickUpInfoList) {
            vo.setUserNameStr(userNameMap.getOrDefault(vo.getUserName(), ""));
            vo.setOrderTakersUserNameStr(userNameMap.getOrDefault(vo.getOrderTakersUserName(), ""));
            vo.setTimeTypeStr(timeTypeMap.getOrDefault(vo.getTimeType(), "未知类型"));
            // 取货地址
            vo.setPickUpInfo(pickUpAddressVoMap.getOrDefault(vo.getPickUpCode(), null));
            // 收货地址
            vo.setDeliveryInfo(deliveryAddressVoMap.getOrDefault(vo.getDeliveryCode(), null));
            // 支付状态
            vo.setPaymentStatusStr(paymentStatusMap.getOrDefault(vo.getPaymentStatus(), "未知状态"));
            // 订单状态
            vo.setOrderTakersStatusStr(orderTakersStatusMap.getOrDefault(vo.getOrderTakersStatus(), "未知状态"));
        }
        return newPageList;
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return pickUpCourierMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(PickUpCourierPo po) {
        // 产生code
        String code = UUID.randomUUID().toString().replace("-", "");
        po.setCode(code);
        PickUpCourier info = BeanV1Utils.toBean(po, PickUpCourier.class);

        // 当前登录用户
        info.setUserName(SecurityUtils.getUsername());
        // 未接单
        info.setOrderTakersStatus("0");
        // 未支付
        info.setPaymentStatus("0");

        info.setCreateBy(SecurityUtils.getUsername());
        info.setCreateTime(new Date());
        pickUpCourierMapper.insert(info);
        return 0;
    }

    @Override
    public int insertSelective(PickUpCourier record) {
        return pickUpCourierMapper.insertSelective(record);
    }

    @Override
    public PickUpCourier selectByPrimaryKey(Long id) {
        return pickUpCourierMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(PickUpCourier record) {
        return pickUpCourierMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(PickUpCourier record) {
        return pickUpCourierMapper.updateByPrimaryKey(record);
    }

    @Override
    public int updateByCode(PickUpCourierPo po) {
        PickUpCourier info = BeanV1Utils.toBean(po, PickUpCourier.class);
        info.setUpdateBy(SecurityUtils.getUsername());
        info.setUpdateTime(new Date());
        return pickUpCourierMapper.updateByCode(info);
    }

    @Override
    public PickUpCourierVo getInfoByCode (String code) {
        PickUpCourier info = pickUpCourierMapper.selectByCode(code);
        PickUpCourierVo infoVo = BeanV1Utils.toBean(info, PickUpCourierVo.class);
        return infoVo;
    }

    @Override
    public void deleteByCode(String code) {
        pickUpCourierMapper.deleteByCode(code);
    }

    @Override
    public Boolean updatePaymentStatusByCodeForPay(String code) {
        // 读取订单详情
        PickUpCourier pickUpCourierInfo = pickUpCourierMapper.selectByCode(code);

        Boolean sfPay = true;
        try {
            // TODO 调用微信支付接口,返回支付状态
            // 发起支付接口调用
        } catch (Exception e) {
            sfPay = false;
            log.error("调用微信支付接口失败", e);
        }

        if (sfPay) {
            // 1-已支付
            pickUpCourierInfo.setPaymentStatus("1");
        } else {
            // 4-支付失败
            pickUpCourierInfo.setPaymentStatus("4");
        }
        pickUpCourierMapper.updateByCode(pickUpCourierInfo);
        return sfPay;
    }

    @Override
    @Transactional
    public Boolean updateRefundStatusByCodeForRefund(String code) {
        // 读取订单详情
        PickUpCourier pickUpCourierInfo = pickUpCourierMapper.selectByCode(code);
        String paymentStatus = pickUpCourierInfo.getPaymentStatus();
        // 判断是否支付成功
        if (!"1".equals(paymentStatus)) {
            throw new SecurityException("订单未支付,不能进行退款申请!");
        }

        Boolean sfRefund = true;
        try {
            // TODO 调用微信退款接口,返回退款状态
        } catch (Exception e) {
            sfRefund = false;
            log.error("调用微信退款接口失败", e);
        }

        if (sfRefund) {
            // 2-退款中
            pickUpCourierInfo.setPaymentStatus("2");
        } else {
            // 5-退款失败
            pickUpCourierInfo.setPaymentStatus("5");
        }
        pickUpCourierMapper.updateByCode(pickUpCourierInfo);
        return sfRefund;
    }

    @Override
    @Transactional
    public Boolean updateRefundStatusByCodeForCancelRefund(String code) {
        // 读取订单详情
        PickUpCourier pickUpCourierInfo = pickUpCourierMapper.selectByCode(code);
        String paymentStatus = pickUpCourierInfo.getPaymentStatus();
        // 判断是否进行退款
        if (!"2".equals(paymentStatus)) {
            throw new SecurityException("订单处于退款过程中,不能取消退款申请!");
        }

        Boolean sfCancelRefund = true;
        try {
            // TODO 调用微信取消退款接口,返回取消退款状态
        } catch (Exception e) {
            sfCancelRefund = false;
            log.error("调用微信取消退款接口失败", e);
        }

        if (sfCancelRefund) {
            // 1-已支付
            pickUpCourierInfo.setPaymentStatus("1");
        }
        pickUpCourierMapper.updateByCode(pickUpCourierInfo);
        return sfCancelRefund;
    }

    @Override
    public void updateOrderStatusByCodeForTakeOrders(String code) {
        String lockKey = ORDER_LOCK_PREFIX + code;
        // 添加Redis的锁
        acceptOrderWithRedisLock(code, lockKey);
        try {
            PickUpCourier info = pickUpCourierMapper.selectByCode(code);
            String orderTakersStatus = info.getOrderTakersStatus();
            if (!"0".equals(orderTakersStatus)) {
                if ("1".equals(orderTakersStatus)) {
                    throw new ServiceException("订单已被其他人接单,请尝试其他订单!");
                }
                throw new ServiceException("订单状态不正确!无法接单");
            }
            // 1-已接单
            info.setOrderTakersStatus("1");
            info.setOrderTakersUserName(SecurityUtils.getUsername());
            pickUpCourierMapper.updateByCode(info);
        } finally {
            // 释放锁
            redisService.deleteObject(lockKey);
        }
    }

    @Override
    public void updateOrderStatusByCode(String code, String orderTakersStatus) {
        PickUpCourier info = pickUpCourierMapper.selectByCode(code);
        // 更新状态
        info.setOrderTakersStatus(orderTakersStatus);
        pickUpCourierMapper.updateByCode(info);
    }

    @Override
    public void deleteForDataOut15Minutes() {
        pickUpCourierMapper.deleteForDataOut15Minutes();
    }

    /**
     * 接单接口添加锁,解决并发问题
     * */
    private void acceptOrderWithRedisLock (String code, String lockKey) {
        // 判断是否已经存在锁
        if (redisService.hasKey(lockKey)) {
            throw new ServiceException("订单已被其他人接单,请刷新重试");
        }
        // 添加锁 - 锁的有效期10秒
        redisService.setCacheObject(lockKey, code, 10L, TimeUnit.SECONDS);
    }

}
