<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单用户" prop="userName">
        <el-select v-model="queryParams.userName" filterable placeholder="请选择订单用户账号" popper-class="custom-dropdown">
          <el-option v-for="item in userOptions" :key="item.userName" :label="item.nickName" :value="item.userName" :disabled="item.status == 1" ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="订单状态" prop="status">
        <el-select v-model="queryParams.orderTakersStatus" placeholder="订单状态" clearable>
          <el-option
            v-for="dict in dict.type.order_takers_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['server:pick_up_courier:add']"
        >新增</el-button>
      </el-col>
      <!-- 是否展示搜索栏 -->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="pageList">
      <el-table-column v-if="false" label="代取订单ID" prop="id"/>
      <el-table-column v-if="false" label="代取订单code" prop="code"/>
      <el-table-column v-if="false" label="订单用户code" prop="userName"/>
      <el-table-column v-if="false" label="取货地址code" prop="pickUpCode"/>
      <el-table-column v-if="false" label="收货地址code" prop="deliveryCode"/>
      <el-table-column v-if="false" label="时间类型" prop="timeType"/>
      <el-table-column v-if="false" label="接单用户" prop="orderTakersUserName"/>
      <el-table-column v-if="false" label="订单状态" prop="orderTakersStatus"/>
      <el-table-column v-if="false" label="支付状态" prop="paymentStatus"/>

      <el-table-column width="200" show-overflow-tooltip label="用户昵称" align="left" prop="userNameStr"/>
      <el-table-column width="200" show-overflow-tooltip label="取货地址" align="left" prop="pickUpInfoName"/>
      <el-table-column width="200" show-overflow-tooltip label="收货地址" align="left" prop="deliveryInfoName"/>
      <el-table-column label="时间类型" align="left" prop="timeTypeStr"/>
      <el-table-column label="开始时间" align="left" prop="startTime"/>
      <el-table-column label="结束时间" align="left" prop="endTime"/>
      <el-table-column label="小件数量" align="left" prop="smallCount"/>
      <el-table-column label="中件数量" align="left" prop="mediumCount"/>
      <el-table-column label="大件数量" align="left" prop="bigCount"/>
      <el-table-column label="订单状态" align="left" prop="orderTakersStatusStr"/>
      <el-table-column label="取件信息" width="200" show-overflow-tooltip align="left" prop="pickupMessage"/>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['server:pick_up_courier:add:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['server:pick_up_courier:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改代取订单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item v-if="false" label="代取订单id" prop="id">
          <el-input v-model="form.id" />
        </el-form-item>
        <el-form-item v-if="false" label="代取订单code" prop="code">
          <el-input v-model="form.code" />
        </el-form-item>
        <el-form-item v-if="false" label="代取订单-用户账号" prop="userName">
          <el-input v-model="form.userName" />
        </el-form-item>

        <el-form-item label="取货地址" prop="pickUpCode">
          <el-select v-model="form.pickUpCode" placeholder="请选择取货地址" style="width: 100%;">
            <el-option v-for="item in pickupAddressOptions" 
              :key="item.addressCode" 
              :label="item.addressName" 
              :value="item.addressCode" 
              :disabled="item.canValid === '0'">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="收货地址" prop="deliveryCode">
          <el-select v-model="form.deliveryCode" placeholder="请选择取货地址" style="width: 100%;">
            <el-option v-for="item in deliveryAddressOptions" 
              :key="item.code" 
              :label="`${item.deliveryName} - ${item.fullAddress} - ${item.phoneNum} ${item.isDefaultAddress === '1' ? ' - [默认地址]' : ''}`"
              :value="item.code"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="时间类型"prop="timeType">
          <el-radio-group v-model="form.timeType">
            <el-radio v-for="dict in dict.type.wx_time_type"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <!-- 时间范围的选择 -->
        <el-row :gutter="10">
          <el-col :span="11">
            <el-form-item label="时间选择" prop="startTime">
              <el-time-select
                placeholder="起始时间"
                v-model="form.startTime"
                :picker-options="{
                  start: '09:00',
                  step: '01:00',
                  end: '18:00'
                }"
                @change="handleStartTimeChange"
                >
              </el-time-select>
            </el-form-item>
          </el-col>
          <el-col :span="11">
            <el-form-item prop="endTime">
              <el-time-select
                placeholder="结束时间"
                v-model="form.endTime"
                :picker-options="{
                  start: '09:00',
                  step: '01:00',
                  end: '18:00'
                }"
                @change="handleEndTimeChange"
                >
              </el-time-select> 
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 件数 -->
        <el-form-item label="小件数量" prop="smallCount">
          <el-input-number v-model="form.smallCount" :min="1" :max="99" label="小件数量"></el-input-number>
        </el-form-item>
        <el-form-item label="中件数量" prop="mediumCount">
          <el-input-number v-model="form.mediumCount" :min="0" :max="99" label="中件数量"></el-input-number>
        </el-form-item>
        <el-form-item label="大件数量" prop="bigCount">
          <el-input-number v-model="form.bigCount" :min="0" :max="99" label="大件数量"></el-input-number>
        </el-form-item>
        <!-- 取件信息 备注姓名-电话号码等信息 -->
        <el-form-item label="取件信息" prop="pickupMessage">
          <el-input
            type="textarea"
            placeholder="请备注姓名电话以及粘贴取件信息(此信息仅接单员可见)"
            v-model="form.pickupMessage"
            maxlength="200"
            show-word-limit
          >
          </el-input>
        </el-form-item>
        <!-- 备注 -->
        <el-form-item label="备注" prop="remake">
          <el-input
            type="textarea"
            placeholder=""
            v-model="form.remake"
            maxlength="200"
            show-word-limit
          >
          </el-input>
        </el-form-item>
        <!-- 订单赏金 -->
        <el-form-item label="订单赏金" prop="reward">
          <el-input-number v-model="form.reward" :controls="false" :min="0" :max="10"></el-input-number>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import pickupCourierApi from "@/api/server/pickup_courier";
import pickupAddressApi from "@/api/server/pickup_address";
import deliveryAddressApi from "@/api/server/delivery_address";
import { listAllUser } from "@/api/system/user";


export default {
  name: "PickupCourier",
  dicts: ['order_takers_status','payment_status','wx_time_type'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      codes: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 地址表格数据
      pageList: [],
      // 取货地址选项
      pickupAddressOptions: undefined,
      // 收货地址选项
      deliveryAddressOptions: undefined,
      // 用户列表
      userOptions: undefined,
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        orderTakersStatus: undefined
      },
      // 表单参数
      form: {canValid: '1'},
      // 表单校验
      rules: {
        pickUpCode: [
          { required: true, message: "取货地址不能为空", trigger: "blur" }
        ],
        deliveryCode: [
          { required: true, message: "收货地址不能为空", trigger: "blur" }
        ],
        timeType: [
          { required: true, message: "时间类型不能为空", trigger: "blur" }
        ],
        smallCount: [
          { required: true, message: "小件数量不能为空", trigger: "blur" }
        ],
        mediumCount: [
          { required: true, message: "中件数量不能为空", trigger: "blur" }
        ],
        bigCount: [
          { required: true, message: "大件数量不能为空", trigger: "blur" }
        ],
        pickupMessage: [
          { required: true, message: "取件信息不能为空", trigger: "blur" }
        ],
        startTime: [
          { required: true, message: "开始时间不能为空", trigger: "blur" }
        ],
        endTime: [
          { required: true, message: "结束时间不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
    // 取货地址下拉列表
    this.getPickupAddressOptions();
    // 收货地址下拉列表
    this.getDeliveryAddressOptions();
    // 获取所有用户下拉列表
    this.getUserList();
  },
  methods: {
    // 处理开始时间的变化
    handleStartTimeChange(newStartTime) {
      const [hours, minutes] = newStartTime.split(':').map(Number);
      const newEndHours = hours + 1;

      // 如果结束时间早于新的开始时间，则重置结束时间
      if (newEndHours > 18) {
        this.form.endTime = '';
      } else {
        this.form.endTime = `${String(newEndHours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`;
      }
    },
    // 处理结束时间的变化
    handleEndTimeChange(newEndTime) {
      const [hours, minutes] = newEndTime.split(':').map(Number);
      const newStartHours = hours - 1;

      // 如果开始时间晚于新的结束时间，则重置开始时间
      if (newStartHours < 9) {
        this.form.startTime = '';
      } else {
        this.form.startTime = `${String(newStartHours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`;
      }
    },
    // 比较两个时间的大小
    compareTime(time1, time2) {
      const [h1, m1] = time1.split(':').map(Number);
      const [h2, m2] = time2.split(':').map(Number);
      return h1 !== h2 ? h1 - h2 : m1 - m2;
    },
    /** 查询取货地址列表 */
    getList() {
      this.loading = true;
      pickupCourierApi.getList(this.queryParams).then(response => {
        this.pageList = response.data.records;
        console.log(response.data.records);
        this.pageList.forEach(item => {
          item.pickUpInfoName = item.pickUpInfo.addressName;
          item.deliveryInfoName = item.deliveryInfo.systemAddressFullName +'/'+ item.deliveryInfo.fullAddress;
        });
        this.total = response.data.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        timeType: "0",
        startTime: "09:00",
        endTime: "10:00",
        smallCount: 1,
        mediumCount: 0,
        bigCount: 0,
        reward: 0
      };
      this.resetForm("form");
    },
    /** 查询取货地址 */
    getPickupAddressOptions() {
      pickupAddressApi.getAllList().then(response => {
        this.pickupAddressOptions = response.data;
      });
    },
    /** 查询收货地址 */
    getDeliveryAddressOptions() {
      deliveryAddressApi.getAllList().then(response => {
        this.deliveryAddressOptions = response.data;
      });
    },
    /** 获取所有用户 */
    getUserList() {
      listAllUser().then(response => {
        this.userOptions = response;
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加代取订单";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const code = row.code;
      pickupCourierApi.getInfo(code).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改订单信息";
      });
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        console.log("valid",valid);
        if (valid) {
          let smallCountLet = this.form.smallCount;
          let mediumCountLet = this.form.mediumCount;
          let bigCount = this.form.bigCount;

          let smallFy = 0;
          let mediumFy = 0;
          let bigFy = 0;

          smallFy = smallCountLet * 2;
          mediumFy = mediumCountLet * 5;
          bigFy = bigCount * 10

          if (this.form.code != undefined) {
            this.$confirm('此订单<br>小件费用:'+smallFy +'元<br>中件费用:'+mediumFy + '元<br>大件费用:'+bigFy + '元<br>合计:' + (smallFy+mediumFy+bigFy) +'元', '订单费用提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning',
              dangerouslyUseHTMLString: true  // 启用 HTML 渲染
            }).then(() => {
              pickupCourierApi.update(this.form).then(response => {
                this.$modal.msgSuccess("修改订单成功");
                this.open = false;
                this.getList();
              });
            }).catch(() => {
              this.$message({
                type: 'info',
                message: '已取消修改订单'
              });          
            });
          } else {
            this.$confirm('此订单<br>小件费用:'+smallFy +'元<br>中件费用:'+mediumFy + '元<br>大件费用:'+bigFy + '元<br>合计:' + (smallFy+mediumFy+bigFy) +'元', '订单费用提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning',
              dangerouslyUseHTMLString: true  // 启用 HTML 渲染
            }).then(() => {
              // 合计
              this.form.fulfillmentFee = smallFy+mediumFy+bigFy;
              pickupCourierApi.add(this.form).then(response => {
                this.$modal.msgSuccess("发布订单成功");
                this.open = false;
                this.getList();
              });
            }).catch(() => {
              this.$message({
                type: 'info',
                message: '已取消发布订单'
              });          
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$modal.confirm('是否确认删除数据项？').then(function() {
        return pickupCourierApi.del(row.code);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
  }
};
</script>


<style>
/* 设置 el-select 下拉选项的最大高度 */
.custom-dropdown {
  max-height: 200px; /* 设置最大高度 */
  overflow-y: auto;  /* 自动出现滚动条 */
}
</style>