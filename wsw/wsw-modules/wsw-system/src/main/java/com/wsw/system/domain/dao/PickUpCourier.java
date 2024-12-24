package com.wsw.system.domain.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wsw.common.core.web.domain.BaseV1Entity;
import lombok.*;

/**
 * 代取快递管理 DAO
 *
 * @description 版权所有 万事屋
 * @author chenzhongxin
 * @date 2024/12/10
 */
@Data
@TableName("pick_up_courier")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PickUpCourier extends BaseV1Entity {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;

    /**
     * 收货地址code
     */
    private String code;

    /**
     * 这个地址绑定的用户账号 (订单-发出者)
     */
    private String userName;

    /**
     * 取货地址code
     */
    private String pickUpCode;

    /**
     * 收货地址code
     */
    private String deliveryCode;

    /**
     * 选择时间类型 今天 明天 后天
     */
    private String timeType;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 小件数量
     */
    private Integer smallCount;

    /**
     * 中件数量
     */
    private Integer mediumCount;

    /**
     * 大件数量
     */
    private Integer bigCount;

    /**
     * 取件信息 备注姓名-电话号码等信息
     */
    private String pickupMessage;

    /**
     * 备注信息
     */
    private String remake;

    //=======================================================

    /**
     * 接单者
     */
    private String orderTakersUserName;

    /**
     * 订单状态 0-未接单 1-已接单 2-已取货 3-已送达
     */
    private String orderTakersStatus;

    /**
     * 赏金
     */
    private Double reward;

    /**
     * 配送费用
     */
    private Double fulfillmentFee;

    /**
     * 0-未支付, 1-已支付, 2-退款中, 3已退款完成
     */
    private String paymentStatus;

}