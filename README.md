# How to start

Start with docker:

```shell
docker build -t backendTestcase .
docker run -p 8080:8080 backendTestcase
```

Start with gradle (May require `JAVA_HOME` and other dev dependencies):

```shell
gradlew run
```

# How to use

Open `http://localhost:8080/swagger-ui` in browser. <br />
Or alternatively you can import `./api.postman_collection.json` into postman client. <br />
OpenAPI specification stored inside `src/main/resources/static/swagger.json`.

# How it works

All the routing stored inside `web.` package. There are two main routes: `/swagger-ui` and `users`.
`/swagger-ui` serves swagger ui and `/static/swagger.json` OpenAPI specification file.
`/user` serves user-related operations: GET, POST. <br />
Inside this package is also `exceptionHandling` extension function that is used to handle all the exceptions that occur
inside web layer.

Database settings stored inside `service.DatabaseFactory` object. Flyway is here too. This branch supports
flyway's `JavaBasedMigrations`, they are stored inside `db.migration` package of the source directory.

**!! IMPORTANT !!: if you look for sql-based migrations - I've created separate branch: `sqlMigrations`**

Exposed-based ORM and models stored inside `model.` package. There's only one database table: Users. Everything else is
utils.

The last package is `util.`. There are stored all the util functions.

Main file is responsible for all the server configuration.

# Dev notes.

### Ktor itself

I've never used ktor before and this case was fun for me to do. Because lack of documentation about ktor itself and its
libraries I had to usually do some research in source code of both of them. It boosted me as developer a little, and I
think about make some contributions in these technologies. Before this case I've never had thoughts like these.

### OpenAPI and Swagger with Ktor

This is a huge problem here. I've tried at least two libraries: [Kompendium](https://github.com/bkbnio/kompendium)
and [Ktor-OpenAPI-Generator](https://github.com/papsign/Ktor-OpenAPI-Generator). Both of them have terrible
documentation and at the end I finally had to create my own solution. I've been trying to generate OpenAPI spec from
existing routing code but neither of them didn't do that well. So finally I had to write it myself with my hands. And
because of it I've spent ~8 hours struggling with my own misspelling of `Accept: appliaction/json` header causing Ktor
to respond with 406 status code.

### Exposed

It's just cool dsl that is much closer to native sql than java solutions like hibernate. I've enjoyed learning it.

