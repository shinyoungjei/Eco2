import { configureStore } from "@reduxjs/toolkit";
import { userSlice } from "./user/userSlice";
import { leavesSlice } from "./mainTree/leavesSlice";
import { userInformationSlice } from "./user/userSettingSlice";
import { accountSlice } from "./user/accountSlice";
import { dailymissionSlice } from "./mission/dailymissionSlice";
import { myEcoMissionSlice } from "./mission/missionMainSlice";
import { cumtomSlice } from "./mission/customMissionSlice";
import { favoriteSlice } from "./mission/favoriteSlice";
import { alarmSlice } from "./alarm/alarmSlice";

import { postSlice } from "./post/postSlice";
import { commentSlice } from "./post/commentSlice";
import { noticeSlice } from "./admin/noticeSlice";
import { imgSlice } from "./img/imgSlice";
import { questSlice } from "./quest/questSlice";
import { reportSlice } from "./admin/reportSlice";
import { chattingSlice } from "./chat/chattingSlice";

export const store = configureStore({
  reducer: {
    user: userSlice.reducer,
    userInformation: userInformationSlice.reducer,
    account: accountSlice.reducer,
    comment: commentSlice.reducer,
    leaves: leavesSlice.reducer,
    dailyMission: dailymissionSlice.reducer,
    missionMain: myEcoMissionSlice.reducer,
    custom: cumtomSlice.reducer,
    favorite: favoriteSlice.reducer,
    post: postSlice.reducer,
    notice: noticeSlice.reducer,
    img: imgSlice.reducer,
    alarm: alarmSlice.reducer,
    quest: questSlice.reducer,
    report: reportSlice.reducer,
    chatting: chattingSlice.reducer,
  },
});
