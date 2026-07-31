import request from '../utils/request'

/**
 * 获取行程详情
 * @param {string} id - 行程 ID
 * @returns {Promise<Object>} 行程完整数据
 */
export function getItineraryDetailApi(id) {
  return request.get(`/itinerary/${id}`)
}

/**
 * 保存行程到后端（保存 AI 生成的行程）
 * @param {Object} data - 行程完整数据
 * @returns {Promise<Object>} 保存结果
 */
export function saveItineraryApi(data) {
  return request.post('/itinerary/save', data)
}

/**
 * 获取当前用户的行程列表
 * @param {number} pageNum - 页码
 * @param {number} pageSize - 每页数量
 * @returns {Promise<Object>} 列表数据
 */
export function myItineraryListApi(pageNum = 1, pageSize = 10) {
  return request.get('/itinerary', { params: { pageNum, pageSize } })
}

/**
 * 删除行程
 * @param {string} id - 行程 ID
 * @returns {Promise<Object>} 删除结果
 */
export function deleteItineraryApi(id) {
  return request.delete(`/itinerary/${id}`)
}

/**
 * 归档行程
 * @param {string} id - 行程 ID
 * @returns {Promise<Object>} 归档结果
 */
export function archiveItineraryApi(id) {
  return request.put(`/itinerary/${id}/archive`)
}

/**
 * 预订行程
 * @param {Object} data - 预订信息
 * @returns {Promise<Object>} 预订结果
 */
export function bookItineraryApi(data) {
  return request.post('/itinerary/booking', data)
}

/**
 * 导出行程为 PDF（返回文件流）
 * @param {string} id - 行程 ID
 * @returns {Promise<Blob>} PDF 文件 Blob
 */
export function exportItineraryPdfApi(id) {
  return request.get(`/itinerary/export/${id}`, {
    responseType: 'blob'
  })
}
