import { useSelector } from "react-redux";
import { Link } from "react-router-dom";
import LogoutButton from "../LogoutButton";

const tab = "block rounded-lg px-4 py-2 text-sm font-medium";
const active_style = 'bg-blue-50 text-blue-400 font-semibold hover:underline hover:underline-offset-2';
const inactive_style = 'text-gray-700 hover:bg-gray-100 hover:text-black';

const MySideMenu = ({group, active}) => {
    const profile = useSelector(state => state.loginSlice);


    return (
        <>
            <div className="w-[270px] min-h-[600px] flex flex-col justify-between border rounded-2xl border-gray-200 bg-white shadow-md">
                <div className="px-4 py-6">
                    <div className="flex flex-col items-center gap-2 mt-6 mb-12">
                        {
                            !profile.isLoading
                            &&
                            <img src={profile.image} className="w-[100px] h-[100px] rounded-full"></img>
                        }

                        <div className="flex items-center gap-2">
                            {
                                profile.social == 'KAKAO'
                                &&
                                <img src="/images/kakao_link.png" width={20}></img>
                                ||
                                profile.social == 'NAVER'
                                &&
                                <img src="/images/naver_link.png" width={20}></img>
                            }
                            <span className="text-sm font-semibold">{profile.showname}</span><span className="text-sm text-gray-500">님</span>
                        </div>
                    </div>

                    <ul className="space-y-3">
                        <li>
                            <details className="group [&_summary::-webkit-details-marker]:hidden" open={group == 'account' ? true : false}>
                                <summary
                                    className="flex cursor-pointer items-center justify-between rounded-lg px-4 py-2 text-gray-500 hover:bg-gray-100 hover:text-gray-700"
                                >
                                    <span className="text-sm font-medium">계정</span>

                                    <span className="shrink-0 transition duration-300 group-open:-rotate-180">
                                        <svg
                                            xmlns="http://www.w3.org/2000/svg"
                                            className="size-5"
                                            viewBox="0 0 20 20"
                                            fill="currentColor"
                                        >
                                            <path
                                                fillRule="evenodd"
                                                d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z"
                                                clipRule="evenodd"
                                            />
                                        </svg>
                                    </span>
                                </summary>

                                <ul className="mt-2 space-y-1 px-4">
                                    <li>
                                        <Link
                                            to={'/mypage/account'}
                                            className={`${tab} ${active == 'account' ? active_style : inactive_style}`}
                                        >
                                            회원 정보
                                        </Link>
                                    </li>

                                    <li>
                                        <Link
                                            to={'/mypage/account/security'}
                                            className={`${tab} ${active == 'security' ? active_style : inactive_style}`}
                                        >
                                            보안 사항
                                        </Link>
                                    </li>
                                </ul>
                            </details>
                        </li>

                        <li>
                            <details className="group [&_summary::-webkit-details-marker]:hidden" open={group == 'payment' ? true : false}>
                                <summary
                                    className="flex cursor-pointer items-center justify-between rounded-lg px-4 py-2 text-gray-500 hover:bg-gray-100 hover:text-gray-700"
                                >
                                    <span className="text-sm font-medium">결제</span>

                                    <span className="shrink-0 transition duration-300 group-open:-rotate-180">
                                        <svg
                                            xmlns="http://www.w3.org/2000/svg"
                                            className="size-5"
                                            viewBox="0 0 20 20"
                                            fill="currentColor"
                                        >
                                            <path
                                                fillRule="evenodd"
                                                d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z"
                                                clipRule="evenodd"
                                            />
                                        </svg>
                                    </span>
                                </summary>

                                <ul className="mt-2 space-y-1 px-4">
                                    <li>
                                        <Link
                                            to="/mypage/reserve"
                                            className={`${tab} ${active == 'reserve' ? active_style : inactive_style}`}
                                        >
                                            예약 목록
                                        </Link>
                                    </li>

                                    <li>
                                        <Link
                                            to="/mypage/refund"
                                            className={`${tab} ${active == 'refund' ? active_style : inactive_style}`}
                                        >
                                            환불 내역
                                        </Link>
                                    </li>
                                </ul>
                            </details>
                        </li>

                        <li>
                            <details className="group [&_summary::-webkit-details-marker]:hidden" open={group == 'activity' ? true : false}>
                                <summary
                                    className="flex cursor-pointer items-center justify-between rounded-lg px-4 py-2 text-gray-500 hover:bg-gray-100 hover:text-gray-700"
                                >
                                    <span className="text-sm font-medium">활동</span>

                                    <span className="shrink-0 transition duration-300 group-open:-rotate-180">
                                        <svg
                                            xmlns="http://www.w3.org/2000/svg"
                                            className="size-5"
                                            viewBox="0 0 20 20"
                                            fill="currentColor"
                                        >
                                            <path
                                            fillRule="evenodd"
                                            d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z"
                                            clipRule="evenodd"
                                            />
                                        </svg>
                                    </span>
                                </summary>

                                <ul className="mt-2 space-y-1 px-4">
                                    <li>
                                        <Link
                                            to="/mypage/review"
                                            className={`${tab} ${active == 'review' ? active_style : inactive_style}`}
                                        >
                                            리뷰 관리
                                        </Link>
                                    </li>

                                    <li>
                                        <Link
                                            to="/mypage/qna"
                                            className={`${tab} ${active == 'qna' ? active_style : inactive_style}`}
                                        >
                                            문의 관리
                                        </Link>
                                    </li>
                                </ul>
                            </details>
                        </li>

                        <li>
                            <LogoutButton styleClass="flex cursor-pointer items-center justify-between rounded-lg mt-6 px-4 py-2 text-gray-500 hover:bg-gray-100 hover:text-gray-700 text-sm"></LogoutButton>
                        </li>
                    </ul>
                </div>
            </div>
        </>
    )
}

export default MySideMenu;