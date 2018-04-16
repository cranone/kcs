package com.shadego.fkg;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;

import com.shadego.kcs.util.RetrofitFactory;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class Main {

    public static void main(String[] args) {
        String l="cdnFQp41oqcPN7vhVu2MBCrOD+TQosWW5NqvNUPhya5agpVdeA1xeewJTEB7K0MZR4zcVOHSZJuOnGBDpkjKBw==";
        byte[] decodeBase64 = Base64.decodeBase64(l);
        System.out.println(new String(decodeBase64,Charset.forName("UTF-8")));
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), l);  
//        RetrofitFactory.getInstance().getApiService().postf("http://web.flower-knight-girls.co.jp/api/v1/master/getMaster",requestBody)
//        .subscribe(response -> {
//            byte[] decompress = ZLibUtils.decompress((response.body().bytes()));
//            byte[] decodeBase64 = Base64.decodeBase64(decompress);
//            String res=new String(decodeBase64,Charset.forName("UTF-8"));
//           System.out.println(res); 
//        });
    }
}
