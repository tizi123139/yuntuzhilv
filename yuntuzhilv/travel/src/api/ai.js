import request from '../utils/request'

/**
 * AI 生成行程
 * @param {Object} data - 生成参数
 * @param {string} data.fromCity - 出发地，如 "北京"
 * @param {string} data.toCity - 目的地，如 "成都"
 * @param {number} data.days - 游玩天数
 * @param {number} data.budget - 人均预算（元）
 * @param {string[]} data.interests - 兴趣标签，如 ["自然风光", "美食"]
 * @returns {Promise<Object>} 生成的行程
 * @returns {string} returns.id - 行程 ID
 * @returns {string} returns.title - 行程标题
 * @returns {number} returns.totalBudget - 总预算
 * @returns {string} returns.fromCity - 出发地
 * @returns {string} returns.toCity - 目的地
 * @returns {number} returns.days - 天数
 * @returns {string[]} returns.interests - 兴趣标签
 * @returns {Array<Object>} returns.timeline - 每日行程时间线
 * @returns {number} returns.timeline[].day - 第几天
 * @returns {string} returns.timeline[].morning - 上午安排
 * @returns {string} returns.timeline[].afternoon - 下午安排
 * @returns {string} returns.timeline[].dinner - 晚餐安排
 * @returns {string} returns.timeline[].hotel - 住宿安排
 */
export function generateItineraryApi(data) {
  return request.post('/ai/generateItinerary', data)
}

/**
 * AI 修改行程中某个时段的内容（"换一换"功能）
 * @param {Object} data - 修改参数
 * @param {string} data.itineraryId - 行程 ID
 * @param {number} data.day - 第几天
 * @param {string} data.slot - 时段 key：morning | afternoon | dinner | hotel
 * @param {string} data.current - 当前内容
 * @returns {Promise<Object>} 修改结果
 * @returns {string} returns.content - 替换后的新内容
 */
export function modifyItineraryApi(data) {
  return request.post('/ai/modifyItinerary', data)
}
