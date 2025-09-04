// src/app/utils/Constants.ts
export const API_URLS1 = {
  // ✅ Base backend URL
  BASE_URL: 'http://hallonik.ap-south-1.elasticbeanstalk.com',

  // ✅ Auth endpoints
  LOGIN: '/api/auth/login',
  SIGNUP: '/api/auth/register',
  SEND_OTP: '/api/auth/send-otp',
  VERIFY_OTP: '/api/auth/verify-otp',
  FORGOT_PASSWORD: '/api/auth/forgot-password',

  // ✅ Admin endpoints (if still needed)
  ADMIN_LOGIN: '/api/admin/SignIn',
  ADMIN_SIGNUP: '/api/admin/Signup',
  ADMIN_FORGOT_PASSWORD: '/api/admin/forgotPassword'
};
