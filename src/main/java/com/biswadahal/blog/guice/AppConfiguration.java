package com.biswadahal.blog.guice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class AppConfiguration {
	public enum ConfigKey {
		PATH_APP_BASE("path.app.base"),
		GCS_RETRY_DELAY_MILLIS("gcs.retry.delay.millis"),
		GCS_RETRY_PERIOD_MILLIS("gcs.retry.period.millis"),
		GCS_RETRY_MIN_ATTEMPTS("gcs.retry.min.attempts"),
		GCS_RETRY_MAX_ATTEMPTS("gcs.retry.max.attempts"),
		GCS_SIZE_BUFFER_UPLOAD_TWO_MB("gcs.size.buffer.upload.two.mb"),
		GCS_BUCKET_DEFAULT_NAME("gcs.bucket.default.name");
		
		String propKey;
		private ConfigKey(String propKey){
			this.propKey = propKey;
		}
	}
	
	public static final Logger logger = LoggerFactory.getLogger(AppConfiguration.class);
	public static final String APP_CONFIG_PROPERTY_FILE = "/AppConfiguration.properties"; 
	private final Properties properties;
	private final Map<ConfigKey, Config> configs = new HashMap<>();
	private final Map<String, ConfigKey> enumReverseIndex = new HashMap<>();
	
	
	public AppConfiguration(){
		properties = new Properties();
		generateEnumReverseIndex();
		loadConfigs();
	}
	
	private void generateEnumReverseIndex(){
		for(ConfigKey k: ConfigKey.values()){
			enumReverseIndex.put(k.propKey, k);
		}
	}
	
	private void loadConfigs(){
		try {
			properties.load(AppConfiguration.class.getResourceAsStream(APP_CONFIG_PROPERTY_FILE));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		List<String> unsupportedProperties = new ArrayList<>();
		for(String propName: properties.stringPropertyNames()){
			ConfigKey key = enumReverseIndex.get(propName);
			if(key == null){
				unsupportedProperties.add(propName);
				continue;
			}
			Config config = new Config(key, properties.getProperty(propName));
			configs.put(key, config);
		}
		if(!unsupportedProperties.isEmpty()){
			logger.warn(String.format("Unsupported properties found: %s", unsupportedProperties));
		}
	}
	
	public String basePath(){
		Optional<String> basePath= getStringValue(ConfigKey.PATH_APP_BASE, null);
		if( basePath.isPresent()){
			return basePath.get();
		}else{
			logger.error(String.format("Missing configuration: %s", ConfigKey.PATH_APP_BASE.propKey));
			throw new IllegalStateException("App is not correctly configuredO");
		}
	}

	
	public Optional<String> getStringValue(ConfigKey key, String defaultValueIfMissing) {
		return configs.containsKey(key)? configs.get(key).getStringValue(defaultValueIfMissing): Optional.fromNullable(defaultValueIfMissing);
	}
	
	public Optional<Long> getLongValue(ConfigKey key, long defaultValueIfMissing) {
		return configs.containsKey(key)? configs.get(key).getLongValue(defaultValueIfMissing): Optional.fromNullable(defaultValueIfMissing);
	}
	
	public Optional<Integer> getIntValue(ConfigKey key, int defaultValueIfMissing) {
		return configs.containsKey(key)? configs.get(key).getIntValue(defaultValueIfMissing): Optional.fromNullable(defaultValueIfMissing);
	}

	/**
	 * Property Wrapper
	 */
	public static final class Config{
		private final ConfigKey key;
		private final Optional<String> value;
		
		public Config(ConfigKey key, String value){
			Preconditions.checkNotNull(key);
			this.key = key;
			this.value = Optional.fromNullable(value);
		}

		public ConfigKey getKey() {
			return key;
		}

		public Optional<String> getStringValue(String defaultValueIfMissing) {
			return value.isPresent()? value: Optional.fromNullable(defaultValueIfMissing);
		}
		
		public Optional<Long> getLongValue(long defaultValueIfMissing) {
			return Optional.fromNullable(NumberUtils.toLong(value.orNull(), defaultValueIfMissing));
		}
		
		public Optional<Integer> getIntValue(int defaultValueIfMissing) {
			return Optional.fromNullable(NumberUtils.toInt(value.orNull(), defaultValueIfMissing));
		}
	}
	

}
