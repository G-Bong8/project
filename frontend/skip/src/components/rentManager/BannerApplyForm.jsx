import { useEffect, useRef, useState } from 'react';
import '../../css/rentAdForm.css';
import '../../css/userlist.css';
import { fetchActiveBanners } from '../../services/admin/BannerService';
import { submitBannerRequest } from '../../services/admin/rent/AdService';
import { useSelector } from 'react-redux';
import { findRentByUserId } from '../../services/admin/RentListService';

const BannerApplyForm = () => {
  const { userId } = useSelector(state => state.loginSlice);
  const [maxBid, setMaxBid] = useState(0);
  const [cpcBid, setCpcBid] = useState('');
  const [avgRating, setAvgRating] = useState(2.5);
  const [recentRating, setRecentRating] = useState(2.5);
  const [finalScore, setFinalScore] = useState(0);
  const [percentile, setPercentile] = useState(0);
  const [activeScores, setActiveScores] = useState([]);
  const [selectedFileName, setSelectedFileName] = useState('');
  const imageRef = useRef();
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
      const actives = await fetchActiveBanners();
      const bids = actives.map(b => b.cpcBid);
      setMaxBid(bids.length > 0 ? Math.max(...bids) : 0);
      setActiveScores(actives.map(b => Number(b.finalScore)));
    };
    load();
  }, []);

  useEffect(() => {
    const max = maxBid > 0 ? maxBid : 1;
    const normalized = Number(cpcBid) / max;
    const score = avgRating * 0.2 + normalized * 0.3 + recentRating * 0.5;
    const final = Number.isNaN(score) ? 0 : Number(score.toFixed(2));
    setFinalScore(final);

    const scores = [...activeScores, final];
    scores.sort((a, b) => b - a); // 내림차순

    const betterCount = scores.filter(s => s > final).length;
    let perc = ((scores.length - betterCount) / scores.length) * 100;

    perc = Math.round(perc / 10) * 10; // 10% 단위 표현용
    if (perc > 100) perc = 100;    
    perc = 100 - perc;
    if (perc < 0) perc = 1;

    setPercentile(perc);
  }, [cpcBid, maxBid, avgRating, recentRating, activeScores]);


  const handleFileChange = (event) => {
    if (event.target.files[0]) {
        setSelectedFileName(event.target.files[0].name);
    }
  };

  const handleSubmit = async e => {
    e.preventDefault();
    const form = new FormData();
    form.append('userId', userId);
    form.append('rentId', selectedRentId);
    form.append('cpcBid', cpcBid);
    if (imageRef.current?.files[0]) form.append('bannerImage', imageRef.current.files[0]);
    await submitBannerRequest(form);
    setCpcBid('');
    if (imageRef.current) imageRef.current.value = null;
  };

  const getNextMonday3AM = () => {
    const today = new Date();
    const day = today.getDay();
    const diff = (8 - day) % 7 || 7;
    const monday = new Date(today);
    monday.setDate(today.getDate() + diff);
    monday.setHours(3, 0, 0, 0);
    return monday.toISOString().split('T')[0] + ' 03:00';
  };
  const registDay = getNextMonday3AM();

  const openFile = () => {
    imageRef.current?.click();
  };

  return (
  <div style={{display:"flex"}}>
    <div className="table-container" style={{marginLeft:"20px",marginTop:"0px", width:"500px", boxShadow:"0 2px 6px rgba(0, 0, 0, 0.3)"}}>
      <h3 className="form-header" >
        🖼️ 배너 광고 신청
        <span style={{ fontSize: '14px', marginLeft: '10px', color: '#555' }}>
          📅 {registDay}(월) 등록 예정
        </span>
      </h3>
      <div className="form-container" style={{margin:"0px"}}>
        <form onSubmit={handleSubmit} encType="multipart/form-data">
          <div className="form-group">
            <label>렌탈샵 선택</label>
            <select value={selectedRentId} onChange={e => setSelectedRentId(Number(e.target.value))}>
              {rentList.map(r => (
                <option key={r.rentId} value={r.rentId}>{r.name}</option>
              ))}
            </select>
          </div>
          <div className="form-group">
            <label>이번 주 최고 입찰가</label>
            <input type="text" value={maxBid} readOnly/>
          </div>
          <div className="form-group">
            <label>CPC 입찰가</label>
            <input
              type="number"
              value={cpcBid}
              onChange={e => setCpcBid(e.target.value)}
              required             
            />
          </div>
          <div className="form-group">
          <label>
            예상 노출도 점수
            <span className="tooltip-icon">?
              <span className="tooltip-text" style={{width:"410px"}}>
                노출도 점수 산정방식: <br/> 0.2×평균별점 + 0.3×(입찰가/최고입찰가) + 0.5×최근7일평점

              </span>
            </span>
          </label>
          <input type="text" value={finalScore} readOnly />
          <p className="score-info" style={{ fontSize: '10px', color: '#555' }} >
            상위 {percentile}%의 순서로 배너가 노출 될 예정입니다
          </p>
          </div>
          <div className="form-group">
          <label>배너 이미지</label>
          <div>
            <button type="button" className="file-button" onClick={openFile}>
              이미지 선택
            </button>
            <button type="submit" style={{marginLeft:"100px"}}>신청하기</button> <br />
            <span className="file-name">
              {imageRef.current?.files[0]?.name || '선택된 파일 없음'}
            </span>
            <input
                type="file"
                ref={imageRef}
                accept="image/*"
                style={{ display: 'none' }}
                onChange={handleFileChange}
            />
          </div>
          </div>
          
        </form>
      </div>
    </div>
    <div className='table-container' style={{marginLeft:"20px",marginTop:"0px", width:"1100px", boxShadow:"0 2px 6px rgba(0, 0, 0, 0.3)"}}>
      <h3 className="form-header" style={{borderBottom:"1px solid #c8c8c8"}}>
        ⚠️ 주의사항        <br /><br />
        <span style={{ fontSize: '14px', marginLeft: '10px', color: '#555' }}>
          * 배너 등록 요청 시 등록비용으로 150,000원이 차감됩니다. 
          <br />&nbsp;&nbsp;* 요청된 배너는 내부심사 이후 '승인 반려' 시 등록비용이 반환됩니다.
          <br />&nbsp;&nbsp;* 승인된 배너는 매주 월요일 오전 3:00에 갱신됩니다.          
          <br />&nbsp;&nbsp;* 보유 캐시가 모두 소진될 시, 다시 충전될 때 까지 자동으로 배너는 홈페이지에 노출되지 않습니다.
          <br />&nbsp;&nbsp;* 등록된 배너 및 클릭당 비용은 일주일간 유지되며, 이용자의 클릭 수에 따라 5분에 한번씩 갱신되어 보유캐시에서 차감됩니다.
          <br /><br />
        </span>
      </h3>
      <h3 className="form-header">
        👀 미리보기
      </h3>
    </div>
  </div>
  );
};

export default BannerApplyForm;