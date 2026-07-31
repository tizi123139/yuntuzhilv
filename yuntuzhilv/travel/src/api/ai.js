import request from '../utils/request'

/**
 * AI 生成行程
 * @param {Object} data - 生成参数
 * @param {string} data.departureCity - 出发地
 * @param {string} data.destinationCity - 目的地
 * @param {number} data.days - 游玩天数
 * @param {number} data.budget - 人均预算（元）
 * @param {string[]} data.interests - 兴趣标签
 * @param {number} data.people - 人数
 * @returns {Promise<Object>} 生成的行程
 */
export function generateItineraryApi(data) {
  return request.post('/ai/itinerary/generate', data)
}

/**
 * AI 修改行程
 * @param {string} itineraryId - 行程 ID
 * @param {Object} data - 修改参数
 * @param {string} data.modifications - 修改内容描述
 * @returns {Promise<Object>} 修改结果
 */
export function modifyItineraryApi(itineraryId, data) {
  return request.post(`/ai/modify/${itineraryId}`, data)
}
