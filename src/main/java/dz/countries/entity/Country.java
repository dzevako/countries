package dz.countries.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Country implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id 
    @GeneratedValue
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true)
    private String code;

    public Country(String name, String code)
    {
        this.name = name;
        this.code = code;     
    }

    Country()
    {
    }

    public String getCode()
    {
        return code;
    }

    public String getName()
    {
        return name;
    }
    
    public Long getId()
    {
        return id;
    }
    
    @Override
    public String toString()
    {
        return String.format("Country[name='%s', code='%s']", getName(), getCode()); 
    }
}
