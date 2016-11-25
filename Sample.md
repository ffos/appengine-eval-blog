# API Calls
All calls are server-side validated. Some calls are public (no sign-in required). Others required administrative login (only google login supported)

## CRUD
The following examples are for different entities (to show different examples)

### Create
```
Request
POST http://0.0.0.0:8888/blog/api/tags
{
  "label":"test"
}

Response
201 Created
Location: http://0.0.0.0:8888/blog/api/tags/ahJiaXN3YWRhaGFsLWRvdC1jb21yEAsSA1RhZxiAgICAgIDACgw

{
    "label": "test2",
    "key": "/blog/api/tags/ahJiaXN3YWRhaGFsLWRvdC1jb21yEAsSA1RhZxiAgICAgIDACgw"
}
```

### Get
```
Request
GET http://0.0.0.0:8888/blog/api/tags

Resonse:
200 OK
[
    {
        "label": "test--updated",
        "key": "/blog/api/tags/ahJiaXN3YWRhaGFsLWRvdC1jb21yEAsSA1RhZxiAgICAgICACgw"
    },
    {
        "label": "test2",
        "key": "/blog/api/tags/ahJiaXN3YWRhaGFsLWRvdC1jb21yEAsSA1RhZxiAgICAgIDACgw"
    }
]
```

### Update

Notice how links are sent for `template` and `tags`
```
Request
http://0.0.0.0:8888/blog/api/pages/ahJiaXN3YWRhaGFsLWRvdC1jb21yEQsSBFBhZ2UYgICAgICAgAsM
Content-Type: application/json

{
    "template": "/blog/api/ptemplates/ahJiaXN3YWRhaGFsLWRvdC1jb21yGQsSDFBhZ2VUZW1wbGF0ZRiAgICAgICACQw",
    "type": "MAIN",
    "htmlContent": "<h1>Html content of this page</h1>",
    "meta": {
        "title": "Page2 Test...>>>>>>>>>",
        "mimeType": {
            "className": "text",
            "typeName": "plain",
            "fullMimeType": "text/plain"
        },
        "caption": null,
        "status": "NEVER_PUBLISHED",
        "lastModifiedTimestamp": "2016-08-15T01:23:33+00:00",
        "createdTimestamp": "2016-08-15T01:23:33+00:00",
        "tags": [
            "/blog/api/tags/ahJiaXN3YWRhaGFsLWRvdC1jb21yEAsSA1RhZxiAgICAgICACgw"
        ],
        "accessControl": {
            "other": []
        }
    },
    "extraCss": [
        "http://www.aa.css",
        "http://www.bb.css"
    ],
    "extraJs": [
        "http://www.aa.js",
        "http://www.bb.css"
    ],
    "key": "/blog/api/pages/ahJiaXN3YWRhaGFsLWRvdC1jb21yEQsSBFBhZ2UYgICAgICAgAsM"
}


Response:
200 OK
{
    "template": "/blog/api/ptemplates/ahJiaXN3YWRhaGFsLWRvdC1jb21yGQsSDFBhZ2VUZW1wbGF0ZRiAgICAgICACQw",
    "type": "MAIN",
    "htmlContent": "<h1>Html content of this page</h1>",
    "meta": {
        "title": "Page2 Test...>>>>>>>>>",
        "mimeType": {
            "className": "text",
            "typeName": "plain",
            "fullMimeType": "text/plain"
        },
        "caption": null,
        "status": "NEVER_PUBLISHED",
        "lastModifiedTimestamp": "2016-08-15T01:23:33+00:00",
        "createdTimestamp": "2016-08-15T01:23:33+00:00",
        "tags": [
            "/blog/api/tags/ahJiaXN3YWRhaGFsLWRvdC1jb21yEAsSA1RhZxiAgICAgICACgw"
        ],
        "accessControl": {
            "other": []
        }
    },
    "extraCss": [
        "http://www.aa.css",
        "http://www.bb.css"
    ],
    "extraJs": [
        "http://www.aa.js",
        "http://www.bb.css"
    ],
    "key": "/blog/api/pages/ahJiaXN3YWRhaGFsLWRvdC1jb21yEQsSBFBhZ2UYgICAgICAgAsM"
}
```


## Content Negotiation
For pages, you can do content-negotiation. You can ask pages either as json or rendered html. The following example shows the page updated above retrieved as html.

```
Request:
GET http://0.0.0.0:8888/blog/api/pages/ahJiaXN3YWRhaGFsLWRvdC1jb21yEQsSBFBhZ2UYgICAgICAgAsM
Accept: text/html

Response:
200 OK
Content-Type: text/html; charset=UTF-8

<!DOCTYPE html>
<html x-data-ptemplate="/blog/api/ptemplates/ahJiaXN3YWRhaGFsLWRvdC1jb21yGQsSDFBhZ2VUZW1wbGF0ZRiAgICAgICACQw" x-data-ptemplate-title="Template1-________UPDATED&gt;&gt;+++">
    <head>
        <title>Page2 Test...&gt;&gt;&gt;&gt;&gt;&gt;&gt;&gt;&gt;</title>
        <meta></meta>
        <link rel="stylesheet" href="http://www.aa.css" type="text/css"/>
        <link rel="stylesheet" href="http://www.bb.css" type="text/css"/>
        <script src="http://www.aa.js" type="text/javascript"></script>
        <script src="http://www.bb.css" type="text/javascript"></script>
    </head>
    <body>
        <div>Test</div>
        <h1>Html content of this page</h1>
        <div>Footer</div>
    </body>
</html>

```


## Chunked Transfer
For byte stream, we can ask partial or full content. Default is full content. For partial content, set appropriate header. Example below:

```
Request:
GET http://0.0.0.0:8888/blog/api/assets/ahJiaXN3YWRhaGFsLWRvdC1jb21yEgsSBUFzc2V0GICAgICAgIAJDA/stream
Range: bytes=10-20

Response:
206 Partial Content
Content-Length: 11
Content-Range: bytes 10-20/386939

<bytes...>

```
