package com.wsw.system.domain.vo;

import lombok.*;

/**
 * 代取快递管理 Vo
 *
 * @description 版权所有 万事屋
 * @author chenzhongxin
 * @date 2024/12/10
 */
@Data
public class PickUpCourierVo {

    private Long id;

    /**
     * 收货地址code
     */
    private String code;

    /**
     * 这个地址绑定的用户账号 (订单-发出者)
     * */
    private String userName;

    /**
     * 用户昵称
     * */
    private String userNameStr;

    /**
     * 取货地址code
     * */
    private String pickUpCode;

    /**
     * 取货地址详情信息
     * */
    private PickUpAddressVo pickUpInfo;

    /**
     * 收货地址code
     * */
    private String deliveryCode;

    /**
     * 收货地址详情信息
     * */
    private DeliveryAddressVo deliveryInfo;

    /**
     * 选择时间类型 今天 明天 后天
     * */
    private String timeType;

    /**
     * 时间类型展示文本
     * */
    private String timeTypeStr;

    /**
     * 开始时间
     * */
    private String startTime;

    /**
     * 结束时间
     * */
    private String endTime;

    /**
     * 小件数量
     * */
    private Integer smallCount;

    /**
     * 中件数量
     * */
    private Integer mediumCount;

    /**
     * 大件数量
     * */
    private Integer bigCount;

    /**
     * 取件信息 备注姓名-电话号码等信息
     * */
    private String pickupMessage;

    /**
     * 备注信息
     * */
    private String remake;

    //=======================================================

    /**
     * 接单者
     * */
    private String orderTakersUserName;

    /**
     * 接单者昵称
     * */
    private String orderTakersUserNameStr;

    /**
     * 订单状态 0-未接单 1-已接单 2-已取货 3-已送达
     * */
    private String orderTakersStatus;

    /**
     * 订单状态 文本
     * */
    private String orderTakersStatusStr;

    /**
     * 赏金
     * */
    private Double reward;

    /**
     * 配送费用
     * */
    private Double fulfillmentFee;

    /**
     * 0-未支付, 1-已支付, 2-退款中, 3已退款完成
     * */
    private String paymentStatus;

    /**
     * 支付状态 文本
     * */
    private String paymentStatusStr;

}