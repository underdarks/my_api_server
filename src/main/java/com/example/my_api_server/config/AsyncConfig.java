package com.example.my_api_server.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AsyncConfig {

    //i/o 바운드
    @Bean("ioExecutor")
    public ExecutorService ioExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }


    //스레드 개수는 막 넣는게 아닙니다.(Context SWitch Cost 비례)
    //그래서 논리적으로 계산을하여서 넣습니다.(조금 더 복잡합니다)
    //cpu 바운드
    @Bean("cpuExecutor")
    public ExecutorService cpuExecutor() {
        //cpu 코어 개수 확인(하이퍼스레딩)
        //인텔을 쓰면 코어 * 2
        //맥 인텔칩 (하이퍼 스레딩 ok), 애플 칩(하이퍼 스레딩 x)
        int coreCount = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(coreCount);
    }

}
