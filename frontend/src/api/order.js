import request from './request'
export const createOrder = (data) => request.post('/api/orders', data)
export const getOrderList = (params) => request.get('/api/orders', { params })
export const getOrderDetail = (id) => request.get(`/api/orders/${id}`)
export const getOrderItems = (id) => request.get(`/api/orders/${id}/items`)
export const cancelOrder = (id) => request.post(`/api/orders/${id}/cancel`)
