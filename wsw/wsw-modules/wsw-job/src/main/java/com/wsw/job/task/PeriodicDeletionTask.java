package com.wsw.job.task;

import com.wsw.job.feign.PickUpCourierFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时任务调度测试 - 周期删除
 * 
 * @author wsw
 */
@Slf4j
@Component("periodicDeletion")
public class PeriodicDeletionTask {

    @Resource
    private PickUpCourierFeign pickUpCourierFeign;

    /**
     * 周期删除任务
     *
     * @author chenzhongxin
     * @date 2024/12/31 01:46
     */
    public void periodicDeletionTask() {
        // 代取订单周期删除 (针对未支付且订单时间超过15分钟数据)
        log.info("代取订单周期删除开始...");
        pickUpCourierFeign.deleteForDataOutTime(15);
        log.info("代取订单周期删除结束...");
    }
}
