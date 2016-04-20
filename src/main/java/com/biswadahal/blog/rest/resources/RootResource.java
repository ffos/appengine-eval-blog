package com.biswadahal.blog.rest.resources;

import javax.inject.Inject;
import javax.ws.rs.Path;

import com.google.inject.servlet.RequestScoped;

@Path("")
@RequestScoped
public class RootResource {
	
	@Inject
	private TagsResource tagsResource;

	@Inject
	private PageTemplatesResource pageTemplateResource;

	@Inject
	private PagesResource pagesResource;
	
	@Inject
	private AssetsResource assetsResource;
	
	@Path("tags")
	public TagsResource tagsResource() {
		return tagsResource;
	}

	@Path("pages")
	public PagesResource pagesResource() {
		return pagesResource;
	}
	
	@Path("ptemplates")
	public PageTemplatesResource pageTemplateResource() {
		return pageTemplateResource;
	}
	
	@Path("assets")
	public AssetsResource assetsResource() {
		return assetsResource;
	}
}
