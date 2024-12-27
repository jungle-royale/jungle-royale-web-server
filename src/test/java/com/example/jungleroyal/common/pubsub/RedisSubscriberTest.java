package com.example.jungleroyal.common.pubsub;

import com.example.jungleroyal.JungleroyalApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = JungleroyalApplication.class)
public class RedisSubscriberTest {
    @MockBean
    private RedissonClient redissonClient; // 테스트를 위한 MockBean

    @Autowired
    private RedisSubscriber redisSubscriber;

    @Test
    public void testSubscribeToChannel(){
        RedissonClient redissonClient = mock(RedissonClient.class);
        RTopic topic = mock(RTopic.class);

        // Mock설정 : getTopic 호출 시 RTopic 반환
        when(redissonClient.getTopic("game-events")).thenReturn(topic);

        // RedisSubscriber
        RedisSubscriber subscriber = new RedisSubscriber(redissonClient, new ObjectMapper());

        // 메시지 리스너 등록 테스트
        doAnswer(invocation -> {
            // Mock 메시지 처리 로직 호출
            String channel = invocation.getArgument(0);
            String message = invocation.getArgument(1);
            System.out.println("Mock Received message = " + message);
            return null;
        }).when(topic).addListener(eq(String.class), any());

        // PostConstruct 메서드 호출
        subscriber.subscribeToChannel();

        // 메시지 발행 시뮬레이션
        topic.publish("Test message");

        // 검증: 메시지가 제대로 처리되었는지 확인
        verify(topic, times(1)).addListener(eq(String.class), any());
    }

}
