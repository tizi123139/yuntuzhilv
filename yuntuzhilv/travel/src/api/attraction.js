import request from '../utils/request'

/**
 * ========== 景点浏览接口 ==========
 *
 * 【后端对接说明】
 * 所有接口统一前缀 /api，由 request.js 中 baseURL 配置。
 * 请求头自动携带 token（由请求拦截器注入）。
 *
 * 统一响应格式：
 * {
 *   code: 200,
 *   message: "success",
 *   data: { ... }        // 具体结构见各接口注释
 * }
 */

/**
 * 景点分页列表（支持城市/类型筛选）
 *
 * GET /attraction/list
 *
 * 请求参数 (query string)：
 *   - city     {string}  可选，城市名模糊搜索，如 "杭州"
 *   - type     {string}  可选，景点类型精确匹配，如 "历史古迹" | "自然风光" | "美食街区"
 *   - pageNum  {number}  必填，当前页码，从 1 开始
 *   - pageSize {number}  必填，每页条数
 *
 * 后端返回 data 结构：
 * {
 *   list: [              // 当页景点数组
 *     {
 *       id:        number,   // 景点ID
 *       name:      string,   // 景点名称
 *       city:      string,   // 所在城市
 *       type:      string,   // 景点类型
 *       desc:      string,   // 景点简介
 *       price:     string,   // 门票价格描述，如 "¥60" 或 "免费"
 *       rating:    string,   // 评分，如 "4.8"
 *       address:   string,   // 详细地址
 *       openTime:  string    // 开放时间，如 "08:30-17:00"
 *     }
 *   ],
 *   total: number          // 符合条件的总条数（用于前端分页）
 * }
 */
export function attractionListApi(params) {
  return request.get('/attraction/list', { params })
}

/**
 * 景点详情
 *
 * GET /attraction/detail
 *
 * 请求参数 (query string)：
 *   - id {number}  必填，景点ID
 *
 * 后端返回 data 结构：
 * {
 *   id:        number,
 *   name:      string,
 *   city:      string,
 *   type:      string,
 *   desc:      string,
 *   price:     string,
 *   rating:    string,
 *   address:   string,
 *   openTime:  string,
 *   images:    string[]   // 可选，景点图片URL数组（预留扩展）
 * }
 */
export function attractionDetailApi(id) {
  return request.get('/attraction/detail', { params: { id } })
}