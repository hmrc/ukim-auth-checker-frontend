
# ukim-auth-checker-frontend

UKIM AUTH CHECKER FRONTEND

This repository contains the necessary code to display user journey for UKIM Auth Checker Frontend

The codebase uses the none scaffolding route with no database

### Overview

The Frontend is for a new UK Internal Market Scheme (UKIMS) for internal trade that enables traders to self-assess their goods’ destination as At Risk (AR) or Not at Risk (NAR).
It will take in one UKIM number, make a call to the backend (https://github.com/hmrc/ukim-auth-checker-api) returning the UKIM numbers validity.

### Running in DEV mode

To start the Frontend repo use `sbt run`

To start the service locally using service manager, use `sm2 --start UKIM_ALL`

### Running the tests

    sbt test

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").