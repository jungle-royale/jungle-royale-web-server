package com.example.jungleroyal.service;

import com.example.jungleroyal.common.pubsub.RedisPublisher;
import com.example.jungleroyal.common.pubsub.RedisSubscribeListener;
import com.example.jungleroyal.domain.dto.MessageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class RedisPubServiceTest {
    @Mock
    private RedisMessageListenerContainer redisMessageListenerContainer;

    @Mock
    private RedisPublisher redisPublisher;

    @Mock
    private RedisSubscribeListener redisSubscribeListener;

    private RedisPubService redisPubService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        redisPubService = new RedisPubService(redisMessageListenerContainer, redisPublisher, redisSubscribeListener);
    }

    @Test
    void 채널에_메시지를_구독하고_발행한다() {
        // given
        String channel = "testChannel";
        MessageDto message = new MessageDto("testRoomId", "testUser", "testContent");

        ArgumentCaptor<ChannelTopic> channelTopicCaptor = ArgumentCaptor.forClass(ChannelTopic.class);
        ArgumentCaptor<MessageDto> messageDtoCaptor = ArgumentCaptor.forClass(MessageDto.class);

        // when
        redisPubService.pubMsgChannel(channel, message);

        // then
        verify(redisMessageListenerContainer, times(1))
                .addMessageListener(eq(redisSubscribeListener), channelTopicCaptor.capture());
        verify(redisPublisher, times(1))
                .publish(channelTopicCaptor.capture(), messageDtoCaptor.capture());

        assertEquals(channel, channelTopicCaptor.getAllValues().get(0).getTopic());
        assertEquals(message, messageDtoCaptor.getValue());
    }

    @Test
    void 메시지_발행_도중_예외가_발생하면_예외를_던진다() {
        // given
        String channel = "testChannel";
        MessageDto message = new MessageDto("testRoomId", "testUser", "testContent");

        doThrow(new RuntimeException("발행 오류"))
                .when(redisPublisher).publish(any(ChannelTopic.class), any(MessageDto.class));

        // when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            redisPubService.pubMsgChannel(channel, message);
        });

        // then
        assertEquals("발행 오류", exception.getMessage());
        verify(redisMessageListenerContainer, times(1))
                .addMessageListener(any(RedisSubscribeListener.class), any(ChannelTopic.class));
    }

    @Test
    void 잘못된_채널로_구독시_예외를_던진다() {
        // given
        String invalidChannel = null; // or invalid format
        MessageDto message = new MessageDto("testRoomId", "testUser", "testContent");

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            redisPubService.pubMsgChannel(invalidChannel, message);
        });

        // then
        assertEquals("Channel name must not be null", exception.getMessage());
        verify(redisMessageListenerContainer, never())
                .addMessageListener(any(RedisSubscribeListener.class), any(ChannelTopic.class));
        verify(redisPublisher, never())
                .publish(any(ChannelTopic.class), any(MessageDto.class));
    }
}
