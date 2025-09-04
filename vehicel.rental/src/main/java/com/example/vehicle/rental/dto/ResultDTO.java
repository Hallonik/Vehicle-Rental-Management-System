package com.example.vehicle.rental.dto;

public class ResultDTO<T> {
    private T data;
    private int statusCode;
    private String message;

    public T getData() {
		return data;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getMessage() {
		return message;
	}

	public ResultDTO() {}

    public ResultDTO(T data, int statusCode, String message) {
        this.data = data;
        this.statusCode = statusCode;
        this.message = message;
    }

    public static <T> ResultDTO<T> success(T data) {
        return new ResultDTO<>(data, 200, "Success");
    }

    public static <T> ResultDTO<T> error(String message, int statusCode) {
        return new ResultDTO<>(null, statusCode, message);
    }

	public void setData(T data) {
		this.data = data;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public void setMessage(String message) {
		this.message = message;
	}
    
    
}