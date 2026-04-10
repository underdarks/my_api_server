package com.example.my_api_server.virtual_thread;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VTClass {

    static final int TASK_COUNT = 1000;

    static final Duration IO_DURATION = Duration.ofSeconds(1); //i/o 작업 시간

    public static void main(String[] args) {
        /*SseEmitter sseEmitter = new SseEmitter();
        sseEmitter.send("ni");*/
//        Thread vt1 = Thread.ofVirtual()
//            .name("가상스레드1")
//            .start(VTClass::ioRun);

        //i/o는 메모리를 조금 더쓰더라도 성능 자체가 극도하게 차이납니다.
//        log.info("[i/o]플랫폼 스레드 시작!");
//        ioRun(Executors.newFixedThreadPool(200)); //플랫폼 스레드 N개 생성(미리 만드는 방식)
//
//        log.info("[i/o]가상 스레드 시작!");
//        ioRun(Executors.newVirtualThreadPerTaskExecutor()); //가상 스레드 필요한개수만큼 생성(필요할떄 만드는 방식)

        // 가상스레드는 힙에 생성됩니다. 그로 인해서 JVM 힙 메모리가 많이 사용되고, GC가 더많은 일을 해야합니다(STW)
        // 결국에는 사용자가 많아질수록 cpu 연산의 차이는 크게 안나고 메모리많이 사용하게됩니다. 비효율적
//        log.info("[cpu]플랫폼 스레드 시작!");
//        cpuRun(Executors.newFixedThreadPool(200)); //플랫폼 스레드 N개 생성(미리 만드는 방식)
        //2MB * 200 = 400MB만 고정해서 씁니다

//        log.info("[cpu]가상 스레드 시작!");
//        cpuRun(Executors.newVirtualThreadPerTaskExecutor()); //가상 스레드 필요한개수만큼 생성(필요할떄 만드는 방식)
        //1KB * TASKCOUNT = 5000kb = 5mb

//        log.info("[io]플랫폼 스레드 피닝 테스트 시작!");
//        ioRunPinning(Executors.newFixedThreadPool(200));
//
//        log.info("[io]가상 스레드 피닝 테스트 시작!");
//        ioRunPinning(Executors.newVirtualThreadPerTaskExecutor());

        log.info("[io]플랫폼 스레드 피닝 테스트2 시작!");
        ioRunPinningRL(Executors.newFixedThreadPool(200));

        log.info("[io]가상 스레드 피닝 테스트2 시작!");
        ioRunPinningRL(Executors.newVirtualThreadPerTaskExecutor());
    }


    //플랫폼 스레드 vs 가상 스레드 실행 속도 측정 비교
    public static void ioRun(ExecutorService es) {
        Instant start = Instant.now(); //실행 시간 측정

        try (es) {
            IntStream.range(0, TASK_COUNT).forEach(idx -> {
                es.submit(() -> {
                    try {
                        //실제 외부 API 및 DB 연동 코드(i/o 발생!, i/o bound)
                        //가상 스레드는 i/o를 만나면 unmount되고 다른 가상스레드가 일을 할 수 있게 됨
                        Thread.sleep(IO_DURATION);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            });
        } //try-resource 자동으로 리소스해제(es.close())

        Instant end = Instant.now(); //실행 시간 측정
        System.out.printf("작업 완료 시간: %d ms%n", Duration.between(start, end).toMillis());
    }

    public static void cpuRun(ExecutorService es) {
        Instant start = Instant.now(); //실행 시간 측정

        try (es) {
            IntStream.range(0, TASK_COUNT).forEach(idx -> {
                es.submit(() -> {
                    // cpu 연산이 많다 (cpu bound)
                    for (int i = 0; i < 1000000; i++) {
                        int a = 1;
                        int b = 2;
                        int c = a + b;
                    }

                });
            });
        } //try-resource 자동으로 리소스해제(es.close())

        Instant end = Instant.now(); //실행 시간 측정
        System.out.printf("작업 완료 시간: %d ms%n", Duration.between(start, end).toMillis());
    }


    public static void ioRunPinning(ExecutorService es) {
        Instant start = Instant.now(); //실행 시간 측정

        try (es) {
            IntStream.range(0, TASK_COUNT).forEach(idx -> {
                es.submit(() -> {
                    //내부적으로 락을 사용한다고 가정
                    //synchronized 커널의 세마포어/뮤텍스 객체를 동시성을 제어, SystemCall
                    // -> 플랫폼스레드가 Blocked이 되고, 그러면 플랫폼 스레드 1번이 일을 못하게되닌깐 가상스레드도 일을 못하게됩니다.
                    //가상 스레드 장점 없어지게되고 비효율적으로 돌아가게됩니다.
                    synchronized (es) {
                        try {
                            //실제 외부 API 및 DB 연동 코드(i/o 발생!, i/o bound)
                            //가상 스레드는 i/o를 만나면 unmount되고 다른 가상스레드가 일을 할 수 있게 됨
                            Thread.sleep(IO_DURATION);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                });
            });
        } //try-resource 자동으로 리소스해제(es.close())

        Instant end = Instant.now(); //실행 시간 측정
        System.out.printf("작업 완료 시간: %d ms%n", Duration.between(start, end).toMillis());
    }

    //ReentrantLock 적용
    public static void ioRunPinningRL(ExecutorService es) {
        Instant start = Instant.now(); //실행 시간 측정

        try (es) {
            IntStream.range(0, TASK_COUNT).forEach(idx -> {
                es.submit(() -> {
                    ReentrantLock lock = new ReentrantLock();
                    lock.lock();
                    try {
                        //실제 외부 API 및 DB 연동 코드(i/o 발생!, i/o bound)
                        //가상 스레드는 i/o를 만나면 unmount되고 다른 가상스레드가 일을 할 수 있게 됨
                        Thread.sleep(IO_DURATION);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        lock.unlock();
                    }
                });
            });
        } //try-resource 자동으로 리소스해제(es.close())

        Instant end = Instant.now(); //실행 시간 측정
        System.out.printf("작업 완료 시간: %d ms%n", Duration.between(start, end).toMillis());
    }

}
