package com.e4developer.foodorderconsumer;

import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.GenericMessage;

import com.rabbitmq.client.Channel;

@EnableBinding(Sink.class)
@SpringBootApplication
public class FoodOrderConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodOrderConsumerApplication.class, args);
    }

    @StreamListener(target = Sink.INPUT)
    public void handlerMethod(
            @Payload GenericMessage<String> message,
            @Header(AmqpHeaders.CHANNEL) Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag) throws Exception {
        try {
            processMessage(message);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, true);
        }
    }

    private void processMessage(@Payload GenericMessage<String> message) throws Exception {
        String meal = message.getPayload();
        if (meal.contains("vegetables"))
            throw new Exception("Vegetables! Move to dead letter queue!");
        if (meal.contains("poison"))
            throw new Exception("Poison! Move to dead letter queue!");
        System.out.println("Meal consumed: " + meal);
    }

}
