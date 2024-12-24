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

}
