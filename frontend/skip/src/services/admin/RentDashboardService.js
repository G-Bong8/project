import axios from "axios";
import caxios from "../../api/caxios";

export const fetchRentSummary = async (userId, rentId, startDate, endDate) => {
  const response = await caxios.get(`/api/rentAdmin/summary`, {
    params: { userId, rentId, startDate, endDate },
  });
  return response.data;
};

export const fetchRentChart = async (userId, rentId, start, end) => {
  const response = await caxios.get(`/api/rentAdmin/sales/chart`, {
    params: { userId, rentId, start, end },
  });
  return response.data;
};

