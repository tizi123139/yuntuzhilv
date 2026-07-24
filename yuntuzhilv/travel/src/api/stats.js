import request from '../utils/request'

// 热门景点统计
export function getHotAttractionsApi(silent = true) {
  return request.get('/stats/hotAttractions', { silent })
}

// 热门城市统计
export function getHotCitiesApi(silent = true) {
  return request.get('/stats/hotCities', { silent })
}
