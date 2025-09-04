export const API_URLS = {
  BASE_URL: 'http://hallonik.ap-south-1.elasticbeanstalk.com',
  LOGIN: '/api/admin/SignIn',
  SIGNUP: '/api/auth/signUp',
  SENDOTP:"/api/auth/send-otp",
  FORGOTPASSWORD: '/api/admin/forgotPassword',
  getVehicle: '/api/vehicles/available',
  CalculateFare: '/api/Fare/FareCalculation',
    paymentProcess: '/api/paymentprocess/PaymentProcess',
    getVehicles: '/api/vehicles/bookedByUser/',
    getDashboard:'/api/admin/Get_admin_dash_summary',
    transcationHistory: '/api/paymentprocess/AllTransactions',
    submitFeedback: '/api/feedback/submit' ,  // <-- add your backend feedback API here
    getAllFeedback: '/api/feedback/all',
    refundBooking:  '/api/paymentprocess/RefundByTransactionId/'
};
