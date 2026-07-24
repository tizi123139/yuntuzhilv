import request from '../utils/request'

/**
 * 获取用户足迹列表（按城市分组）
 *
 * 请求方式: GET /footprint/list
 * 认证: 需要携带 token
 *
 * 响应 data:
 *   @returns {{
 *     totalCities: number,                      // 到访城市总数
 *     totalFootprints: number,                   // 足迹总数
 *     list: Array<{
 *       cityId: number,                          // 城市 ID
 *       cityName: string,                        // 城市名称，如 "北京"
 *       visitCount: number,                      // 到访次数
 *       firstVisitDate: string,                  // 首次到访日期，ISO 格式
 *       lastVisitDate: string,                   // 最近到访日期，ISO 格式
 *       attractions: Array<{
 *         attractionId: number,                  // 景点 ID
 *         name: string,                          // 景点名称
 *         image: string,                         // 景点封面图 URL
 *         visitDate: string,                     // 到访日期，ISO 格式
 *         rating: number,                        // 用户评分 1-5
 *         comment: string                        // 用户评语（可能为空）
 *       }>
 *     }>
 *   }}
 */
export function getFootprintListApi() {
  return request.get('/footprint/list')
}

/**
 * 添加一条足迹记录
 *
 * 请求方式: POST /footprint/add
 * 认证: 需要携带 token
 *
 * 请求参数（Body）:
 *   @param {number} attractionId - 必填，景点 ID
 *   @param {string} visitDate    - 必填，到访日期，格式 YYYY-MM-DD
 *   @param {number} rating       - 选填，评分 1-5
 *   @param {string} comment      - 选填，评语
 *
 * 响应 data:
 *   @returns {{
 *     footprintId: number,       // 新建足迹记录 ID
 *     message: string            // 提示信息
 *   }}
 */
export function addFootprintApi(attractionId, visitDate, rating, comment) {
  return request.post('/footprint/add', { attractionId, visitDate, rating, comment })
}

/**
 * 删除一条足迹记录
 *
 * 请求方式: POST /footprint/delete
 * 认证: 需要携带 token
 *
 * 请求参数（Body）:
 *   @param {number} footprintId - 必填，足迹记录 ID
 *
 * 响应 data:
 *   @returns {{ message: string }}
 */
export function deleteFootprintApi(footprintId) {
  return request.post('/footprint/delete', { footprintId })
}
