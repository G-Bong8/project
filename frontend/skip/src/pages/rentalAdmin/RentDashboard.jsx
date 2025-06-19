import { useState, useEffect } from "react";
import { useSelector } from "react-redux";
import DateInput from "../../components/adminpage/DateInput";
import SalesSummaryChart from "../../components/adminpage/SalesSummaryChart";
import SalesCategoryChart from "../../components/adminpage/SalesCategoryChart";
import StatOverviewCard from "../../components/adminpage/StatOverviewCard";
import { fetchRentSummary, fetchRentChart } from "../../services/admin/rentDashboardService";
import { findRentByUserId } from "../../services/admin/RentListService";
import Odometer from "react-odometerjs";
import "odometer/themes/odometer-theme-default.css";

const RentDashboard = () => {
  const { userId } = useSelector(state => state.loginSlice);
  const getToday = () => new Date().toISOString().split("T")[0];
  const getWeekAgo = () => {
    const d = new Date();
    d.setDate(d.getDate() - 7);
    return d.toISOString().split("T")[0];
  };

  const [startDate, setStartDate] = useState(getWeekAgo());
  const [endDate, setEndDate] = useState(getToday());
  const [summaryData, setSummaryData] = useState({});
  const [isClicked, setIsClicked] = useState(false);
  const [mounted, setMounted] = useState(false);
  const [chartData, setChartData] = useState([]);
  const [rentList, setRentList] = useState([]);
  const [selectedRentId, setSelectedRentId] = useState(0);

  useEffect(() => {
    if (!userId) return;
    findRentByUserId(userId).then(list => {
      setRentList(list);
      if (list.length > 0) setSelectedRentId(list[0].rentId);
    });
  }, [userId]);

  useEffect(() => {
    if (!userId) return;
    loadData();
  }, [userId, selectedRentId, startDate, endDate]);

  useEffect(() => {
    setMounted(true);
  }, []);

  const loadData = async () => {
    const summary = await fetchRentSummary(userId, selectedRentId, startDate, endDate);
    setSummaryData(summary);
    const chart = await fetchRentChart(userId, selectedRentId, startDate, endDate);
    setChartData(chart);
  };

  const handleClick = (type) => {
    setIsClicked(type);
    setTimeout(() => setIsClicked(null), 150);
    const extension = type === ".xlsx" || type === ".cell" ? type : "";
    window.location.href = `/api/rentAdmin/summary/export?userId=${userId}&rentId=${selectedRentId}&atStart=${startDate}&atEnd=${endDate}&extension=${extension}`;
  };
  

  return (
    <div className="admin-dashboard" style={{ paddingTop:"0px", backgroundColor: "#f1f3f5" }}>
      <h2>📊 렌탈샵 매출 관리</h2>
      <select value={selectedRentId} onChange={e => setSelectedRentId(Number(e.target.value))} style={{marginTop:"10px"}}>
        {rentList.map(r => (
          <option key={r.rentId} value={r.rentId}>{r.name}</option>
        ))}
      </select>
      <div style={{ display: "flex", marginTop: "10px" }}>
        <div className="date-card">
          <h3>📅 조회일자 선택</h3>
          <DateInput startDate={startDate} setStartDate={setStartDate} endDate={endDate} setEndDate={setEndDate} />
        </div>
        <div className="card-sales">
          <h3>💰 총 매출</h3>
          <div className="odometer">
            <Odometer value={mounted ? summaryData.totalSales || 0 : 0} format="(,ddd)" duration={500} /> 원
          </div>
        </div>
        <div className="card-sales" style={{width:"150px"}}>
          <h3>💸 순 이익</h3>
          <div className="odometer">
            <Odometer value={mounted ? summaryData.totalProfit || 0 : 0} format="(,ddd)" duration={500} /> 원
          </div>
        </div>
        <div className="card">
          <h3>✅ 결제 완료</h3>
          <div className="odometer">
            <Odometer value={mounted ? summaryData.totalSuccessCount || 0 : 0} duration={500} /> 건
          </div>
        </div>
        <div className="card">
          <h3>❌ 결제 취소</h3>
          <div className="odometer">
            <Odometer value={mounted ? summaryData.totalCancelCount || 0 : 0} duration={500} /> 건
          </div>
        </div>
        <div className="card-sales" style={{width:"150px"}}>
          <h3>📣 잔여 광고 캐시</h3>
          <div className="odometer">
            <Odometer value={mounted ? summaryData.totalAdCash || 0 : 0} format="(,ddd)" duration={500} /> 원
          </div>
        </div>
        <div className="card">
          <h3>📦 예약 건수</h3>
          <div className="odometer">
            <Odometer value={mounted ? summaryData.reservationCount || 0 : 0} duration={500} /> 건
          </div>
        </div>
        <div className="card-excel">
          <div style={{display:"flex",marginBottom:"10px", cursor: "pointer", backgroundColor: isClicked==="xlsx" ? "#ccc" : "transparent",transition: "background-color 0.2s ease", borderRadius:"4px"}} onClick={()=>{handleClick(".xlsx")}}>
                <img src="/public/images/icons8-msExcel-48.png" style={{width:"10%",height:"10%"}}/>
                <h6>&nbsp;&nbsp;엑셀 파일로 내려받기 (.xlsx)</h6>
            </div>
            <div style={{display:"flex", cursor: "pointer", backgroundColor: isClicked==="cell" ? "#ccc" : "transparent", transition: "background-color 0.2s ease", borderRadius:"4px"}} onClick={()=>{handleClick(".cell")}}>
                <img src="/public/images/icon-hancell.png" style={{width:"10%",height:"9%"}}/>
                <h6>&nbsp;&nbsp;한셀 파일로 내려받기 (.cell)</h6>
            </div>
        </div>
      </div>
      <div style={{ display: "flex", alignItems: "flex-start", gap: "20px" }}>
        <SalesSummaryChart summaryData={summaryData} />
        <SalesCategoryChart categoryData={chartData} />
        <StatOverviewCard stats={{ current: summaryData }} />
      </div>
    </div>
  );
};

export default RentDashboard;