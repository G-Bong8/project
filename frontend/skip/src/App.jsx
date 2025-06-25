import { BrowserRouter, Route, Routes } from "react-router-dom"
import Home from "./pages/Home"
import MainLayout from "./pages/MainLayout"
import LoginPage from "./pages/login/LoginPage"
import SignupPage from "./pages/login/SignupPage"
import { useDispatch, useSelector } from "react-redux"
import { setProfile } from "./slices/loginSlice"
import { useEffect } from "react"
import RentalshopPage from "./pages/RentalshopPage"
import ProductPage from "./pages/ProductPage"
import ReviewPopupPage from "./pages/ReviewPopupPage"
import RentInsertForm from "./components/rentManager/RentInsertForm"
import RentList from "./components/rentManager/RentList"
import RentDetail from "./components/rentManager/RentDetail"
import RentUpdateForm from "./components/rentManager/RentUpdateForm"
import ItemInsertForm from "./components/rentManager/ItemInsertForm"
import ItemListAndDetails from "./components/rentManager/ItemListAndDetails"
import ItemUpdateForm from "./components/rentManager/ItemUpdateForm"
import CartList from "./components/cart/cartList"
import QnaPopupPage from "./pages/QnaPopupPage"
import AdminLayout from "./pages/admin/AdminLayout"
import AdminDashboard from "./pages/admin/AdminDashBoard"
import RentDashboard from "./pages/rentalAdmin/RentDashboard"
import UsersList from "./pages/admin/UsersList"
import LoginLayout from "./pages/login/LoginLayout"
import AccountPage from "./pages/myPage/AccountPage"
import AccountSecurityPage from "./pages/myPage/AccountSecurityPage"
import MyReviewPage from "./pages/myPage/MyReviewPage"
import MyQnaPage from "./pages/myPage/MyQnaPage"
import MyPageLayout from "./pages/myPage/MyPageLayout"
import AdminQnaList from "./components/qna/AdminQnaList"
import WishList from "./components/WishList"
import PasswordResettingPage from "./pages/login/PasswordResettingPage"
import PasswordResetSuccessPage from "./pages/login/PasswordResetSuccessPage"
import UsernameInputPage from "./pages/login/UsernameInputPage"
import UsernameFindPage from "./pages/login/UsernameFindPage"
import UsernameFindSuccessPage from "./pages/login/UsernameFindSuccessPage"
import MyReservePage from "./pages/myPage/MyReservePage"
import RefundList from "./components/rentManager/RefundList"
import UserApprovalTable from "./components/adminpage/UserApprovalTable"
import UserPendingTable from "./components/adminpage/UserPendingTable"
import UserWithdrawTable from "./components/adminpage/UserWithdrawTable"
import AdminReviewList from "./components/review/AdminReviewList"
import ActiveBannerList from "./pages/admin/ActiveBannerList"
import PendingBannerList from "./pages/admin/PendingBannerList"
import RentLayout from "./pages/rentalAdmin/RentLayout"
import MyRefundPage from "./pages/myPage/MyRefundPage"
import BoostPurchasePage from "./pages/rentalAdmin/BoostPurchasePage"
import CashChargePage from "./pages/rentalAdmin/CashCahrgePage"
import BannerApplyPage from "./pages/rentalAdmin/BannerApplyPage"
import BannerResubmitPage from "./pages/rentalAdmin/BannerResubmitPage"
import ReservationList from './components/rentManager/ReservationList';
import SearchResultPage from "./pages/SearchResultPage"

const safeUUID = () => {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        const r = (Math.random() * 16) | 0,
        v = c === 'x' ? r : (r & 0x3) | 0x8;
        return v.toString(16);
    });
};


function App() {
    let deviceId = localStorage.getItem('deviceId');
    if (!deviceId) {
        deviceId = safeUUID();
        localStorage.setItem('deviceId', deviceId);
    }

    const dispatch = useDispatch();

    useEffect(() => {
        dispatch(setProfile());
    }, []);


    return (
        <>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<MainLayout></MainLayout>}>
                        {/* 홈페이지 */}
                        <Route index element={<Home></Home>}></Route>

                        {/* 마이페이지 */}
                        <Route path="mypage/" element={<MyPageLayout></MyPageLayout>}>
                            <Route path="account" element={<AccountPage></AccountPage>}></Route>

                            <Route path="account/security" element={<AccountSecurityPage></AccountSecurityPage>}></Route>

                            <Route path="reserve" element={<MyReservePage></MyReservePage>}></Route>

                            <Route path="refund" element={<MyRefundPage></MyRefundPage>}></Route>

                            <Route path="review" element={<MyReviewPage></MyReviewPage>}></Route>

                            <Route path="qna" element={<MyQnaPage></MyQnaPage>}></Route>
                        </Route>


                        {/* 중간관리자 */}
                        <Route path="/rentAdmin" element={<RentLayout />}>
                            <Route index element={<RentDashboard />} />
                            <Route path="/rentAdmin/insert" element={<RentInsertForm/>}></Route>
                            <Route path="/rentAdmin/list" element={<RentList/>}></Route>
                            <Route path="/rentAdmin/detail/:rentId" element={<RentDetail/>}></Route>
                            <Route path="/rentAdmin/update/:rentId" element={<RentUpdateForm/>}></Route>
                            <Route path="/rentAdmin/item/insert/:rentId?" element={<ItemInsertForm/>}></Route>
                            <Route path="/rentAdmin/item/list/" element={<ItemListAndDetails/>}></Route>
                            <Route path="/rentAdmin/item/update/:rentId/:itemId" element={<ItemUpdateForm/>}></Route>
                            <Route path="/rentAdmin/reservManager/list" element={<ReservationList/>}></Route>
                            <Route path="/rentAdmin/refundManager/list" element={<RefundList/>}></Route>
                            <Route path="/rentAdmin/cash" element={<CashChargePage/>}></Route>
                            <Route path="/rentAdmin/boost" element={<BoostPurchasePage/>}></Route>
                            <Route path="/rentAdmin/banner" element={<BannerApplyPage/>}></Route>
                            <Route path="/rentAdmin/banner/edit/:waitingId" element={<BannerResubmitPage/>}></Route>
                            <Route path="/rentAdmin/qna" element={<AdminQnaList></AdminQnaList>} />
                            <Route path="/rentAdmin/review" element={<AdminReviewList></AdminReviewList>} />

                        </Route>


                        {/* 사용자 장바구니 */}
                        <Route path="/cart/list" element={<CartList/>}></Route>
                        {/* 사용자 찜 */}
                        <Route path="/wish/list" element={<WishList/>}></Route>

                        {/* 렌탈샵 상세 페이지 */}
                        <Route path="/rent/detail/:rentId" element={<RentalshopPage/>}></Route>
                        <Route path="/rent/product/:rentId/:itemId" element={<ProductPage/>}></Route>

                        {/* 렌탈샵 검색 페이지 */}
                        <Route path="/rent/search" element={<SearchResultPage></SearchResultPage>}></Route>
                    </Route>

                    {/* 관리자 라우트 */}
                    <Route path="/admin" element={<AdminLayout />}>
                        <Route index element={<AdminDashboard />} />
                        {/* 추가 라우트 */}
                        <Route path="/admin/pendinglist" element={<UserPendingTable/>} />
                        <Route path="/admin/withdrawnlist" element={<UserWithdrawTable/>} />
                        <Route path="/admin/approvallist" element={<UserApprovalTable/>} />                        
                        <Route path="/admin/userlist" element={<UsersList/>} />
                        <Route path="/admin/activebannerList" element={<ActiveBannerList/>} />
                        <Route path="/admin/pendingbannerList" element={<PendingBannerList/>} />
                    </Route>
                    {/* 리뷰 팝업 */}
                    <Route path="/reviews/write/:rentItemId" element={<ReviewPopupPage />}></Route>
                    <Route path="/reviews/edit/:reviewId" element={<ReviewPopupPage/>}></Route>
                    {/* Q&A 팝업 */}
                    <Route path="/rent/product/:rentId/:itemId/qna/write" element={<QnaPopupPage />}></Route>
                    <Route path="/rent/product/:rentId/:itemId/qna/edit/:qnaId" element={<QnaPopupPage mode="edit"/>}/>


                    {/* 로그인 */}
                    <Route element={<LoginLayout></LoginLayout>}>
                        <Route path="/login" element={<LoginPage></LoginPage>}></Route>

                        <Route path="/signup" element={<SignupPage></SignupPage>}></Route>

                        <Route path="/id/find" element={<UsernameFindPage></UsernameFindPage>}></Route>

                        <Route path="/id/find/success" element={<UsernameFindSuccessPage></UsernameFindSuccessPage>}></Route>

                        <Route path="/password/reset/id" element={<UsernameInputPage></UsernameInputPage>}></Route>

                        <Route path="/password/reset" element={<PasswordResettingPage></PasswordResettingPage>}></Route>

                        <Route path="/password/reset/success" element={<PasswordResetSuccessPage></PasswordResetSuccessPage>}></Route>
                    </Route>
                </Routes>
            </BrowserRouter>
        </>
    )
}

export default App;