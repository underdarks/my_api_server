package com.example.my_api_server.lock;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class Counter {

    private int count = 0; //공유영역값(Heap, 임계영역)


    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        int threadCount = 20000; //유저수
        Counter counter = new Counter();

        //스레드 생성
        //new Thread는 작업이 끝나면 jvm이 확인하고 os에게 반납합니다.
        //limit이 도달하기전에 반납이 되기때문에 이전과 같은 리소스 오류가 발생안합니다
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(counter::increaseCount); //스레드 연산
            thread.start(); //스레드 시작
            threads.add(thread); //스레드 그룹에 스레드 add
        }

        //스레드의 일이 다 끝날때까지 기다림
        threads.forEach(thread ->
        {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        log.info("기대값 : {}", threadCount);
        log.info("실제값 : {}", counter.getCount());
    }

    private void increaseCount() {
        count++;
    }

}
