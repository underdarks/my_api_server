package com.example.my_api_server.lock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadExceptionTest {

    private int count = 0;

    public static void main(String[] args) {
        ThreadExceptionTest t = new ThreadExceptionTest();
        int threadCount = 100;
        //N개를 커널로부터 생성한뒤, 반납하지 x
        ExecutorService es = Executors.newFixedThreadPool(threadCount); //N개의 플랫폼 스레드 생성

        //: unable to create native thread: possibly out of memory or process/resource limits reached
        //네이티브 스레드를 생성할 수 없습니다. 메모리 부족 또는 프로세스/리소스 제한 초과 때문일 수 있습니다.
        //os는 자원(메모리, 스레드, 소켓 등등)을 프로세스마다 제한량(limit)이 있습니다.
        for (int i = 0; i < threadCount; i++) {
            es.submit(t::increase);
        }

        es.shutdown();

        System.out.println("실행완료!");
    }

    public void increase() {
        count++;
    }

}
