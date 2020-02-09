package dz.countries;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Сервис для хранения информации о странах
 * и получении информации о них через REST-API
 * 
 * Варианты запуска:
 * 
 * args[0] = 1: Приложение запускается как клиент и предоставляет REST-API для получения
 * информации о странах и возможность обновить список стран. Без модификации БД
 * 
 * args[0] = 2: Приложение запускается как приемник JMS сообщений на обновление списка стран.
 * Приложение не имеет REST-API. Может модифицировать БД
 * 
 * @author dzevako
 *
 */
@SpringBootApplication
public class CountriesApplication
{
    private static final Logger LOG = LoggerFactory.getLogger(CountriesApplication.class);
    
    public static void main(String[] args)
    {
        String appType = args.length == 0 ? "1" : args[0];
        LOG.info("Starting CountriesApplication {} ...", appType);
        System.setProperty("appType", appType);
        SpringApplication.run(CountriesApplication.class, args);
    }
    
    /**
     * Обход политики безопасности ActiveMQ
     * http://activemq.apache.org/objectmessage.html
     */
    @Bean
    public ConnectionFactory jmsConnectionFactory()
    {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setTrustAllPackages(true);
        return factory;
    }
}
