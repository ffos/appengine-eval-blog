package com.biswadahal.blog.models;

import java.net.URI;

import com.googlecode.objectify.impl.Path;
import com.googlecode.objectify.impl.translate.CreateContext;
import com.googlecode.objectify.impl.translate.LoadContext;
import com.googlecode.objectify.impl.translate.SaveContext;
import com.googlecode.objectify.impl.translate.SkipException;
import com.googlecode.objectify.impl.translate.TypeKey;
import com.googlecode.objectify.impl.translate.ValueTranslator;
import com.googlecode.objectify.impl.translate.ValueTranslatorFactory;

/**
 * Appengine stores URIs as Strings. This Objectify translator translates 
 * URIs to String and vice-versa
 */
public class URITranslatorFactory extends ValueTranslatorFactory<URI, String> {
	public URITranslatorFactory() {
		super(URI.class);
	}

	@Override
	protected ValueTranslator<URI, String> createValueTranslator(TypeKey<URI> typeKey, CreateContext ctx, Path path) {
		return new ValueTranslator<URI, String>(String.class) {
			@Override
			protected URI loadValue(String value, LoadContext ctx, Path path) throws SkipException {
				return URI.create(value);
			}

			@Override
			protected String saveValue(URI value, boolean index, SaveContext ctx, Path path) throws SkipException {
				return value.toString();
			}
		};
	}
}