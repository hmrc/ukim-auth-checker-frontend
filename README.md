
# UKIM Auth Checker Frontend

This repository contains the necessary code to display user journey for UKIM Auth Checker Frontend

The codebase uses the none scaffolding route with no database

### Overview

The Frontend is for a new UK Internal Market Scheme (UKIMS) for internal trade that enables traders to self-assess their goods’ destination as At Risk (AR) or Not at Risk (NAR).
It will take in an EORI number, make a call to the backend (https://github.com/hmrc/pds-auth-checker-api), and returns whether the EORI number is associated with a valid or invalid UKIMs Authorisation

### Running in DEV mode

To start the Frontend repo use `sbt run`

To start the service locally using service manager, use `sm2 --start UKIM_ALL`

### Running unit tests

    sbt test

### Running integration tests

    sbt it/test

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").