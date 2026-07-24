import request from '../utils/request'

/**
 * ========== 后台管理接口 ==========
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
 *
 * 分页接口统一返回：
 * {
 *   list: [...],          // 当页数据数组
 *   total: number         // 符合条件的总条数
 * }
 */

/* ========== 用户管理 ========== */

/**
 * 分页查询用户列表
 *
 * GET /user/list
 *
 * 请求参数 (query string)：
 *   - pageNum  {number}  必填，当前页码，从 1 开始
 *   - pageSize {number}  必填，每页条数
 *   - username {string}  可选，按用户名模糊搜索
 *
 * 后端返回 data 结构：
 * {
 *   list: [
 *     {
 *       userId:     number,   // 用户ID
 *       username:   string,   // 用户名
 *       status:     number,   // 状态：1=正常, 0=禁用
 *       createTime: string    // 创建时间，如 "2025-01-01 10:00:00"
 *     }
 *   ],
 *   total: number
 * }
 */
export function adminUserListApi(params) {
  return request.get('/user/list', { params })
}

/**
 * 修改用户启用/禁用状态
 *
 * POST /user/updateStatus  (Content-Type: application/json)
 *
 * 请求体：
 * {
 *   userId: number,   // 用户ID
 *   status: number    // 目标状态：1=启用, 0=禁用
 * }
 *
 * 后端返回 data：null 或 {}
 */
export function updateUserStatusApi(data) {
  return request.post('/user/updateStatus', data)
}

/* ========== 景点管理 ========== */

/**
 * 分页查询景点
 *
 * GET /attraction/list
 *
 * 请求参数 (query string)：
 *   - pageNum  {number}  必填，当前页码
 *   - pageSize {number}  必填，每页条数
 *
 * 后端返回 data 结构：
 * {
 *   list: [
 *     {
 *       attractionId: number,   // 景点ID
 *       name:         string,   // 景点名称
 *       type:         string,   // 景点类型
 *       price:        number,   // 票价（元），0 表示免费
 *       address:      string,   // 地址
 *       openTime:     string,   // 开放时间
 *       description:  string,   // 景点介绍
 *       longitude:    number,   // 经度（可选）
 *       latitude:     number    // 纬度（可选）
 *     }
 *   ],
 *   total: number
 * }
 */
export function adminAttractionListApi(params) {
  return request.get('/attraction/list', { params })
}

/**
 * 新增景点
 *
 * POST /attraction/add  (Content-Type: application/json)
 *
 * 请求体：
 * {
 *   name:        string,   // 必填，景点名称
 *   type:        string,   // 必填，景点类型
 *   price:       number,   // 可选，票价
 *   openTime:    string,   // 可选，开放时间
 *   address:     string,   // 可选，地址
 *   description: string,   // 可选，介绍
 *   longitude:   number,   // 可选，经度
 *   latitude:    number    // 可选，纬度
 * }
 *
 * 后端返回 data：null 或 { attractionId: number }
 */
export function addAttractionApi(data) {
  return request.post('/attraction/add', data)
}

/**
 * 编辑景点
 *
 * POST /attraction/update  (Content-Type: application/json)
 *
 * 请求体：同新增，但必须包含 attractionId
 * {
 *   attractionId: number,   // 必填，景点ID
 *   ...                     // 其余字段同新增
 * }
 *
 * 后端返回 data：null 或 {}
 */
export function updateAttractionApi(data) {
  return request.post('/attraction/update', data)
}

/**
 * 删除景点
 *
 * DELETE /attraction/delete
 *
 * 请求参数 (query string)：
 *   - attractionId {number}  必填，景点ID
 *
 * 后端返回 data：null 或 {}
 */
export function deleteAttractionApi(params) {
  return request.delete('/attraction/delete', { params })
}

/* ========== 酒店管理 ========== */

/**
 * 分页查询酒店
 *
 * GET /hotel/list
 *
 * 请求参数 (query string)：
 *   - pageNum  {number}  必填，当前页码
 *   - pageSize {number}  必填，每页条数
 *
 * 后端返回 data 结构：
 * {
 *   list: [
 *     {
 *       hotelId:     number,   // 酒店ID
 *       name:        string,   // 酒店名称
 *       star:        number,   // 星级（2/3/4/5）
 *       price:       number,   // 房价（元/晚）
 *       remain:      number,   // 剩余房源数
 *       city:        string,   // 所在城市
 *       address:     string,   // 详细地址
 *       description: string    // 酒店介绍
 *     }
 *   ],
 *   total: number
 * }
 */
export function adminHotelListApi(params) {
  return request.get('/hotel/list', { params })
}

/**
 * 新增酒店
 *
 * POST /hotel/add  (Content-Type: application/json)
 *
 * 请求体：
 * {
 *   name:        string,   // 必填，酒店名称
 *   star:        number,   // 必填，星级
 *   price:       number,   // 必填，房价
 *   remain:      number,   // 可选，剩余房源
 *   city:        string,   // 可选，城市
 *   address:     string,   // 可选，地址
 *   description: string    // 可选，介绍
 * }
 *
 * 后端返回 data：null 或 { hotelId: number }
 */
export function addHotelApi(data) {
  return request.post('/hotel/add', data)
}

/**
 * 编辑酒店
 *
 * POST /hotel/update  (Content-Type: application/json)
 *
 * 请求体：同新增，但必须包含 hotelId
 * {
 *   hotelId: number,   // 必填，酒店ID
 *   ...                // 其余字段同新增
 * }
 *
 * 后端返回 data：null 或 {}
 */
export function updateHotelApi(data) {
  return request.post('/hotel/update', data)
}

/**
 * 删除酒店
 *
 * DELETE /hotel/delete
 *
 * 请求参数 (query string)：
 *   - hotelId {number}  必填，酒店ID
 *
 * 后端返回 data：null 或 {}
 */
export function deleteHotelApi(params) {
  return request.delete('/hotel/delete', { params })
}

/* ========== 交通管理 ========== */

/**
 * 查询交通线路（管理端，支持分页）
 *
 * GET /traffic/list
 *
 * 请求参数 (query string)：
 *   - pageNum  {number}  必填，当前页码
 *   - pageSize {number}  必填，每页条数
 *
 * 后端返回 data 结构：
 * {
 *   list: [
 *     {
 *       id:        number,   // 记录ID
 *       fromCity:  string,   // 出发城市
 *       toCity:    string,   // 目的城市
 *       type:      string,   // 交通方式：高铁/飞机/大巴/轮船
 *       price:     number,   // 费用（元）
 *       duration:  string    // 行程时长，如 "4h 30m"
 *     }
 *   ],
 *   total: number
 * }
 */
export function adminTrafficListApi(params) {
  return request.get('/traffic/list', { params })
}

/**
 * 新增交通线路
 *
 * POST /traffic/add  (Content-Type: application/json)
 *
 * 请求体：
 * {
 *   fromCity: string,   // 必填，出发城市
 *   toCity:   string,   // 必填，目的城市
 *   type:     string,   // 必填，交通方式
 *   price:    number,   // 必填，费用
 *   duration: string    // 必填，时长
 * }
 *
 * 后端返回 data：null 或 { id: number }
 */
export function addTrafficApi(data) {
  return request.post('/traffic/add', data)
}

/* ========== 数据统计 ========== */

/**
 * 热门景点统计
 *
 * GET /stats/hotAttraction
 *
 * 无需请求参数。
 *
 * 后端返回 data 结构（数组）：
 * [
 *   { name: string, heat: number },   // 景点名称 + 热度值
 *   ...
 * ]
 */
export function hotAttractionStatsApi() {
  return request.get('/stats/hotAttraction')
}

/**
 * 热门城市统计
 *
 * GET /stats/hotCity
 *
 * 无需请求参数。
 *
 * 后端返回 data 结构（数组）：
 * [
 *   { name: string, count: number },   // 城市名称 + 访问量
 *   ...
 * ]
 */
export function hotCityStatsApi() {
  return request.get('/stats/hotCity')
}

/**
 * 城市酒店选择占比统计
 *
 * GET /stats/hotelSelectionRatio
 *
 * 请求参数 (query string)：
 *   - city {string}  必填，城市名称
 *
 * 后端返回 data 结构（数组）：
 * [
 *   { name: string, value: number },   // 酒店类型名称 + 占比百分比
 *   ...
 * ]
 *
 * 示例返回：
 * [
 *   { name: '五星酒店', value: 35 },
 *   { name: '四星酒店', value: 28 },
 *   { name: '三星酒店', value: 22 },
 *   { name: '快捷酒店', value: 15 }
 * ]
 */
export function hotelSelectionRatioApi(params) {
  return request.get('/stats/hotelSelectionRatio', { params })
}

/**
 * 城市景点选择占比统计
 *
 * GET /stats/attractionSelectionRatio
 *
 * 请求参数 (query string)：
 *   - city {string}  必填，城市名称
 *
 * 后端返回 data 结构（数组）：
 * [
 *   { name: string, value: number },   // 景点类型名称 + 占比百分比
 *   ...
 * ]
 *
 * 示例返回：
 * [
 *   { name: '历史古迹', value: 45 },
 *   { name: '自然风光', value: 20 },
 *   { name: '主题乐园', value: 18 },
 *   { name: '文化体验', value: 17 }
 * ]
 */
export function attractionSelectionRatioApi(params) {
  return request.get('/stats/attractionSelectionRatio', { params })
}

/**
 * 热门城市访问量趋势统计
 *
 * GET /stats/cityTrend
 *
 * 无需请求参数。
 *
 * 后端返回 data 结构：
 * {
 *   months: [string],                   // 月份数组，如 ['1月', '2月', '3月', ...]
 *   cities: [
 *     {
 *       name: string,                   // 城市名称
 *       data: [number]                  // 各月访问量百分比数组，与months对应
 *     }
 *   ]
 * }
 *
 * 示例返回：
 * {
 *   months: ['1月', '2月', '3月', '4月', '5月', '6月', '7月'],
 *   cities: [
 *     { name: '北京', data: [35, 42, 58, 72, 85, 92, 88] },
 *     { name: '上海', data: [38, 45, 62, 75, 88, 95, 90] },
 *     ...
 *   ]
 * }
 */
export function cityTrendApi() {
  return request.get('/stats/cityTrend')
}

/* ========== 操作日志 ========== */

/**
 * 分页查询操作日志
 *
 * GET /log/list
 *
 * 请求参数 (query string)：
 *   - pageNum      {number}  必填，当前页码
 *   - pageSize     {number}  必填，每页条数
 *   - operatorType {string}  可选，操作类型筛选：add | update | delete
 *
 * 后端返回 data 结构：
 * {
 *   list: [
 *     {
 *       logId:        number,   // 日志ID
 *       operatorType: string,   // 操作类型：add/update/delete
 *       content:      string,   // 操作内容描述
 *       operator:     string,   // 操作人用户名
 *       createTime:   string    // 操作时间
 *     }
 *   ],
 *   total: number
 * }
 */
export function logListApi(params) {
  return request.get('/log/list', { params })
}

/* ========== 意见反馈 ========== */

/**
 * 提交意见反馈
 *
 * POST /feedback/submit  (Content-Type: application/json)
 *
 * 请求体：
 * {
 *   userId:   number,   // 可选，用户ID（已登录用户自动携带）
 *   content:  string,   // 必填，反馈内容
 *   username: string    // 可选，用户名（未登录用户可填写）
 * }
 *
 * 后端返回 data：null 或 {}
 */
export function submitFeedbackApi(data) {
  return request.post('/feedback/submit', data)
}

/**
 * 分页查询意见反馈列表（管理员端）
 *
 * GET /feedback/list
 *
 * 请求参数 (query string)：
 *   - pageNum  {number}  必填，当前页码
 *   - pageSize {number}  必填，每页条数
 *
 * 后端返回 data 结构：
 * {
 *   list: [
 *     {
 *       feedbackId: number,   // 反馈ID
 *       userId:     number,   // 用户ID（可能为0表示未登录）
 *       username:   string,   // 用户名
 *       content:    string,   // 反馈内容
 *       status:     number,   // 状态：0=待处理, 1=已处理
 *       createTime: string    // 提交时间
 *     }
 *   ],
 *   total: number
 * }
 */
export function feedbackListApi(params) {
  return request.get('/feedback/list', { params })
}

/**
 * 修改反馈状态（标记为已处理）
 *
 * POST /feedback/updateStatus  (Content-Type: application/json)
 *
 * 请求体：
 * {
 *   feedbackId: number,   // 反馈ID
 *   status:     number    // 目标状态：0=待处理, 1=已处理
 * }
 *
 * 后端返回 data：null 或 {}
 */
export function updateFeedbackStatusApi(data) {
  return request.post('/feedback/updateStatus', data)
}
