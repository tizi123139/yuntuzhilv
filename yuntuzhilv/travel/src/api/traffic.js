import request from '../utils/request'

/**
 * ========== 交通查询接口 ==========
 *
 * 【后端对接说明】
 * 统一响应格式见 attraction.js 顶部注释。
 */

/**
 * 交通方案列表（按起止城市查询）
 *
 * GET /traffic/list
 *
 * 请求参数 (query string)：
 *   - fromCity {string}  必填，出发城市，如 "北京"
 *   - toCity   {string}  必填，到达城市，如 "上海"
 *
 * 后端返回 data 结构：
 * {
 *   list: [              // 交通方案数组
 *     {
 *       id:       number,   // 记录ID（预留扩展）
 *       type:     string,   // 交通方式，如 "高铁" | "飞机" | "大巴"
 *       code:     string,   // 班次编号，如 "G1288" | "MU5101"
 *       fromCity: string,   // 出发城市
 *       toCity:   string,   // 到达城市
 *       time:     string,   // 行程耗时，如 "4h 32m"
 *       price:    number,   // 票价（元）
 *       departTime: string, // 出发时间，如 "08:00"（预留扩展）
 *       carrier:  string    // 承运方，如 "中国铁路" | "东方航空"（预留扩展）
 *     }
 *   ],
 *   total: number          // 总条数（预留分页）
 * }
 */
export function trafficListApi(params) {
  return request.get('/traffic/list', { params })
}
