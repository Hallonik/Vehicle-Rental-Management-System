// 🔹 Generic Result wrapper (same as your backend ResultDTO)
export interface ResultDTO<T> {
  data: T;
  statusCode: number;
  message: string;
}

// 🔹 Payment record
export interface Payment {
  paymentId: number;
  name: string;
  email: string;
  amount: number;
  paymentMode: string;
  paymentFor: string;
  status: string;
  remark: string;
}

// 🔹 Count by payment mode
export interface PaymentModeCounts {
  [mode: string]: number; // Example: { "CASH": 5, "CARD": 3 }
}
