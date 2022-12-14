import { React, useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { login, googleLogin } from "../../store/user/userSlice";
import styles from "./Login.module.css";
import { GreenBtn, LoginInput, WarningText } from "../../components/styled";
import { signInGoogle, auth } from "../../store/firebase";
import {
  getUserId,
  getUserName,
  setAccessToken,
  setUserEmail,
  setUserId,
  setUserName,
} from "../../store/user/common";
import { emailValidationCheck } from "../../utils";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loginFailMsg, setLoginFailMsg] = useState(false);
  const [message, setMessage] = useState("");
  const [autoLogin, setAutoLogin] = useState(false);

  const [emailMessage, setEmailMessage] = useState("");
  const [isEmail, setIsEmail] = useState(false);

  const navigate = useNavigate();
  const dispatch = useDispatch();
  const location = useLocation();

  const redirectPath = location.state?.path || "/mainTree";

  // 이메일 형식 확인 -> 중복 확인
  const emailValidation = (e) => {
    setEmail(e.target.value);
    if (emailValidationCheck(e.target.value)) {
      setEmailMessage("이메일 형식이 틀렸어요! 다시 확인해주세요");
      setIsEmail(false);
    } else {
      setEmailMessage("올바른 이메일 형식이에요 : )");
      setIsEmail(true);
    }
  };

  // 로그인 요청
  // 요청 성공시 sessionstorage에 이메일과 이름 저장 후 메인피드로 이동
  // 자동 로그인 체크 시 localstorage에 저장됨
  const handleSubmit = (event) => {
    event.preventDefault();
    dispatch(login({ email: email, password: password, socialType: 0 }))
      .then((res) => {
        if (res.payload?.status === 200) {
          setLoginFailMsg(false);
          setUserEmail(autoLogin, email);
          setUserName(autoLogin, res.payload.user.name);
          setUserId(autoLogin, res.payload.user.id);
          setAccessToken(autoLogin, res.payload.accessToken);
          // navigate(redirectPath, { replace: true });
          window.location.replace(redirectPath);
        }
        setLoginFailMsg(true);
        setMessage("등록된 이메일이 없거나 비밀번호가 일치하지 않습니다.");
      })
      .catch((err) => console.log(err));
  };

  // 구글 로그인
  // 요청 성공시 sessionstorage에 이메일과 이름 저장 후 메인피드로 이동
  const onGoogleLogin = async () => {
    const data = await signInGoogle();
    auth.currentUser
      .getIdToken(true)
      .then(function (idToken) {
        dispatch(
          googleLogin({
            socialType: 1,
            idToken: idToken,
          })
        ).then((res) => {
          if (res.payload?.status === 200) {
            setLoginFailMsg(false);
            if (!res.payload.user.name) {
              setUserEmail(false, data.additionalUserInfo.profile.email);
              setUserId(false, res.payload.user.id);
              setAccessToken(autoLogin, res.payload.accessToken);
              navigate("/ecoName");
            } else {
              setUserEmail(false, data.additionalUserInfo.profile.email);
              setUserId(false, res.payload.user.id);
              setUserName(false, res.payload.user?.name);
              setAccessToken(autoLogin, res.payload.accessToken);
              window.location.reload(redirectPath);
            }
          } else if (res.payload?.status === 202) {
            setLoginFailMsg(true);
            setMessage("이미 다른 소셜로 가입한 이메일입니다.");
          }
        });
      })
      .catch(function (error) {
        console.log(error);
      });
  };

  const onKakaoLogin = async () => {
    window.location.href = `https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=${process.env.REACT_APP_KAKAO_REST_API_KEY}&redirect_uri=${process.env.REACT_APP_KAKAO_REDIRECT}&scope=account_email`;
  };

  useEffect(() => {
    if (!!getUserId() && getUserId() != null) {
      navigate("/mainTree");
    }
  });

  return (
    <div className={styles.login}>
      <img
        src={`${process.env.PUBLIC_URL}/logo2.png`}
        alt="earth"
        className={styles.img}
      />
      <form onSubmit={handleSubmit} className={styles.form}>
        <LoginInput
          type="email"
          className={styles.input}
          required
          value={email}
          placeholder="이메일"
          onChange={emailValidation}
        />
        {email.length > 0 && (
          <p className={isEmail ? styles.success : styles.fail}>
            {emailMessage}
          </p>
        )}
        {email.length === 0 && <div className={styles.test}></div>}
        <LoginInput
          type="password"
          className={styles.input}
          required
          value={password}
          placeholder="비밀번호"
          onChange={(e) => setPassword(e.target.value)}
        />
        {loginFailMsg ? <WarningText>{message}</WarningText> : null}
        <div className={styles.wrapper}>
          <input
            className={styles.checkbox1}
            type="checkbox"
            id="autoLogin"
            onChange={(e) => setAutoLogin(e.target.checked)}
          />
          <label
            htmlFor="autoLogin"
            className={`${styles.label1} ${styles.label}`}
          >
            <div className={styles.dot}></div>
            <span className={styles.labelText}>자동로그인</span>
          </label>
        </div>
        <GreenBtn
          type="submit"
          disabled={!(isEmail && password)}
          className={styles.loginButton}
        >
          로그인
        </GreenBtn>
      </form>
      <div className={styles.lineGroup}>
        <hr className={styles.shortLine} />
        <span className={styles.lineText}>SNS로 3초만에 시작하기</span>
        <hr className={styles.longLine} />
      </div>
      <div className={styles.socialGroup}>
        <button onClick={onGoogleLogin} className={styles.googleButton}>
          <img
            src="google_logo.png"
            alt="social_logo"
            className={styles.googleLogo}
          />
          <p className={styles.google}>구글 로그인</p>
        </button>
        <button onClick={onKakaoLogin} className={styles.kakaoButton}>
          <img
            src="kakao_login_medium_wide.png"
            alt="kakaoLogin"
            className={styles.kakaoLogo}
          />
        </button>
      </div>
      <div className={styles.lineGroup}>
        <hr className={styles.shortLine2} />
        <span className={styles.lineText}>제가</span>
        <hr className={styles.longLine2} />
      </div>
      <Link to="/regist" className={styles.link}>
        <p className={styles.text}>아직 회원이 아니에요</p>
      </Link>
      <Link to="/findpassword" className={styles.link}>
        <p className={styles.text}>비밀번호를 잊어버렸어요</p>
      </Link>
    </div>
  );
}

export default Login;
