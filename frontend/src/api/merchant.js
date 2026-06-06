import request from './request'
export const getMerchantList = (params) => request.get('/api/merchants', { params })
export const getMerchantDetail = (id) => request.get(`/api/merchants/${id}`)
