# Performance Сoap Test Suite

## Getting started

Now you have an opportunity to independently evaluate the performance of **IoTBroker.Cloud**. 
Besides this test suite can be used to measure the performance of your own software. The following instruction will 
explain how to run the performance tests by yourself.

### Prerequisites

The following programs should be installed before starting to clone the project:

* **JDK (version 8+)**;
* **Maven**.

### Installation

First of all, you should clone [Performance COAP Test Suite](https://github.com/mobius-software-ltd/coap-test-suite).

Then you have to build the project. For this purpose run in console "mvn clean install -Dgpg.skip=true" 

Now you have the controller (coap-test-suite/controller/target/coap-controller) and the test runner 
(coap-test-suite/runner/target/coap-scenario-runner) jar files on your computer.
To make the work more convenient, create _performance_test_ folder containing
`coap-controller.jar` and `coap-scenario-runner.jar`.
Also you should add [JSON files](https://github.com/mobius-software-ltd/coap-test-suite/tree/master/runner/src/test/resources/json) and [config.properties](https://github.com/mobius-software-ltd/coap-test-suite/controller/src/main/resources/config.properties) to this very performance_test folder. 
Modify scenario file by setting "controller.1.ip" and "broker.ip" with public IP addresses used on controller and broker.
In config.properties set "localHostname" property with local ip address of the machine running the controller.

### Test run

First you should open the terminal and `cd` to _performance_test_ folder. You should start the controller by running
the command which is given below (do not forget to indicate your path):
 

Now you can start the controller by running the following command :

```
java -Xmx1024m -Xms1024m -jar coap-controller.jar
 
```
Here is a brief explanation:

**Xmx1024m** – maximum memory allocation;

**Xmx1024m** – initial memory allocation;

**controller.jar** – controller which is inside the _performance_test_ folder;


Now you should open the second terminal window and `cd` to _performance_test_ folder. 
Now you can run the test by running the following command:
```
java -jar test-runner.jar publishers_qos0.json
```
The command mentioned above is an example of running the test scenario which is described in `publishers_qos0.json` file.

Each [JSON file](https://github.com/mobius-software-ltd/coap-test-suite/tree/master/runner/src/test/resources/json) contains different test scenarios. You can separately run each test scenario by indicating the name of a specific [JSON file](https://github.com/mobius-software-ltd/coap-test-suite/tree/master/runner/src/test/resources/json). When the test is over you will get the report for each test scenario:
```
+---------- Scenario-ID:  8f7f4b89-ad35-40f7-985e-7280b3d569d4 ---------- Result: SUCCESS ----------+ 

| Start Time                      | 2019-01-04 13:04:13.748        | 1546599853748                  | 

| Finish Time                     | 2019-01-04 13:04:39.851        | 1546599879851                  | 

| Current Time                    | 2019-01-04 13:04:46.943        | 1546599886943                  | 

+---------------------------------+--------------------------------+--------------------------------+ 

| Total clients                 1 | Total commands               3 | Errors occured               0 | 

| Successfuly finished          1 | Successfuly finished         3 | Duplicates received          0 | 

| Failed                        0 | Failed                       0 | Duplicates sent              0 | 

+--------------- Outgoing counters ---------------+--------------- Incoming counters ---------------+ 

|      Counter Name      |      Counter Value     |      Counter Name      |      Counter Value     | 

|        SUBSCRIBE       |            1           |        SUBSCRIBE       |            0           | 

|         SUBACK         |            0           |         SUBACK         |            1           | 

|         PUBLISH        |            0           |         PUBLISH        |          10000         | 

|         PUBACK         |          10000         |         PUBACK         |            0           | 

|       UNSUBSCRIBE      |            1           |       UNSUBSCRIBE      |            0           | 

|        UNSUBACK        |            0           |        UNSUBACK        |            1           | 

|         PINGREQ        |            8           |         PINGREQ        |            0           | 

|        PINGRESP        |            0           |        PINGRESP        |            8           | 

+------------------------+------------------------+------------------------+------------------------+ 

+---------- Scenario-ID:  568df358-dacf-48d4-8871-dbf4cbb32ed3 ---------- Result: SUCCESS ----------+ 

| Start Time                      | 2019-01-04 13:04:14.764        | 1546599854764                  | 

| Finish Time                     | 1970-01-01 13:04:42.831        | 1546599882831                  | 

| Current Time                    | 2019-01-04 13:04:46.947        | 1546599886947                  | 

+---------------------------------+--------------------------------+--------------------------------+ 

| Total clients               500 | Total commands           10500 | Errors occured               0 | 

| Successfuly finished        500 | Successfuly finished     10500 | Duplicates received          0 | 

| Failed                        0 | Failed                       0 | Duplicates sent              0 | 

+--------------- Outgoing counters ---------------+--------------- Incoming counters ---------------+ 

|      Counter Name      |      Counter Value     |      Counter Name      |      Counter Value     | 

|         PUBLISH        |          10000         |         PUBLISH        |            0           | 

|         PUBACK         |            0           |         PUBACK         |          10000         | 

|         PINGREQ        |          3492          |         PINGREQ        |            0           | 

|        PINGRESP        |            0           |        PINGRESP        |          3492          | 

+------------------------+------------------------+------------------------+------------------------+
```
Each test can be run in its current form.
Besides you can change the existing test scenarios or add the new ones.

Performance MQTT-SN Test Suite is developed by [Mobius Software](http://mobius-software.com).

## [License](LICENSE.md)
