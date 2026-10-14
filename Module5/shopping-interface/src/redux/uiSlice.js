import { createSlice } from '@reduxjs/toolkit';

/**
 * UI Slice
 * Manages global UI feedback state:
 * - toast: global notification message (success / error / info)
 * - orderSuccessData: placed order details for the confirmation modal popup
 */
const uiSlice = createSlice({
  name: 'ui',
  initialState: {
    toast: null, // { message: string, type: 'success' | 'info' | 'error' }
    orderSuccessData: null, // Holds placed order details for the confirmation modal
  },
  reducers: {
    showToast: (state, action) => {
      state.toast = {
        message: action.payload.message || 'Thành công',
        type: action.payload.type || 'success',
      };
    },
    hideToast: (state) => {
      state.toast = null;
    },
    setOrderSuccess: (state, action) => {
      state.orderSuccessData = action.payload;
    },
    clearOrderSuccess: (state) => {
      state.orderSuccessData = null;
    },
  },
});

export const {
  showToast,
  hideToast,
  setOrderSuccess,
  clearOrderSuccess,
} = uiSlice.actions;

// Selectors
export const selectToast = (state) => state.ui.toast;
export const selectOrderSuccessData = (state) => state.ui.orderSuccessData;

export default uiSlice.reducer;
