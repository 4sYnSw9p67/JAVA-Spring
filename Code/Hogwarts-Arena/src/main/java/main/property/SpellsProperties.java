package main.property;

import lombok.Data;
import main.config.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@ConfigurationProperties
@PropertySource(value = "spells.yaml", factory = YamlPropertySourceFactory.class)
public class SpellsProperties {

    // TODO: Load spell properties here.
    // Hint: Make an inner public class here that matches the structure of spells.yaml file
}
