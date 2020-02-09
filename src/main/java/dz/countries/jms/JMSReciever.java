package dz.countries.jms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.fusesource.hawtbuf.ByteArrayInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import dz.countries.entity.CountriesRepository;
import dz.countries.entity.Country;

/**
 * Обработчик JMS-сообщений
 * @author dzevako
 */
@Component
@ConditionalOnProperty(name = "appType", havingValue = "2")
public class JMSReciever
{
    private static final Logger LOG = LoggerFactory.getLogger(JMSReciever.class);
    private CountriesRepository repo;
    private EntityManager entityManager;
    
    @Autowired
    public JMSReciever(CountriesRepository repo, EntityManager entityManager)
    {
        this.repo = repo;
        this.entityManager = entityManager;
    }

    @Transactional
    @JmsListener(destination = "q1")
    public void update(byte[] bytes)
    {
        LOG.info("Received message to update countries...");
        List<Country> countries = getCountriesFromBytes(bytes);
        repo.deleteAll();

        // Необходимо сохранить в базе состояние, иначе не добавить новые страны из-за уникальных колонок
        entityManager.flush(); 
        LOG.info("Countries deleted.");

        repo.saveAll(countries);
        LOG.info("Countries saved successfully.");
    }

    /**
     * Получение списка стран из массива байт
     * @param bytes массив байт
     */
    private List<Country> getCountriesFromBytes(byte[] bytes)
    {
        List<Country> countries = null;
        try (InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(bytes));
                BufferedReader br = new BufferedReader(isr);)
        {
            countries = br.lines()
                    .skip(1)
                    .map(line -> getCountryFromLine(line))
                    .collect(Collectors.toList());      
        }
        catch (IOException e)
        {
            LOG.error("Exception, while reading bytes: {}", e.getMessage());
        }
        catch (Exception e)
        {
            LOG.error("Exception: {}", e.getMessage());
        }
        return countries;
    }

    /**
     * Получение из строки объекта Country/
     * Метод позволяет обрабатывать строки,
     * в которых задано только название страны,
     * либо код и название (в порядке: код; название)
     * 
     * @param line строка для разбора
     */
    private Country getCountryFromLine(String line)
    {
        String[] parts = line.split(";");
        String name = parts.length < 2 ? parts[0] : parts[1];
        String code = parts.length < 2 ? null : parts[0];
        
        return new Country(strip(name), code);
    }
    
    /**
     * Убрать кавычки со строки
     */
    private String strip(String str)
    {
        return str.startsWith("\"") && str.endsWith("\"")
            ? str.substring(1, str.length() - 1) : str;
    }
}
