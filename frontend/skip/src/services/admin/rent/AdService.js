import axios from 'axios';

export const fetchCash = async (userId, rentId) => {
  const resp = await axios.get('/api/rentAdmin/cash', { params: { userId, rentId } });
  return resp.data.remainingCash;
};

export const chargeCash = async data => {
  const resp = await axios.post('/api/rentAdmin/cash', data);
  return resp.data.remainingCash;
};

export const purchaseBoost = async (userId, rentId, boost, cpb) => {
  const resp = await axios.post('/api/rentAdmin/boost', { userId, rentId, boost, cpb });
  return resp.data.remainingCash;
};

export const submitBannerRequest = async formData => {
  const resp = await axios.post('/api/rentAdmin/banner', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return resp.data;
};