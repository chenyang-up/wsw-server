package com.wsw.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wsw.common.core.web.controller.BaseController;
import com.wsw.common.core.web.domain.AjaxResult;
import com.wsw.common.log.annotation.Log;
import com.wsw.common.log.enums.BusinessType;
import com.wsw.common.security.annotation.RequiresPermissions;
import com.wsw.system.domain.po.PickUpAddressPo;
import com.wsw.system.domain.qo.PickUpAddressQo;
import com.wsw.system.domain.vo.PickUpAddressVo;
import com.wsw.system.service.IPickUpAddressService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * 取货地址 信息操作处理
 *
 * @author chenzhongxin
 * @date 2024/11/30
 */
@RestController
@RequestMapping("/pick_up_address")
public class PickUpAddressController extends BaseController {
    @Resource
    private IPickUpAddressService pickUpAddressService;

    /**
     * 取货地址分页查询
     */
    @RequiresPermissions("server:pick_up_address:list")
    @GetMapping("/getPageList")
    public AjaxResult page(PickUpAddressQo qo) {
        Page<PickUpAddressVo> pageList = pickUpAddressService.selectPageList(qo);
        return success(pageList);
    }

    /**
     * 获取取货地址信息
     */
    @GetMapping("/getInfoByCode/{addressCode}")
    public AjaxResult getInfo(@PathVariable String addressCode) {
        return success(pickUpAddressService.getInfo(addressCode));
    }

    /**
     * 新增取货地址
     */
    @RequiresPermissions("server:pick_up_address:add")
    @Log(title = "取货地址管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody PickUpAddressPo entity) {
        pickUpAddressService.insert(entity);
        return success();
    }

    /**
     * 修改取货地址
     */
    @RequiresPermissions("server:pick_up_address:edit")
    @Log(title = "取货地址管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody PickUpAddressPo entity) {
        pickUpAddressService.update(entity);
        return success();
    }

    /**
     * 修改取货地址状态
     */
    @RequiresPermissions("server:pick_up_address:edit")
    @Log(title = "取货地址管理", businessType = BusinessType.UPDATE)
    @PutMapping("/upadteStatus")
    public AjaxResult upadteStatus(@RequestBody PickUpAddressPo entity) {
        pickUpAddressService.updateStatusByCode(entity.getAddressCode(), entity.getCanValid());
        return success();
    }

    /**
     * 删除取货地址(批量)
     */
    @RequiresPermissions("server:pick_up_address:remove")
    @Log(title = "取货地址管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/deleteBatch")
    public AjaxResult deleteBatch(@RequestBody PickUpAddressPo po) {
        if (CollectionUtils.isEmpty(po.getAddressCodes())) {
            return error("addressCodes列表选择为空!");
        }
        pickUpAddressService.deleteByCodes(po.getAddressCodes());
        return success();
    }

    /**
     * 获取所有取货地址列表(非分页)
     * 下拉使用
     *
     * @author chenzhongxin
     * @date 2024/12/27 09:07
     */
    @GetMapping("/getPickupAddressList")
    public AjaxResult getPickupAddressList() {
        List<PickUpAddressVo> pickUpAddressVos = pickUpAddressService.selectAll();
        return success(pickUpAddressVos);
    }

}
