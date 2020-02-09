package dz.countries.rest;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import dz.countries.entity.CountriesRepository;

@RestController
@ConditionalOnProperty(name = "appType", havingValue = "1")
@RequestMapping("/countries")
public class CountriesRestController {

    private static final Logger LOG = LoggerFactory.getLogger(CountriesRestController.class);
    private JmsTemplate jmsTemplate;
    private CountriesRepository repo;
    
    @Autowired 
    public CountriesRestController(CountriesRepository repo, JmsTemplate jmsTemplate)
    {
        this.repo = repo;
        this.jmsTemplate = jmsTemplate;
    }
    
    /**
     * Многофункциональный метод выполняет поиск страны по
     * идентификатору, названию или коду
     * Также делает отфильтрованную выборку по строке query
     * @param id идентификатор
     * @param code код
     * @param name название
     * @param query строка поиска
     * @return
     */
    @GetMapping("")
    public Object countries(
            @RequestParam(value = "id", required = false) Long id, 
            @RequestParam(value = "code", required = false) String code, 
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "query", required = false) String query) 
    {
        Object[] params = new Object[] { id, code, name, query };
        long notNullParamsCount = Arrays.asList(params).stream().filter(t -> t != null).count();
        if (notNullParamsCount != 1)
        {
            throw new RuntimeException(
                    "Please, set only one param: id, code, name or query(for search by name).");
        }

        if (name != null)
        {
            return repo.getByNameIgnoreCase(name);
        }
        if (code != null)
        {
            return repo.getByCodeIgnoreCase(code);
        }
        if (id != null)
        {
            return repo.findById(id).get();
        }
        if (query != null)
        {
            return repo.findByNameContainingIgnoreCase(query);
        }
        throw new RuntimeException("Country is not found.");
    }
    
    /**
     * Обновление справочника со странами с помощью файла .csv
     * @param file
     */
    @PostMapping("/update")
    public void update(@RequestParam("file") MultipartFile file)
    {
        String fileName = file.getOriginalFilename();
        if (!fileName.endsWith(".csv"))
        {
            throw new RuntimeException("Please, use .csv file format to update countries list.");
        }
        
        byte[] bytes;
        try
        {
            bytes = file.getBytes();
        }
        catch (IOException e) 
        {
            throw new RuntimeException(String.format("Exception reading file '%s': %s", fileName, e.getMessage()));
        }

        LOG.info("Sending bytes of file '{}' with list of countries.. ", fileName);
        jmsTemplate.convertAndSend("q1", bytes);
    }
}
