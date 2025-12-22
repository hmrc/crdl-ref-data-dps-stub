
# crdl-ref-data-dps-stub

The Central Reference Data Platform Services(DPS) Stub responsibilities:
* Simulate the responses of DPS

### Running the service

```shell
sbt run
```
The service runs on port 7253 by default.

## Prerequisites

To ensure that you have all the prerequisites for running this service, follow the Developer setup instructions in the MDTP Handbook.

This should ensure that you have the prerequisites for the service installed:

* JDK 21
* sbt 1.10.x or later
* MongoDB 7.x or later
* Service Manager 2.x

---

### Adding stub data

Stub data should be added to this repository once a new codelist or correspondence list is added in crdl-cache.

This allows the tests to simulate the response of DPS, without having to call on DPS each time.

* To add stub data to this repository, we make a `GET` request to the [***DPS*** API](https://admin.tax.service.gov.uk/integration-hub/apis/view-specification/38a94f8f-e17f-41c3-863a-bc7b4a37c93d#tag/iv_crdl_reference_data).

  The easiest way to do this is with an API client, then it will be nicely formatted for easy copy-pasting.
  
  At time of writing, the recommended API client for HMRC is Bruno.

  You will need to get your authorisation credentials from Integration Hub and set them up in the Auth tab selecting ***Basic Auth*** as the Authorisation type.

  As an example we can fetch the stub data for the code list code `BC03`

  Make a `GET` request to https://admin.qa.tax.service.gov.uk/hip/crdl/views/iv_crdl_reference_data

  Add the following parameters to your request:

  | Name          | Path                                   |
  |---------------|----------------------------------------|
  | codelist_code | BC03                                   |

  or if you prefer, you can just add the query to the url: https://admin.qa.tax.service.gov.uk/hip/crdl/views/iv_crdl_reference_data?codelist_code=BC03
  or a curl request to:
  
  ```shell
  curl -H "Authorization: Basic $(echo -n <client_id>:<client_secret> : base64 )" https://admin.qa.tax.service.gov.uk/hip/crdl/views/iv_crdl_reference_data?codelist_code=BC03
  ```

* The following parameter is optional, but is what has been used to make the existing stubs:

  | Name          | Path                             |
  |---------------|----------------------------------|
  | $orderby      | code_list_code, snapshotversion  |

  Here is the whole query url: https://admin.qa.tax.service.gov.uk/hip/crdl/views/iv_crdl_reference_data?codelist_code=BC03&$count=10&$orderby=code_list_code,snapshotversion  


* To make a curl request, export the credentials as environment variables in .bashrc or .zshrc
  ```shell
  export CLIENT_ID="<client_id>"
  export CLIENT_SECRET="<client_secret>"
  ```
  And then call them with `$` in the curl request header. In this example we are encoding the credentials to base64.
  
  ```shell
  curl -H "Authorization: Basic $(echo -n "$CLIENT_ID:$CLIENT_SECRET" | base64 )" https://admin.qa.tax.service.gov.uk/hip/crdl/views/iv_crdl_reference_data?codelist_code=BC03&$count=10&$orderby=code_list_code,snapshotversion
  ```

* Make a new file in `conf/resources/codeList` called `BC03_page1.json`, paste the results here, and you are done!

* In a similar way, if you need to add any additional stub data to the customs office, you can do so following the above mentioned steps. The [***DPS*** API for customs office](https://admin.tax.service.gov.uk/integration-hub/apis/view-specification/312528d6-5c8b-45b4-b8a7-f9b12381f063) can be invoked via:

  ```shell
  curl -H "Authorization: Basic $(echo -n "$CLIENT_ID:$CLIENT_SECRET" | base64 )" https://admin.qa.tax.service.gov.uk/hip/crdl/views/iv_crdl_customs_office
  ```
---

### All tests and checks
This is an sbt command alias specific to this project. It will run a scala format
check, run unit tests, run integration tests and produce a coverage report:
> `sbt runAllChecks`

## Steps to generate DPS stubs based on RD files

### input/output folder structure

please input files in following structure.

```
crdl-ref-data-dps-stub/
├── conf/
│   └── resources/
│       ├── input/              ← INPUT: Place your XML files here
│       │   ├── CL231/
│       │   │   └── RD_NCTS-P6_DeclarationType.xml
│       └── codeList/           ← OUTPUT: JSON files generated here
│           ├── CL231/
│           │   ├── CL231_page1.json
│           │   └── CL231_page2.json
```
### How to Run

Run the application using testOnly routes
```bash
sbt 'run -Dapplication.router=testOnlyDoNotUseInAppConf.Routes' 
```
```bash
curl --location --request POST 'localhost:7253/generate-stub-data'
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").