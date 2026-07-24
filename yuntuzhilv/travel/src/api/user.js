import request from '../utils/request'

// 用户登录 POST /user/login
export function loginApi(data) {
  return request.post('/user/login', data)
}

// 用户注册 POST /user/register
export function registerApi(data) {
  return request.post('/user/register', data)
}

/**
 * 发送短信验证码（注册用）
 *
 * 请求参数（Body）:
 *   @param {string} phone - 必填，手机号（含国际区号，如 +8613800138000）
 *
 * 响应 data:
 *   @returns {{ message: string }}  提示信息，如 "验证码已发送"
 */
export function sendCodeApi(phone) {
  return request.post('/user/sendCode', phone)
}

/**
 * 获取当前登录用户的个人信息与统计摘要
 *
 * 请求方式: GET /user/getInfo
 * 认证: 需要携带 token（Authorization header）
 *
 * 响应 data:
 *   @returns {{
 *     userId: number,           // 用户 ID
 *     username: string,         // 用户名 / 昵称
 *     avatar: string,           // 头像 URL（可能为空字符串）
 *     level: string,            // 会员等级，如 "活跃旅行者"
 *     bio: string,              // 个人简介
 *     favoritesCount: number,   // 收藏景点数
 *     tripsCount: number,       // 行程总数
 *     points: number            // 当前积分
 *   }}
 */
export function getUserInfoApi() {
  return request.get('/user/getInfo')
}

// 更新用户基础信息
export function updateUserInfoApi(data) {
  return request.post('/user/update', data)
}

// 更新旅游偏好
export function updatePreferApi(data) {
  return request.post('/user/updatePrefer', data)
}

/* ========================================================
 * 社交登录 / 第三方登录
 * ======================================================== */

/**
 * 获取微信扫码登录授权页地址
 *
 * 请求参数（Query）:
 *   @param {string} redirectUrl - 必填，授权回调地址（前端当前页面 URL 或指定回调）
 *
 * 响应 data:
 *   @returns {{ authUrl: string }}  authUrl — 微信授权页完整 URL，前端跳转此地址即可
 *
 * 流程说明:
 *   1. 前端调用此接口拿到 authUrl
 *   2. 跳转至微信授权页，用户扫码确认
 *   3. 微信回调后端，后端拿到 code
 *   4. 后端用 code 换取 access_token / 用户信息，生成系统 token
 *   5. 后端将 token 写入回调 URL 的 query 或重定向到前端指定页面
 */
export function getWechatAuthUrlApi(redirectUrl) {
  return request.get('/user/social/wechat/url', { params: { redirectUrl } })
}

/**
 * 微信扫码登录回调处理（后端回调后前端携带 code 换取 token）
 *
 * 请求参数（Body）:
 *   @param {string} code - 必填，微信授权回调中携带的临时授权码
 *
 * 响应 data:
 *   @returns {{
 *     token: string,        // 登录凭证，前端存入 localStorage
 *     userId: number,       // 用户 ID
 *     username: string,     // 用户名（微信昵称）
 *     avatar: string,       // 头像 URL
 *     role: string          // 角色，默认 'user'
 *   }}
 */
export function wechatLoginApi(code) {
  return request.post('/user/social/wechat', { code })
}

/**
 * 获取 QQ 登录授权页地址
 *
 * 请求参数（Query）:
 *   @param {string} redirectUrl - 必填，授权回调地址
 *
 * 响应 data:
 *   @returns {{ authUrl: string }}  authUrl — QQ 授权页完整 URL
 *
 * 流程同微信登录。
 */
export function getQQAuthUrlApi(redirectUrl) {
  return request.get('/user/social/qq/url', { params: { redirectUrl } })
}

/**
 * QQ 登录回调处理（前端携带 code 换取 token）
 *
 * 请求参数（Body）:
 *   @param {string} code - 必填，QQ 授权回调中携带的临时授权码
 *
 * 响应 data:
 *   @returns {{
 *     token: string,        // 登录凭证
 *     userId: number,       // 用户 ID
 *     username: string,     // 用户名（QQ 昵称）
 *     avatar: string,       // 头像 URL
 *     role: string          // 角色，默认 'user'
 *   }}
 */
export function qqLoginApi(code) {
  return request.post('/user/social/qq', { code })
}

/**
 * 邮箱验证码登录 — 发送验证码到指定邮箱
 *
 * 请求参数（Body）:
 *   @param {string} email - 必填，目标邮箱地址
 *
 * 响应 data:
 *   @returns {{ message: string }}  提示信息，如 "验证码已发送"
 *
 * 流程说明:
 *   1. 前端调用此接口，后端向指定邮箱发送 6 位数字验证码
 *   2. 用户在弹窗中输入收到的验证码
 *   3. 前端调用 emailLoginApi 完成登录
 */
export function sendEmailCodeApi(email) {
  return request.post('/user/social/email-code', { email })
}

/**
 * 邮箱验证码登录 — 用邮箱 + 验证码换取 token
 *
 * 请求参数（Body）:
 *   @param {string} email    - 必填，邮箱地址（需与发送验证码时一致）
 *   @param {string} code     - 必填，6 位数字验证码
 *
 * 响应 data:
 *   @returns {{
 *     token: string,        // 登录凭证
 *     userId: number,       // 用户 ID
 *     username: string,     // 用户名
 *     email: string,        // 邮箱
 *     role: string          // 角色，默认 'user'
 *   }}
 */
export function emailLoginApi(email, code) {
  return request.post('/user/social/email-login', { email, code })
}