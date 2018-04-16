package com.shadego.kcs;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.shadego.kcs.data.Voice;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Maclaine E-mail:deathencyclopedia@gmail.com
 * 
 */
public class FileTest {

	public static void main(String[] args) throws IOException, ParseException, InterruptedException {
		/*
		 * SimpleDateFormat header=new
		 * SimpleDateFormat("EEE,dd MMM yyyy HH:mm:ss Z",Locale.US);
		 * SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 * Date date=new Date(); date.setTime(1488271530000L);
		 * System.out.println(sdf.format(date)); String
		 * headerstr="Tue, 28 Feb 2017 08:44:31 GMT"; Date parse =
		 * header.parse(headerstr); System.out.println(parse.getTime());
		 * System.out.println(sdf.format(parse));
		 */
		ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(4);
		List<Integer> list = new ArrayList<>();
		List<Flowable<Integer>> flist = new ArrayList<>();
		for (int i = 0; i < 22; i++) {
			flist.add(Flowable.just(i));
		}
		for (Flowable<Integer> flowable : flist) {
			threadPoolExecutor.execute(() -> {
				flowable.subscribe(value -> {
					System.out.println("flow"+value);
					if (value % 2 == 0) {
						list.add(value);
					}
					Thread.sleep(1000);
				});
			});
		}
		Reading reading=new Reading();
		reading.isReading=false;
		Disposable subscribe = Observable.interval(500, TimeUnit.MILLISECONDS)
		.subscribe(v -> {
			System.out.println("ob"+v);
			synchronized(reading){
				if(reading.isReading){
					return;
				}
				if(list.isEmpty()){
					return;
				}
				reading.isReading=true;
				RandomAccessFile randomFile = new RandomAccessFile("errorlog.txt", "rw");
				long fileLength = randomFile.length();
				randomFile.seek(fileLength);
				randomFile.writeBytes(String.valueOf(list.get(0))+"\r\n");
				randomFile.close();
				list.remove(0);
				reading.isReading=false;
			}
			
		});
		
		threadPoolExecutor.shutdown();  
		threadPoolExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
		/*while(!threadPoolExecutor.isTerminated()||!list.isEmpty()){
			System.out.println("doing");
			Thread.sleep(1000);
		}*/
		System.out.println("finished");
		subscribe.dispose();
	}
	
	static class Reading{
		boolean isReading=false;
	}
}
