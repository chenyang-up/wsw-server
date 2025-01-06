package com.wsw.job.feign;

import com.wsw.common.core.web.domain.AjaxResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 代取远程调用 Feign
 *
 * @author chenzhongxin
 * @date 2024/12/31 02:06
 */
@FeignClient(name = "wsw-system", contextId = "pickUpCourierFeign", path = "/pick_up_courier")
public interface PickUpCourierFeign {

    /**
     * 定期删除15分钟前的代取订单
     *
     * @author chenzhongxin
     * @date 2024/12/31
     */
    @DeleteMapping("/deleteForDataOutTime/{minutes}")
    public AjaxResult deleteForDataOutTime(@PathVariable("minutes") Integer minutes);

}
