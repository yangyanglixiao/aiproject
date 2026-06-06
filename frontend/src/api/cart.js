import request from './request'
export const getCartList = () => request.get('/api/cart')
export const addToCart = (data) => request.post('/api/cart/add', data)
export const updateCartItem = (id, data) => request.put(`/api/cart/item/${id}`, data)
export const removeCartItem = (id) => request.delete(`/api/cart/item/${id}`)
