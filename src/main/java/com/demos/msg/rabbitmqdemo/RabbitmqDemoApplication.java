package com.demos.msg.rabbitmqdemo;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.demos.msg.rabbitmqdemo.receiver.Receiver;


//Spring AMQP requires that the Queue, the TopicExchange, 
//and the Binding be declared as top-level Spring beans in 
//order to be set up properly.

@SpringBootApplication
public class RabbitmqDemoApplication {
	
	  private final static String topicExchangeName = "spring-boot-exchange";

	  static final String queueName = "spring-boot";

//	  The queue() method creates an AMQP queue. 
//	  The exchange() method creates a topic exchange. 
	  @Bean
	  Queue queue() {
	    return new Queue(queueName, false);
	  }

	  @Bean
	  TopicExchange exchange() {
	    return new TopicExchange(getTopicexchangename());
	  }

//	  The binding() method binds these two together, defining 
//	  the behavior that occurs when RabbitTemplate publishes 
//	  to an exchange.
//	  n this case, we use a topic exchange, and the queue is 
//	  bound with a routing key of foo.bar.#, which means that 
//	  any messages sent with a routing key that begins with 
//	  foo.bar. are routed to the queue.
	  @Bean
	  Binding binding(Queue queue, TopicExchange exchange) {
	    return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
	  }

	  @Bean
	  SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
	      MessageListenerAdapter listenerAdapter) {
	    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
	    container.setConnectionFactory(connectionFactory);
	    container.setQueueNames(queueName);
	    container.setMessageListener(listenerAdapter);
	    return container;
	  }
	  
	  /*
	   *   The bean defined in the listenerAdapter() 
	   *   method is registered as a message listener in 
	   *   the container (defined in container()). It listens 
	   *   for messages on the spring-boot queue. Because the 
	   *   Receiver class is a POJO, it needs to be wrapped in 
	   *   the MessageListenerAdapter, where you specify that 
	   *   it invokes receiveMessage.
	   */

	  @Bean
	  MessageListenerAdapter listenerAdapter(Receiver receiver) {
	    return new MessageListenerAdapter(receiver, "receiveMessage");
	  }

	  /*
	   * The main() method starts that process by creating a 
	   * Spring application context. 
	   * 
	   * This starts the message 
	   * listener container, which starts listening for messages. 
	   * There is a Runner bean, which is then 
	   * automatically run. It retrieves the RabbitTemplate 
	   * from the application context and sends a Hello from 
	   * RabbitMQ! message on the spring-boot queue. Finally, 
	   * it closes the Spring application context, and the 
	   * application ends.
	   */
	public static void main(String[] args) {
		SpringApplication.run(RabbitmqDemoApplication.class, args);
	}

	public static String getTopicexchangename() {
		return getTopicExchangeName();
	}

	public static String getTopicExchangeName() {
		return topicExchangeName;
	}

}

