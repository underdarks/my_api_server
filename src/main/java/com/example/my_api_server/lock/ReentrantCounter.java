package com.example.my_api_server.lock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ReentrantCounter {

    private final ReentrantLock lock = new ReentrantLock();

    private int count = 0; //공유영역값(Heap, 임계영역)


    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        int threadCount = 10; //유저수
        ReentrantCounter counter = new ReentrantCounter();

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

    private void increaseCount() {
        this.lock.lock();
//        while (true) {
//            try {
//                if (this.lock.tryLock(3, TimeUnit.SECONDS)) {
//                    try {
//                        log.info("락 획득 성공!");
//                        this.count++;
//                        Thread.sleep(4000);
//                        break;
//                    } finally {
//                        this.lock.unlock();
//                    }
//                } else {
//                    log.info("락 획득 실패 → 재시도");
//                }
//            } catch (InterruptedException e) {
//                log.info("작업 중단");
//                Thread.currentThread().interrupt();
//                break;
//            }
//
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                break;
//            }

        //3초 정도 락을 획득하는데 시도
        try {
            if (this.lock.tryLock(3, TimeUnit.SECONDS)) { //3초 정도 기다리겟다
                try {
                    log.info("락 획득 후 연산 작업 시작!");
                    this.count++; //스레드가 연산
                    Thread.sleep(4000);
                } finally {
                    this.lock.unlock(); //락 반환(개발자가 원하는 시점에 락을 획득/반납을 제어 가능)
                }
            } else {
                //3초안에 락 획득 못함
                log.info("3초안에 락 획득을 못함");
            }
        } catch (InterruptedException e) {
            log.info("작업 중단");
            throw new RuntimeException(e);
        }


    }

}
