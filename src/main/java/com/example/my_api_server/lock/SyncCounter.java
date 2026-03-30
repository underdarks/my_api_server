package com.example.my_api_server.lock;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class SyncCounter {

    private int count = 0; //공유영역값(Heap, 임계영역)


    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        int threadCount = 100; //유저수
        SyncCounter counter = new SyncCounter();

        //스레드 생성
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

    //메서드 단위의 sync 메서드 실행자체에 대해서 락을 얻어서 순서를 제어합니다.
    //스레드 1(락 획득, return 반납) -> 스레드 2(락 획득, return 반납)-> 스레드 3(락 획득, return 반납)
    private void increaseCount() {
        //스레드 1번이 들어오면서 락을 획득합니다.
        State state = Thread.currentThread().getState();
        log.info("state1 = {}", state.toString());

        //해당 범위만 락을 얻겟다!(스레드 1 -> 스레드 2 -> 스레드 3)
        synchronized (this) { //락으로 순서 제어
            log.info("state2(락을 얻는 부분) = {}", state.toString());
            count++; //연산..
        }
        //스레드 1번 락을 반환합니다.
        log.info("state3 = {}", state.toString());
    }

}
