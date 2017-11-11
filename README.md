# Spring REST Docs RAML Integration

![](https://img.shields.io/github/license/mduesterhoeft/restdocs-raml.svg?branch=master)
[ ![Build Status](https://travis-ci.org/mduesterhoeft/restdocs-raml.svg)](https://travis-ci.org/mduesterhoeft/restdocs-raml)
[ ![Coverage Status](https://coveralls.io/repos/github/mduesterhoeft/restdocs-raml/badge.svg?branch=master)](https://coveralls.io/r/mduesterhoeft/restdocs-raml)
[ ![Download](https://api.bintray.com/packages/epages/maven/restdocs-raml/images/download.svg)](https://bintray.com/epages/maven/restdocs-raml/_latestVersion)

This is an extension that adds [RAML (RESTful API Modeling Language)](https://raml.org) as an output format to [Spring REST Docs](https://projects.spring.io/spring-restdocs/).

## Motivation

Spring REST Docs is a great tool to produce documentation for your RESTful services that is accurate and readable.

It offers support for AsciiDoc and Markdown. This is great for generating simple HTML-based documentation. 
But both are markup languages and thus it is hard to get any further than statically generated HTML. 

RAML is a lot more flexible. 
With RAML you get a machine-readable description of your API. There is a rich ecosystem around it that contains tools to:
- generate a HTML representation of your API - [raml2html](https://www.npmjs.com/package/raml2html)
- interact with your API using an API console - [api-console](https://github.com/mulesoft/api-console)

Also, RAML is supported by many REST clients like [Postman](https://www.getpostman.com) and [Paw](https://paw.cloud). Thus having a RAML representation of an API is a great plus when starting to work with it.

