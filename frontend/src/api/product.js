import request from './request'
export const getProducts = (merchantId, params) => request.get(`/api/merchants/${merchantId}/products`, { params })
export const getCategories = (merchantId) => request.get(`/api/merchants/${merchantId}/categories`)
