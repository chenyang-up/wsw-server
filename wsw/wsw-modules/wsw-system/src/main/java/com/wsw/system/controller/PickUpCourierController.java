package com.wsw.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wsw.common.core.web.controller.BaseController;
import com.wsw.common.core.web.domain.AjaxResult;
import com.wsw.common.log.annotation.Log;
import com.wsw.common.log.enums.BusinessType;
import com.wsw.common.security.annotation.RequiresPermissions;
import com.wsw.system.domain.po.PickUpCourierPo;
import com.wsw.system.domain.qo.PickUpCourierQo;
import com.wsw.system.domain.vo.PickUpCourierVo;
import com.wsw.system.service.PickUpCourierService;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * 代取快递管理 信息操作处理
 *
 * @author chenzhongxin
 * @date 2024/12/23
 */
@RestController
@RequestMapping("/pick_up_courier")
public class PickUpCourierController extends BaseController {

    @Resource
    private PickUpCourierService pickUpCourierService;

    /**
     * 代取管理-分页查询
     */
    @RequiresPermissions("server:pick_up_courier:list")
    @GetMapping("/getPageList")
    public AjaxResult page(PickUpCourierQo qo) {
        Page<PickUpCourierVo> pageList = pickUpCourierService.getPageList(qo);
        return success(pageList);
    }

    /**
     * 获取代取快递订单信息
     */
    @GetMapping("/getInfoByCode/{code}")
    public AjaxResult getInfo(@PathVariable String code) {
        return success(pickUpCourierService.getInfoByCode(code));
    }

    /**
     * 新增-代取订单
     */
    @RequiresPermissions("server:pick_up_courier:add")
    @Log(title = "代取管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@Validated @RequestBody PickUpCourierPo po) {
        pickUpCourierService.insert(po);
        return success();
    }

    /**
     * 修改-代取订单
     */
    @RequiresPermissions("server:pick_up_courier:edit")
    @Log(title = "代取管理", businessType = BusinessType.UPDATE)
    @PutMapping("/edit")
    public AjaxResult edit(@Validated @RequestBody PickUpCourierPo po) {
        pickUpCourierService.updateByCode(po);
        return success();
    }

    /**
     * 删除-代取订单
     */
    @RequiresPermissions("server:pick_up_courier:remove")
    @Log(title = "代取管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/delete/{code}")
    public AjaxResult deleteBatch(@PathVariable String code) {
        pickUpCourierService.deleteByCode(code);
        return success();
    }

    /**
     * 修改-代取订单-支付订单接口
     */
    @RequiresPermissions("server:pick_up_courier:edit")
    @Log(title = "代取管理", businessType = BusinessType.UPDATE)
    @PutMapping("/payOrders")
    public AjaxResult payOrders(@Param("code") String code) {
        Boolean sfPay = pickUpCourierService.updatePaymentStatusByCodeForPay(code);
        if (!sfPay) {
            return error("支付失败! 若是已完成支付出现支付失败,请联系086-13333进行处理.");
        }
        return success();
    }

    /**
     * 代取订单-退款接口
     */
    @RequiresPermissions("server:pick_up_courier:edit")
    @Log(title = "代取管理", businessType = BusinessType.UPDATE)
    @PutMapping("/refund")
    public AjaxResult refund(@Param("code") String code) {
        Boolean sfRefund = pickUpCourierService.updateRefundStatusByCodeForRefund(code);
        if (!sfRefund) {
            return error("退款申请成功!");
        }
        return success();
    }

    /**
     * 代取订单-取消退款
     */
    @RequiresPermissions("server:pick_up_courier:edit")
    @Log(title = "代取管理", businessType = BusinessType.UPDATE)
    @PutMapping("/cancelRefund")
    public AjaxResult cancelRefund(@Param("code") String code) {
        Boolean sfCancelRefund = pickUpCourierService.updateRefundStatusByCodeForCancelRefund(code);
        if (!sfCancelRefund) {
            return error("取消退款失败!请刷新重试.");
        }
        return success();
    }

    /**
     * 代取订单-接单接口
     *
     * @author chenzhongxin
     * @date 2024/12/30 01:51
     */
    @RequiresPermissions("server:pick_up_courier:edit")
    @Log(title = "代取管理", businessType = BusinessType.UPDATE)
    @PutMapping("/takeOrders")
    public AjaxResult takeOrders(@Param("code") String code) {
        pickUpCourierService.updateOrderStatusByCodeForTakeOrders(code);
        return success();
    }

    /**
     * 代取订单-接单接口-更改订单状态-已取货
     *
     * @author chenzhongxin
     * @date 2024/12/30
     */
    @RequiresPermissions("server:pick_up_courier:edit")
    @Log(title = "代取管理", businessType = BusinessType.UPDATE)
    @PutMapping("/picked")
    public AjaxResult picked(@Param("code") String code) {
        // 2-已取货
        pickUpCourierService.updateOrderStatusByCode(code, "2");
        return success();
    }

    /**
     * 代取订单-接单接口-更改订单状态-已送达
     *
     * @author chenzhongxin
     * @date 2024/12/31
     */
    @RequiresPermissions("server:pick_up_courier:edit")
    @Log(title = "代取管理", businessType = BusinessType.UPDATE)
    @PutMapping("/delivered")
    public AjaxResult delivered(@Param("code") String code) {
        // 3-已送达
        pickUpCourierService.updateOrderStatusByCode(code, "3");
        return success();
    }

    /**
     * 代取订单-定期删除-远程调用使用
     *
     * @author chenzhongxin
     * @date 2024/12/31
     */
    @DeleteMapping("/deleteForDataOut15Minutes")
    public AjaxResult deleteForDataOut15Minutes() {
        pickUpCourierService.deleteForDataOut15Minutes();
        return success();
    }
}
