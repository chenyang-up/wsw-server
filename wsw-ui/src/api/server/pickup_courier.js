import request from '@/utils/request'

const pickupCourierApi = {

  // 查询订单信息列表-分页查询
  getList(query) {
    return request({
      url: '/system/pick_up_courier/getPageList',
      method: 'get',
      params: query
    })
  },

  // 查询订单信息详情信息
  getInfo(code) {
    return request({
      url: '/system/pick_up_courier/getInfoByCode/' + code,
      method: 'get'
    })
  },

  // 新增-订单信息
  add(data) {
    return request({
      url: '/system/pick_up_courier/add',
      method: 'post',
      data: data
    })
  },

  // 修改-订单信息
  update(data) {
    return request({
      url: '/system/pick_up_courier/edit',
      method: 'put',
      data: data
    })
  },

  // 删除-订单信息
  del(code) {
    return request({
      url: '/system/pick_up_courier/delete/' + code,
      method: 'delete'
    })
  },
}
// 导出对象
export default pickupCourierApi;