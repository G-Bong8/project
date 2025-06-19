import { useEffect, useState } from 'react';
import '../../css/rentAdForm.css';
import '../../css/userlist.css';
import { fetchCash, chargeCash } from '../../services/admin/rent/AdService';
import { useSelector } from 'react-redux';
import { findRentByUserId } from '../../services/admin/RentListService';

const CashChargeForm = () => {
  const { userId } = useSelector(state => state.loginSlice);
  const [currentCash, setCurrentCash] = useState(0);
  const [amount, setAmount] = useState('');
  const [pg, setPg] = useState('kakaopay.TC0ONETIME');
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
    const load = async () => {
      if (!userId || !selectedRentId) return;
      const cash = await fetchCash(userId, selectedRentId);
      setCurrentCash(cash);
    };
    load();
  }, [userId, selectedRentId]);

  useEffect(() => {
    if (window.IMP) {
      window.IMP.init(import.meta.env.VITE_IAMPORT_API_KEY);
    }
  }, []);

  const handleCharge = async e => {
    e.preventDefault();
    const merchantUid = `adcash_${new Date().getTime()}`;
    const { IMP } = window;
    if (!IMP) {
      alert('결제 모듈 로딩 실패');
      return;
    }
    IMP.request_pay({
      pg,
      pay_method: 'card',
      merchant_uid: merchantUid,
      name: '광고 캐시 충전',
      amount: Number(amount),
    }, async rsp => {
      if (rsp.success) {
        await chargeCash({
          impUid: rsp.imp_uid,
          merchantUid: rsp.merchant_uid,
          amount: rsp.paid_amount,
          userId,
          rentId: selectedRentId,
          pgProvider: pg,
        });
        const cash = await fetchCash(userId, selectedRentId);
        setCurrentCash(cash);
      } else {
        alert('결제 실패: ' + rsp.error_msg);
      }
    });
    setAmount('');
  };

  const getNextMonday0AM = () => {
    const today = new Date();
    const diff = (8 - today.getDay()) % 7 || 7;
    const monday = new Date(today);
    monday.setDate(today.getDate() + diff);
    monday.setHours(0, 0, 0, 0);
    return monday.toISOString().split('T')[0] + ' 00:00';
  };
  const updateDay = getNextMonday0AM();

  return (
    <div className="table-container" style={{marginLeft:"20px",marginTop:"0px", width:"500px", boxShadow:"0 2px 6px rgba(0, 0, 0, 0.3)"}}>
      <h3 className="form-header">
        💰 광고 캐시 충전
        <span style={{ fontSize: '14px', marginLeft: '10px', color: '#555' }}>
          📅 {updateDay}(월) 적용 예정
        </span>
      </h3>
      <div className="form-container">
        <form onSubmit={handleCharge}>
          <div className="form-group">
            <label>렌탈샵 선택</label>
            <select value={selectedRentId} onChange={e => setSelectedRentId(Number(e.target.value))}>
              {rentList.map(r => (
                <option key={r.rentId} value={r.rentId}>{r.name}</option>
              ))}
            </select>
          </div>
          <div className="form-group">
            <label>현재 보유 캐시</label>
            <input type="text" value={currentCash} readOnly />
          </div>
          <div className="form-group">
            <label>충전 금액</label>
            <input
              type="number"
              value={amount}
              onChange={e => setAmount(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label>결제 수단</label>
            <select value={pg} onChange={e => setPg(e.target.value)}>
              <option value="kakaopay.TC0ONETIME">카카오페이</option>
              <option value="tosspay.tosstest">토스페이</option>
            </select>
          </div>
          <button type="submit" style={{marginLeft:"260px" , marginTop:"20px"}}>결제하기</button>
        </form>
      </div>
    </div>
  );
};

export default CashChargeForm;