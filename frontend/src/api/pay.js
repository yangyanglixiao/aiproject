import request from './request'
export const mockPay = (orderId) => request.post(`/api/pay/mock/${orderId}`)
