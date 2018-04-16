package com.shadego.kcs;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

import com.alibaba.fastjson.JSONObject;
import com.shadego.kcs.data.Api;
import com.shadego.kcs.data.Download;
import com.shadego.kcs.entity.ShipMapping;
import com.shadego.kcs.util.GlobalConst;

/**
 * @author Maclaine E-mail:deathencyclopedia@gmail.com
 * 
 */
public class Main {

    public static void main(String[] args) {
        try {

            Scanner scanner = new Scanner(System.in);
            start(scanner);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void start(Scanner scanner) throws IOException {
        System.out.println("0:更新api\n1:正常下载(会与服务器比对)\n2:仅下载更新列表\n10:船名/文件名查询模式\n11:查询更新列表");
        System.out.println("Input start mode:");
        try {
            Integer mode = Integer.parseInt(scanner.nextLine());
            switch (mode) {
            case 0: {
                JSONObject fromNet = Api.getFromNet(GlobalConst.token);
                System.out.println(fromNet.size());
                break;
            }
            case 1: {
                JSONObject json = Api.readFromFile();
                Map<Integer, ShipMapping> shipMap = Api.initShipMap(json);
                Download download = new Download();
                download.download(shipMap);
                break;
            }
            case 2: {
                JSONObject json = Api.readFromFile();
                Map<Integer, ShipMapping> shipMap = Api.initUpgradeShipMap(json);
                Download download = new Download();
                download.download(shipMap);
                break;
            }
            case 10: {
                JSONObject json = Api.readFromFile();
                Map<Integer, ShipMapping> shipMap = Api.initShipMap(json);
                System.out.println("ships total:" + shipMap.size());
                while (true) {
                    System.out.println("Input shipName or fileName (:q to exit)");
                    String fileName = scanner.nextLine();
                    if (fileName.equals(":q")) {
                        break;
                    }
                    ShipMapping ship = Api.getByFileName(shipMap.values(), fileName);
                    if (ship != null) {
                        System.out.println(ship.getName());
                    } else {
                        ship = Api.getByShipName(shipMap.values(), fileName);
                        if (ship != null) {
                            System.out.println(ship.getFilename());
                        } else {
                            System.out.println("No data found");
                        }
                    }
                }
                break;
            }
            case 11: {
                JSONObject json = Api.readFromFile();
                Map<Integer, ShipMapping> shipMap = Api.initUpgradeShipMap(json);
                System.out.println("upgrade total:" + shipMap.size());
                shipMap.forEach((key, value) -> {
                    System.out.println(
                            key + ":" + value.getName() + ",version:" + Arrays.asList(value.getVersion()).toString());
                });
                break;
            }
            default:
                throw new Exception();
            }
        } catch (Exception e) {
            System.out.println("Eixt");
            System.exit(1);
        }

        System.out.println("-------------------------------------------------------------------------------------------------");
        start(scanner);
    }
}
