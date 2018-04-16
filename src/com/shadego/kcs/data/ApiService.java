package com.shadego.kcs.data;

import com.alibaba.fastjson.JSONObject;
import com.shadego.kcs.util.Constant;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
* @author Maclaine E-mail:deathencyclopedia@gmail.com
* 
*/
public interface ApiService {

	@GET
	public Observable<JSONObject> get(@Url String url);
	
	@FormUrlEncoded
	@POST(Constant.API_URL)
	public Flowable<Response<ResponseBody>> post(@Field("api_verno") String verno, @Field("api_token") String token);
	
	@Streaming
	@GET
	public Flowable<Response<ResponseBody>> download(@Url String url);
	
	@HEAD
    public Flowable<Response<Void>> downloadHeader(@Url String url);
	
//	@POST
//    public Flowable<Response<ResponseBody>> postf(@Url String url,@Body RequestBody body);
}
