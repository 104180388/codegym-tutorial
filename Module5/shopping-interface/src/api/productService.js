import axiosClient from './axiosClient';
import { fallbackProducts } from '../data/fallbackProducts';

export const productService = {
  // Lấy danh sách toàn bộ sản phẩm
  getAllProducts: async () => {
    try {
      const data = await axiosClient.get('/products');
      if (Array.isArray(data) && data.length > 0) {
        return data;
      }
      return fallbackProducts;
    } catch {
      console.info('Using fallback products data (JSON Server might be paused or starting)');
      return fallbackProducts;
    }
  },

  // Lấy chi tiết sản phẩm theo ID
  getProductById: async (id) => {
    try {
      const data = await axiosClient.get(`/products/${id}`);
      if (data && data.id) {
        return data;
      }
      const found = fallbackProducts.find((p) => String(p.id) === String(id));
      return found || null;
    } catch {
      const found = fallbackProducts.find((p) => String(p.id) === String(id));
      return found || null;
    }
  },

  // Tạo đơn hàng mới (Mock thanh toán)
  createOrder: async (orderData) => {
    try {
      const result = await axiosClient.post('/orders', orderData);
      return result;
    } catch {
      // Giả lập lưu đơn hàng thành công khi json-server offline
      console.info('Mock order placed successfully (offline mode fallback)');
      return {
        ...orderData,
        id: `ORD-${Date.now()}`,
        status: 'SUCCESS',
      };
    }
  },
};
