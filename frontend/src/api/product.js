import request from './request'
export const getMerchantDetail = (merchantId) => request.get(`/api/merchants/${merchantId}`)
export const getProducts = (merchantId, params) => request.get(`/api/merchants/${merchantId}/products`, { params })
export const getCategories = (merchantId) => request.get(`/api/merchants/${merchantId}/categories`)
export const adminCreateCategory = (data) => request.post('/api/admin/categories', data)
export const adminDeleteCategory = (id) => request.delete(`/api/admin/categories/${id}`)
