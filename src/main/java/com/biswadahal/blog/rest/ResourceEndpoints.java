package com.biswadahal.blog.rest;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class ResourceEndpoints {
	public static class Endpoints {
		private String all;
		private String byId;

		Endpoints(String all, String byId) {
			this.all = all;
			this.byId = byId;
		}

		public String getAll() {
			return all;
		}

		public String getById() {
			return byId;
		}
	}
	public enum EndpointKeys {
		ASSET("Asset"),
		PAGE("Page"),
		PAGE_TEMPLATE("PageTemplate"),
		TAG("Tag"),
		INVALID_KEY("InvalidKey");
		
		private String key;
		private EndpointKeys(String key){
			this.key = key;
		}
		public String getKey() {
			return key;
		}
	}

	/**
	 * key = Kind (of datastore Key)
	 * value = Endpoints type
	 */
	private static final Map<String, Endpoints> endpoints = new HashMap<>();

	private ResourceEndpoints() {
		endpoints.put(EndpointKeys.ASSET.key, endpoints("assets"));
		endpoints.put(EndpointKeys.PAGE.key, endpoints("pages"));
		endpoints.put(EndpointKeys.PAGE_TEMPLATE.key, endpoints("ptemplates"));
		endpoints.put(EndpointKeys.TAG.key, endpoints("tags"));
		endpoints.put(EndpointKeys.INVALID_KEY.key, endpoints("-invalid-key-"));
	}

	public static ResourceEndpoints get() {
		return new ResourceEndpoints();
	}

	private Endpoints endpoints(final String resource) {
		return new Endpoints(pathForAll(resource), pathForById(resource));
	}

	private String pathForAll(final String resource) {
		return String.format("%s%s", BlogRestAPI.appConfiguration.basePath(), resource);
	}

	private String pathForById(final String resource) {
		return String.format("%s/%s", pathForAll(resource), "%s");
	}

	public Optional<Endpoints> endpointForKind(String kind) {
		Preconditions.checkNotNull(kind);
		return Optional.fromNullable(endpoints.get(kind));
	}

	public static String getAll(EndpointKeys k) {
		Optional<Endpoints> eps = get().endpointForKind(k.key);
		return Optional.fromNullable(eps.get().getAll()).or("");
	}
	
	public static String getById(EndpointKeys k) {
		Optional<Endpoints> eps = get().endpointForKind(k.key);
		return Optional.fromNullable(eps.get().getById()).or("");
	}
	
}
