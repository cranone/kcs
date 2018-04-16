package com.shadego.kcs.util;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.support.retrofit.Retrofit2ConverterFactory;
import com.shadego.kcs.data.ApiService;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * @author Maclaine E-mail:deathencyclopedia@gmail.com
 * 
 */
public class RetrofitFactory {
    private static final Logger logger = LoggerFactory.getLogger(RetrofitFactory.class);
	private static RetrofitFactory mInstance;
	private static ApiService apiService;

	public RetrofitFactory() {
		OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(chain -> {
			Request.Builder builder = chain.request().newBuilder();
			builder.addHeader("user-agent", Constant.USER_AGENT)
				.addHeader("accept", "*/*")
				.addHeader("connection", "Keep-Alive")
				.addHeader("contentType", "text/html;charset=uft-8")
			;
            return chain.proceed(builder.build());
		}).connectTimeout(Constant.TIMEOUT, TimeUnit.SECONDS).readTimeout(Constant.TIMEOUT, TimeUnit.SECONDS).build();
		Retrofit retrofit = new Retrofit.Builder().client(httpClient).baseUrl(Constant.BASE_URL)
		         .addConverterFactory(new Retrofit2ConverterFactory())
		        .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
		apiService = retrofit.create(ApiService.class);
	}

	public static RetrofitFactory getInstance() {
		if (mInstance == null) {
			synchronized (RetrofitFactory.class) {
				mInstance = new RetrofitFactory();
			}
		}
		return mInstance;
	}

	public ApiService getApiService() {
		return apiService;
	}
}
