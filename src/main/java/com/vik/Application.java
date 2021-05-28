package com.vik;

import com.vik.client.ClientHandler;
import com.vik.server.ApplicationServer;
import com.vik.service.DBConnection;
import com.vik.service.InitType;

import java.io.IOException;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) throws IOException {
        DBConnection.init(InitType.DEV);
        ApplicationServer testServer = new ApplicationServer();
        testServer.init(InitType.DEV);
    }

    public static void application() throws IOException {
        Scanner scanner = new Scanner(System.in);
        Integer i = scanner.nextInt();
        if (i == 1) {
            return;
        }
        return;
    }

    private static void printMenu() {
        System.out.println("1. Получить пользователя по ID");
        System.out.println("2. Получить список карт пользователя по ID");
        System.out.println("3. Баланс карты по ID аккаунта");
        System.out.println("4. Пополнить баланс по ID аккаунта");
        System.out.println("5. Выпустить карту по ID аккаунта");
    }
}
