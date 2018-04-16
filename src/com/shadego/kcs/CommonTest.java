package com.shadego.kcs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.alibaba.fastjson.JSONObject;
import com.shadego.kcs.data.Api;
import com.shadego.kcs.data.Download;
import com.shadego.kcs.entity.ShipMapping;
import com.shadego.kcs.util.Constant;
import com.shadego.kcs.util.FileUtil;
import com.shadego.kcs.util.HttpUtil;
import com.shadego.kcs.util.RetrofitFactory;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * @author Maclaine E-mail:deathencyclopedia@gmail.com
 * 
 */
public class CommonTest {

    public static void main(String[] args) throws IOException, InterruptedException {
       /* long count = 1000000;
        System.out.println("start:" + new Date());
        int threadCount = 10;
        ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            threadPoolExecutor.execute(() -> {
                timeTest2((long) (Math.pow(count, 2) / threadCount));
            });
        }

        timeTest2((long) Math.pow(count, 2));

        threadPoolExecutor.shutdown();
        threadPoolExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);*/
//        Request request = new Request.Builder().url("http://125.6.189.135/kcs/sound/kchrkuspdmjtmd/176242.mp3").head().build();
//        OkHttpClient client = new OkHttpClient();
//        Response response = client.newCall(request).execute();
//        System.out.println(response.header("Last-Modified"));;

//        RetrofitFactory.getInstance().getApiService().downloadHeader("http://125.6.189.135/kcs/sound/kchrkuspdmjtmd/176242.mp3")
//            .subscribe(response->{
//                System.out.println(response.headers().get("Last-Modified"));
//            });
//        ;
        Date d=new Date();
        long t=1513169952;
        d.setTime(t*1000);
        System.out.println(d);
    }

    public static void timeTest2(long i) {
        while (i > 0) {
            i--;
        }
        System.out.println("ok:" + new Date());
    }

    
    
    
    
    public static void timeTest1(long i) {
        if (i <= 0) {
            System.out.println(new Date());
            return;
        }
        timeTest1(--i);
    }

}
