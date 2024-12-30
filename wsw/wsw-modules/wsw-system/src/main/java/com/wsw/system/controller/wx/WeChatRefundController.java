package com.wsw.system.controller.wx;

import com.wsw.common.core.exception.ServiceException;
import com.wsw.common.core.utils.XmlUtils;
import com.wsw.common.core.utils.bean.BeanV1Utils;
import com.wsw.system.domain.po.PickUpCourierPo;
import com.wsw.system.domain.vo.PickUpCourierVo;
import com.wsw.system.service.PickUpCourierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/wechat")
public class WeChatRefundController {

    @Resource
    private PickUpCourierService pickUpCourierService;

    @PostMapping("/refund/notify")
    public String handleRefundNotify(@RequestBody String xmlData) {
        try {
            // 解析和处理 XML 数据
            Map<String, String> refundData = XmlUtils.parseXml(xmlData);
            // 退款状态
            String refundStatus = refundData.get("refund_status");
            // 退款单号
            String refundId = refundData.get("refund_id");
            // 退款关闭状态
            String outRefundNo = refundData.get("out_refund_no");

            // 获取订单信息
            PickUpCourierVo info = pickUpCourierService.getInfoByCode(refundId);
            if ("SUCCESS".equals(refundStatus)) {
                // 3-退款成功
                info.setPaymentStatus("3");
                PickUpCourierPo po = BeanV1Utils.toBean(info, PickUpCourierPo.class);
                // 更新订单状态为退款成功
                pickUpCourierService.updateByCode(po);
            } else if ("CHANGE".equals(refundStatus)) {
                throw new ServiceException("退款异常");
            }
        } catch (Exception e) {
            log.error("微信退款回调处理异常! {}", e.getMessage());
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[微信退款回调处理异常]]></return_msg></xml>";
        }
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }
}