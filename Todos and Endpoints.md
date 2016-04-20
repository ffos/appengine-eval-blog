# TODOs and Endpoints:

## TODO
* Create admin screens to create content
* Refactor services to use BlogUser as param for ACL
* Throw 400 Error in json format when JsonParseException error page shown as JsonMappingException as text/html
* Attach "Where Used" information to track which entity use assets
* Provide custom pages for 500, ..and other codes
* Analyze performance and start caching
* User cursor to page through pages
* Write view tests
* Update javadoc where necessary to provide more details
* Enforce HTTPS everywhere


* ✅GET/PUT endpoints for assets, PUT for asset/stream
* ✅PUT endpoints for page, pageTemplate and tags
* ✅Custom validator to ensure a given key actually exists in datastore
* ✅LastModifiedTime should be saved in UTC time (or any other internally created date)
* ✅Create Validation groups - for edit/create/delete (e.g. keys can be null during create, but should not be null during edit/delete)
* ✅Serialize/Deserialize all dates/times in ISO format. 400 else.
* ✅Send URI's instead of keys in payloads
* ✅Create endpoints builder
* ✅Use endpoints builder to set correct URI in 'Location' header
* ✅Custom validator to check length of datastore 'Text' type
* ✅Externalize configs: GCS - DEFAULT_BUCKET_NAME, GCS - BUFFER_SIZE, APP - BASE PATH ... etc
* ✅Use Mustache Template to return page html if accept = text/html


## Public:


```
  GET /
    Returns PUBLISHED main page  
  ✅GET /pages
    Returns PUBLISHED pages for the blog
  ✅GET /pages/1
    Returns PUBLISHED pages for key=1
  ✅GET /assets/1
    Returns a PUBLISHED asset for key=1
  ✅GET /assets/1/stream
    Streams a specific asset for key=1 with content type set to what the asset has
  ✅GET /tags
    Returns all tags (tag doesn't have meta, so no PUBLISHED status)
  ✅GET /tags/1
    Returns tag for key=1  (tag doesn't have meta, so no PUBLISHED status)
```



## Admin users:

```   
  Landing Page:  
    GET /
      Returns main page if exists
    PUT /blog
      update the main page for the blog

  Page Templates:  
    ✅GET /ptemplates
      Returns all page templates
    ✅POST /ptemplates
      Create a new page template
    ✅GET /ptemplates/1
      Returns a specific page template for key=1
    ✅PUT /ptemplates/1
      Update an existing page template for key=1

  Pages:  
    ✅GET /pages
      Returns all pages for the blog
    ✅POST /pages
      Create a new page
    ✅GET /pages/1
      Returns a specific page for key=1
    ✅PUT /pages/1
      Update an existing page for key=1

  Assets:  
    ✅GET /assets
      Returns a list of all assets
    ✅POST /assets
      Create a new asset
    ✅GET /assets/1
      Returns a specific asset for key=1
    ✅GET /assets/1/stream
      Streams a specific asset for key=1 with content type set to what the asset has
    ✅PUT /assets/1
      Updates a specific asset for key=1
    ✅PUT /assets/1/stream
      Updates the blob of specific asset for key=1


  Tags:
    ✅GET /tags
      Returns all tags
    ✅POST /tags
      Create a new tag
    ✅GET /tags/1
      Returns a specific tag for key=1
    ✅PUT /tags/1
      Updates a specific tag for key=1
```
