package com.shadego.kcs.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shadego.kcs.entity.ShipMapping;
import com.shadego.kcs.util.Constant;
import com.shadego.kcs.util.FileUtil;
import com.shadego.kcs.util.GlobalConst;
import com.shadego.kcs.util.RetrofitFactory;

/**
 * @author Maclaine E-mail:deathencyclopedia@gmail.com
 * 
 */
public class Api {

	static class JsonPack {
		JSONObject json;
	}

	public static JSONObject getFromNet(String token) {
		JsonPack json = new JsonPack();
		RetrofitFactory.getInstance().getApiService().post("1", token).subscribe(response -> {
			String result = response.body().string().replace("svdata=", "");
			json.json = JSONObject.parseObject(result);
			result=json.json.toJSONString();
			FileUtil.write("api.json", result);
		}, Throwable::printStackTrace);
		return json.json;
	}

	public static JSONObject readFromFile() throws IOException {
		File file = new File("api.json");
		if (!file.exists()) {
			return null;
		}
		try (FileInputStream fs = new FileInputStream(file); FileChannel fc = fs.getChannel();) {
			CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
			ByteBuffer bb = ByteBuffer.allocate((int) fc.size());
			StringBuffer sb = new StringBuffer();
			while (fc.read(bb) != -1) {
				bb.flip();
				while (bb.hasRemaining()) {
					sb.append(decoder.decode(bb));
				}
				bb.clear();
			}
			JSONObject json = JSONObject.parseObject(sb.toString());
			return json;
		}
	}

	public static JSONObject readFromFileEx() throws IOException {
		File file = new File("api.json");
		if (!file.exists()) {
			return null;
		}
		try (FileInputStream fs = new FileInputStream(file); FileChannel fc = fs.getChannel();) {
			StringBuffer sb = new StringBuffer();
			ByteBuffer bb = ByteBuffer.allocate(1024);
			byte[] lineByte = new byte[0];
			byte[] temp = new byte[0];
			String encode = "UTF-8";

			while (fc.read(bb) != -1) {// fcin.read(rBuffer)：从文件管道读取内容到缓冲区(rBuffer)
				int rSize = bb.position();// 读取结束后的位置，相当于读取的长度
				byte[] bs = new byte[rSize];// 用来存放读取的内容的数组
				bb.rewind();// 将position设回0,所以你可以重读Buffer中的所有数据,此处如果不设置,无法使用下面的get方法
				bb.get(bs);// 相当于rBuffer.get(bs,0,bs.length())：从position初始位置开始相对读,读bs.length个byte,并写入bs[0]到bs[bs.length-1]的区域
				bb.clear();

				int startNum = 0;
				int LF = 10;// 换行符
				int CR = 13;// 回车符
				boolean hasLF = false;// 是否有换行符
				for (int i = 0; i < rSize; i++) {
					if (bs[i] == LF) {
						hasLF = true;
						int tempNum = temp.length;
						int lineNum = i - startNum;
						lineByte = new byte[tempNum + lineNum];// 数组大小已经去掉换行符

						System.arraycopy(temp, 0, lineByte, 0, tempNum);// 填充了lineByte[0]~lineByte[tempNum-1]
						temp = new byte[0];
						System.arraycopy(bs, startNum, lineByte, tempNum, lineNum);// 填充lineByte[tempNum]~lineByte[tempNum+lineNum-1]

						String line = new String(lineByte, 0, lineByte.length, encode);// 一行完整的字符串(过滤了换行和回车)
						System.out.println(line);
						// 过滤回车符和换行符
						if (i + 1 < rSize && bs[i + 1] == CR) {
							startNum = i + 2;
						} else {
							startNum = i + 1;
						}

					}
				}
				if (hasLF) {
					temp = new byte[bs.length - startNum];
					System.arraycopy(bs, startNum, temp, 0, temp.length);
				} else {// 兼容单次读取的内容不足一行的情况
					byte[] toTemp = new byte[temp.length + bs.length];
					System.arraycopy(temp, 0, toTemp, 0, temp.length);
					System.arraycopy(bs, 0, toTemp, temp.length, bs.length);
					temp = toTemp;
				}
			}
			if (temp != null && temp.length > 0) {// 兼容文件最后一行没有换行的情况
				String line = new String(temp, 0, temp.length, encode);
				System.out.println(line);
			}
			JSONObject json = JSONObject.parseObject(sb.toString());
			return json;
		}
	}

	public static Map<Integer, ShipMapping> initShipMap(JSONObject json) {
		Map<Integer, ShipMapping> shipMap = new HashMap<>();
		JSONArray jsonShipArray = json.getJSONObject("api_data").getJSONArray("api_mst_ship");
		jsonShipArray.forEach(obj -> {
			JSONObject jo = (JSONObject) obj;
			ShipMapping ship = new ShipMapping();
			ship.setId(jo.getInteger("api_id"));
			ship.setName(jo.getString("api_name"));
			ship.setSortno(jo.getInteger("api_sortno"));
			ship.setAftershipid(jo.getInteger("api_aftershipid"));
			ship.setVoicef(jo.getInteger("api_voicef"));
			if (ship.getVoicef() != null) {
				ship.setSoundFileNames(Voice.convertFileName(ship.getId(), ship.getVoicef()));
			}
			shipMap.put(ship.getId(), ship);
		});
		JSONArray jsonFileArray = json.getJSONObject("api_data").getJSONArray("api_mst_shipgraph");
		jsonFileArray.forEach(obj -> {
			JSONObject jo = (JSONObject) obj;
			ShipMapping ship = shipMap.get(jo.getInteger("api_id"));
			if (ship == null) {
			    return;
//				ship = new ShipMapping();
//				ship.setId(jo.getInteger("api_id"));
//				ship.setName("unknow");
//				ship.setSortno(jo.getInteger("api_sortno"));
				
			}
			ship.setFilename(jo.getString("api_filename"));
			ship.setVersion(jo.getString("api_version").split(","));
			shipMap.put(ship.getId(), ship);
		});
		return shipMap;
	}
	
	public static  Map<Integer, ShipMapping> initUpgradeShipMap(JSONObject json){
	    Map<Integer, ShipMapping> shipMap = new HashMap<>();
	    JSONArray jsonShipArray = json.getJSONObject("api_data").getJSONArray("api_mst_ship");
	    JSONArray jsonUpgradeShipArray = json.getJSONObject("api_data").getJSONArray("api_mst_shipupgrade");
	    jsonUpgradeShipArray.forEach(obj -> {
            JSONObject jo = (JSONObject) obj;
            ShipMapping ship = new ShipMapping();
            shipMap.put(jo.getInteger("api_id"), ship);
        });
	    
	    jsonShipArray.forEach(obj -> {
	        JSONObject jo = (JSONObject) obj;
	        ShipMapping ship = shipMap.get(jo.getInteger("api_id"));
            if (ship == null) {
                return;
            }
            ship.setId(jo.getInteger("api_id"));
            ship.setName(jo.getString("api_name"));
            ship.setSortno(jo.getInteger("api_sortno"));
            ship.setAftershipid(jo.getInteger("api_aftershipid"));
            ship.setVoicef(jo.getInteger("api_voicef"));
            if (ship.getVoicef() != null) {
                ship.setSoundFileNames(Voice.convertFileName(ship.getId(), ship.getVoicef()));
            }
            shipMap.put(ship.getId(), ship);
	    });
	    
	    JSONArray jsonFileArray = json.getJSONObject("api_data").getJSONArray("api_mst_shipgraph");
        jsonFileArray.forEach(obj -> {
            JSONObject jo = (JSONObject) obj;
            ShipMapping ship = shipMap.get(jo.getInteger("api_id"));
            if (ship == null) {
                return;
            }
            ship.setFilename(jo.getString("api_filename"));
            ship.setVersion(jo.getString("api_version").split(","));
            shipMap.put(ship.getId(), ship);
        });
	    
	    return shipMap;
	}
	
	public static String getPathFromURL(String url){
		String path=null;
		if(url.contains(Constant.SHIP_URL)){
			path=GlobalConst.filePath+url.substring(url.indexOf(Constant.SHIP_URL), url.length());
		}else if(url.contains(Constant.SOUND_URL)){
			path=GlobalConst.filePath+url.substring(url.indexOf(Constant.SOUND_URL), url.length());
		}
		return path;
	}
	
	public static String getShipNameByPath(Collection<ShipMapping> sets,String path) {
	    String shipName="unknow";
	    try {
	        if(path.contains(Constant.SHIP_URL)){
	            shipName=getByFileName(sets,path.substring(path.lastIndexOf("/")+1,path.length()-4)).getName();
	        }else if(path.contains(Constant.SOUND_URL)){
	            String temp=path.substring(0,path.lastIndexOf("/"));
	            shipName=getByFileName(sets,temp.substring(temp.lastIndexOf("/")+3)).getName()+"(voice)";
	        }
        } catch (Exception e) {
//            System.out.println(path);
        }
	    return shipName;
	}
	
	public static ShipMapping getByFileName(Collection<ShipMapping> sets,String fileName) {
	    List<ShipMapping> list=new ArrayList<>();
	    sets.forEach(ship->{
	        if(ship.getFilename().equalsIgnoreCase(fileName)) {
	            list.add(ship);
	        }
	    });
	    if(list.isEmpty()) {
	        list.add(null);
	    }
	    return list.get(0);
	}
	
	public static ShipMapping getByShipName(Collection<ShipMapping> sets,String shipName) {
        List<ShipMapping> list=new ArrayList<>();
        sets.forEach(ship->{
            if(ship.getName().equalsIgnoreCase(shipName)) {
                list.add(ship);
            }
        });
        if(list.isEmpty()) {
            list.add(null);
        }
        return list.get(0);
    }
}
