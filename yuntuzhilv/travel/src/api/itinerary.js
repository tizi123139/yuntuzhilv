import request from '../utils/request'

/**
 * 获取行程详情
 * @param {string} id - 行程 ID
 * @returns {Promise<Object>} 行程完整数据
 * @returns {string} returns.id - 行程 ID
 * @returns {string} returns.title - 行程标题
 * @returns {number} returns.totalBudget - 总预算
 * @returns {string} returns.fromCity - 出发地
 * @returns {string} returns.toCity - 目的地
 * @returns {number} returns.days - 天数
 * @returns {string} returns.status - 状态：planned | active | completed
 * @returns {string} returns.createTime - 创建时间
 * @returns {Array<Object>} returns.timeline - 每日行程时间线
 * @returns {Array<Object>} returns.hotelRecommendations - 酒店推荐列表
 * @returns {string} returns.hotelRecommendations[].name - 酒店名称
 * @returns {string} returns.hotelRecommendations[].desc - 酒店描述
 * @returns {number} returns.hotelRecommendations[].price - 起步价（元/晚）
 * @returns {string} returns.hotelRecommendations[].image - 图片 URL
 * @returns {Array<Object>} returns.foodRecommendations - 美食推荐列表
 * @returns {string} returns.foodRecommendations[].name - 餐厅/美食名称
 * @returns {string} returns.foodRecommendations[].desc - 描述
 * @returns {number} returns.foodRecommendations[].price - 人均消费（元）
 * @returns {string} returns.foodRecommendations[].image - 图片 URL
 */
export function getItineraryDetailApi(id) {
  return request.get('/itinerary/detail', { params: { id } })
}

/**
 * 保存行程到后端
 * @param {Object} data - 行程完整数据（与 getItineraryDetailApi 返回结构一致）
 * @returns {Promise<Object>} 保存结果
 * @returns {string} returns.id - 保存后的行程 ID
 * @returns {string} returns.message - 提示信息
 */
export function saveItineraryApi(data) {
  return request.post('/itinerary/save', data)
}

/**
 * 获取当前用户的行程列表
 * @returns {Promise<Object>} 列表数据
 * @returns {Array<Object>} returns.list - 行程列表
 * @returns {string} returns.list[].id - 行程 ID
 * @returns {string} returns.list[].title - 行程标题
 * @returns {number} returns.list[].totalBudget - 总预算
 * @returns {string} returns.list[].fromCity - 出发地
 * @returns {string} returns.list[].toCity - 目的地
 * @returns {number} returns.list[].days - 天数
 * @returns {number} returns.list[].people - 人数
 * @returns {string} returns.list[].status - 状态：planned | active | completed
 * @returns {string} returns.list[].createTime - 创建时间
 * @returns {string[]} returns.list[].tags - 兴趣标签
 */
export function myItineraryListApi() {
  return request.get('/itinerary/myList')
}

/**
 * 删除行程
 * @param {string} id - 行程 ID
 * @returns {Promise<Object>} 删除结果
 * @returns {boolean} returns.success - 是否成功
 */
export function deleteItineraryApi(id) {
  return request.post('/itinerary/delete', { id })
}

/**
 * 预订行程
 * @param {string} id - 行程 ID
 * @returns {Promise<Object>} 预订结果
 * @returns {string} returns.message - 预订结果提示
 */
export function bookItineraryApi(id) {
  return request.post('/itinerary/book', { id })
}

/**
 * 导出行程为 PDF（返回文件流）
 * @param {string} id - 行程 ID
 * @returns {Promise<Blob>} PDF 文件 Blob
 */
export function exportItineraryPdfApi(id) {
  return request.get('/itinerary/export', {
    params: { id },
    responseType: 'blob'
  })
}
