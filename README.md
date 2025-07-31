
# crdl-ref-data-dps-stub

The Central Reference Data Platform Services(DPS) Stub responsibilities:
* Simulate the responses of DPS

## Prerequisites

To ensure that you have all the prerequisites for running this service, follow the Developer setup instructions in the MDTP Handbook.

This should ensure that you have the prerequisites for the service installed:

* JDK 21
* sbt 1.10.x or later
* MongoDB 7.x or later
* Service Manager 2.x

---

### Adding stub data

Stub data should be added to this repository once a new code_list or correspondence code is added in crdl-cache.

This allows the tests to simulate the response of DPS, without having to call on DPS each time.

* To add stub data to this repository, we make a `GET` request to the ***DPS*** API.

  The easiest way to do this is with an API client such as ***Bruno*** or ***Postman***, then it will be nicely formatted for easy copy-pasting.

  You will need to get your authorisation credentials from Integration Hub and set them up in the Auth tab selecting ***Basic Auth*** as the Authorisation type.

  As an example we can fetch the stub data for `BC03` 

  Make a `GET` request to https://admin.qa.tax.service.gov.uk/hip/crdl/views/iv_crdl_reference_data

  Add the following parameters to your request:

  | Name          | Path                                   |
  |---------------|----------------------------------------|
  | codelist_code | BC03                                   |

  or if you prefer, you can just add the query to the url: https://admin.qa.tax.service.gov.uk/hip/crdl/views/iv_crdl_reference_data?codelist_code=BC03


* The following parameter is optional, but is what has been used to make the existing stubs:

  | Name          | Path                             |
  |---------------|----------------------------------|
  | $orderby      | code_list_code, snapshotversion  |

  Here is the whole query url: https://admin.qa.tax.service.gov.uk/hip/crdl/views/iv_crdl_reference_data?codelist_code=BC03&$count=10&$orderby=code_list_code,snapshotversion

* Make a new file in `conf/resources/codeList` called `BC03_page1.json` and paste the results here.

---

### All tests and checks
This is an sbt command alias specific to this project. It will run a scala format
check, run unit tests, run integration tests and produce a coverage report:
> `sbt runAllChecks`

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").