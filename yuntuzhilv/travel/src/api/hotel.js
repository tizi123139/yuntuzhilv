import request from '../utils/request'

/**
 * ========== 酒店浏览接口 ==========
 *
 * 【后端对接说明】
 * 统一响应格式见 attraction.js 顶部注释。
 */

/**
 * 酒店分页列表（支持星级/价格筛选）
 *
 * GET /hotel/list
 *
 * 请求参数 (query string)：
 *   - star     {number}  可选，星级筛选，如 3 | 4 | 5
 *   - maxPrice {number}  可选，最高价格（元/晚）
 *   - pageNum  {number}  必填，当前页码，从 1 开始
 *   - pageSize {number}  必填，每页条数
 *
 * 后端返回 data 结构：
 * {
 *   list: [              // 当页酒店数组
 *     {
 *       id:    number,   // 酒店ID
 *       name:  string,   // 酒店名称
 *       star:  number,   // 星级（3/4/5）
 *       price: number,   // 每晚价格（元）
 *       desc:  string,   // 酒店简介
 *       address: string, // 详细地址（预留扩展）
 *       images: string[] // 酒店图片URL数组（预留扩展）
 *     }
 *   ],
 *   total: number          // 符合条件的总条数
 * }
 */
export function hotelListApi(params) {
  return request.get('/hotel/list', { params })
}

/**
 * 酒店详情
 *
 * GET /hotel/detail
 *
 * 请求参数 (query string)：
 *   - id {number}  必填，酒店ID
 *
 * 后端返回 data 结构：
 * {
 *   id:      number,
 *   name:    string,
 *   star:    number,
 *   price:   number,
 *   desc:    string,
 *   address: string,
 *   phone:   string,    // 联系电话（预留扩展）
 *   images:  string[],  // 图片URL数组（预留扩展）
 *   facilities: string[] // 设施列表，如 ["WiFi", "停车场"]（预留扩展）
 * }
 */
export function hotelDetailApi(id) {
  return request.get('/hotel/detail', { params: { id } })
}
