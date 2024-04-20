package Intellect.com.Utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Component 
@EnableConfigurationProperties
@ConfigurationProperties(prefix= "app")
public class AppProperties {
	
	/*
	 * application.yml file variable name should same name as Map variable 
	 * Ex:message 
	 * app in entry point that configure yml file
	 * 
	 * @configuration or @Component 
	 */
	private Map<String,String>messages=new HashMap<>();

	public Map<String, String> getMessages() {
		return  messages;
	}

	public void setMessages(Map<String, String> messages) {
		this. messages = messages;
	}
	

}
