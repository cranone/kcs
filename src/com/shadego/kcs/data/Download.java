package com.shadego.kcs.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shadego.kcs.entity.ShipMapping;
import com.shadego.kcs.util.Constant;
import com.shadego.kcs.util.GlobalConst;
import com.shadego.kcs.util.RetrofitFactory;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * @author Maclaine E-mail:deathencyclopedia@gmail.com
 * 
 */
public class Download {

    private static final Logger logger = LoggerFactory.getLogger(Download.class);

    public Download() {
    }

    public void download(Map<Integer, ShipMapping> shipMap) {

        ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(10);
        List<String> errorUrls = new ArrayList<>();
        List<String> updateUrls = new ArrayList<>();
        // List<Flowable<Response<ResponseBody>>> flowableList = new ArrayList<>();
        // Map<String, Flowable<Response<ResponseBody>>> downloadList = new HashMap<>();
        Map<String, Flowable<Response<Void>>> headerList = new HashMap<>();

        shipMap.forEach((key, value) -> {
            String path = Constant.BASE_URL + Constant.SHIP_URL + value.getFilename() + ".swf";
            if (checkFile(path)) {
                headerList.put(path, RetrofitFactory.getInstance().getApiService().downloadHeader(path));
            }
            Set<Integer> soundFileNames = value.getSoundFileNames();
            if (soundFileNames != null && soundFileNames.size() != 0) {
                soundFileNames.forEach(voiceName -> {
                    String voicePath = Constant.BASE_URL + Constant.SOUND_URL + "kc" + value.getFilename() + "/"
                            + voiceName + ".mp3";
                    if (checkFile(voicePath)) {
                        headerList.put(voicePath,
                                RetrofitFactory.getInstance().getApiService().downloadHeader(voicePath));
                    }
                });
            }
        });

        Result reading = new Result();
        Result reading2 = new Result();
        Disposable subscribe = Observable.interval(100, TimeUnit.MILLISECONDS).subscribe(v -> {
            synchronized (reading) {
                if (!reading.successful && !errorUrls.isEmpty()) {
                    reading.successful = true;
                    RandomAccessFile randomFile = new RandomAccessFile("errorUrls.txt", "rw");
                    long fileLength = randomFile.length();
                    randomFile.seek(fileLength);
                    randomFile.writeBytes(String.valueOf(errorUrls.get(0)) + "\r\n");
                    randomFile.close();
                    errorUrls.remove(0);
                    reading.successful = false;
                }
            }
            synchronized (reading2) {
                if (!reading2.successful && !updateUrls.isEmpty()) {
                    reading2.successful = true;
                    RandomAccessFile randomFile = new RandomAccessFile("updateUrls.txt", "rw");
                    long fileLength = randomFile.length();
                    randomFile.seek(fileLength);
                    randomFile.writeBytes(String.valueOf(updateUrls.get(0)) + "\r\n");
                    randomFile.close();
                    updateUrls.remove(0);
                    reading2.successful = false;
                }
            }
        });

        Result count = new Result();
        headerList.entrySet().forEach(entry -> {
            Flowable<Response<Void>> current = entry.getValue();
            String url = entry.getKey();
            String path = Api.getPathFromURL(url);
            String shipName=Api.getShipNameByPath(shipMap.values(),path);
            threadPoolExecutor.execute(() -> {
                count.count++;
                logger.info("{}/{}:{}", count.count, headerList.size(),shipName);
                SimpleDateFormat sdfHeader = new SimpleDateFormat("EEE,dd MMM yyyy HH:mm:ss Z", Locale.US);
                if (path == null) {
                    logger.error("can't resolve this url:{}", url);
                    return;
                }
                File file = new File(path);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        errorUrls.add(url);
                        logger.error("error:{}", e.getMessage());
                        return;
                    }
                }
                current.retry(GlobalConst.retryTime).filter(result -> {
                    if (!result.isSuccessful()) {
                        errorUrls.add(url);
                        file.delete();
                        logger.error("error:{}", url);
                        return false;
                    }
                    Headers headers = result.headers();
                    String headerLength = headers.get("Content-Length");
                    String headerLastModified = headers.get("Last-Modified");
                    long fileLastModified = file.lastModified();
                    long serverLastModified = sdfHeader.parse(headerLastModified).getTime();
                    if (fileLastModified >= serverLastModified && headerLength.equals(String.valueOf(file.length()))) {// 最后修改时间大于等于服务器,不下载
                        // logger.info("ignore:{}", url);
                        return false;
                    }
                    return true;
                }).flatMap(result -> {
                    return RetrofitFactory.getInstance().getApiService().download(url).retry(GlobalConst.retryTime);
                }).doOnNext(result -> {
                    logger.info("start download:{}:{}", shipName,url);
                    try (FileOutputStream fs = new FileOutputStream(file);
                            FileChannel fc = fs.getChannel();
                            ResponseBody body = result.body();) {
                        byte[] bytes = body.bytes();
                        ByteBuffer bb = ByteBuffer.wrap(bytes);
                        bb.put(bytes);
                        bb.flip();
                        fc.write(bb);
                        body.close();
                    } catch (Exception e) {
                        errorUrls.add(url);
                        logger.error("error:{}", e.getMessage());
                    }
                    try {
                        file.setLastModified(sdfHeader.parse(result.headers().get("Last-Modified")).getTime());
                    } catch (Exception e) {
                        logger.error("error:{}", e.getMessage());
                    }
                }).subscribe(result -> {
                    updateUrls.add(result.raw().request().url().toString());
                    logger.info("complete:{}:{}", shipName,url);
                }, throwable -> {
                    errorUrls.add(url);
                    file.delete();
                    logger.error(throwable.getMessage(),throwable);
                });
            });
        });

        try {
            threadPoolExecutor.shutdown();
            threadPoolExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
//            while (!errorUrls.isEmpty() || !updateUrls.isEmpty()) {
//                Thread.sleep(1000);
//            }
            subscribe.dispose();
            logger.info("download complete.{}", headerList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 是否需要下载,true:需要下载
    private boolean checkFile(String url) {
        String path = Api.getPathFromURL(url);
        if (path == null) {
            return false;
        }
//        File file = new File(path);
//        if (file.exists() && file.length() > 0) {
//            return false;
//        }
        return true;
    }

    class Result {
        String url;
        Headers headers;
        ResponseBody responseBody;
        boolean successful = false;
        int count = 0;
    }
}
