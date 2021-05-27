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
        ClientHandler clientHandler = new ClientHandler();
        Scanner scanner = new Scanner(System.in);
        Integer i = scanner.nextInt();
        if (i == 1) {
            System.out.println("Введите ID :");
            i = scanner.nextInt();
            System.out.println(clientHandler.templateRequest("http://localhost:8080/owners", i.longValue()));
        } else if (i == 2) {

        } else if (i == 3) {
        } else if (i == 4) {
        } else
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
