package dz.countries.entity;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 * Сервис доступа к информации о странах
 * @author dzevako
 */
public interface CountriesRepository extends CrudRepository<Country, Long>
{
    Country getByNameIgnoreCase(String name);
    
    Country getByCodeIgnoreCase(String code);
    
    List<Country> findByNameContainingIgnoreCase(String query);
}
