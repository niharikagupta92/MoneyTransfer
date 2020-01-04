## **Revolut Money Transfer Application**

#### **Problem**:

Design and implement a RESTful API (including data model and the backing implementation) 
for money transfers between accounts.

#### **Explicit requirements:**
1. You can use Java or Kotlin.
2. Keep it simple and to the point (e.g. no need to implement any authentication).
3. Assume the API is invoked by multiple systems and services on behalf of end users.
4. You can use frameworks/libraries if you like (​except Spring​), but don't forget about
requirement #2 and keep it simple and avoid heavy frameworks.
5. The datastore should run in-memory for the sake of this test.
6. The final result should be executable as a standalone program (should not require a
pre-installed container/server).
7. Demonstrate with tests that the API works as expected.

#### **Implementation Details And Assumptions:**
- Java is used for implementation.
- SparkJava is used as framework for creating microservice application.
- Gson is used for Json Serialization and Deserialization.
- H2 is used as in-memory database for storing accounts and transfer information.
- SLF4J is used for Logging.
- JUnit is used for unit testing the functionality of the application.
- Only four currencies are supported: EUR, USD, SGD and GBP
- Currency Conversion rates are currently harcoded in the code. Can be implemented separately.
- No support for scheduled transactions.
- Transfer is only valid if the transfer currency matches with sender or receiver account currency.

#### **Methodology:**

##### **Account Creation:**
Account creation is supported via `/createAccount` api, which takes an initial amount and the currency of the 
account. The api returns the corresponding account number of the created account, or an error
if the request is invalid. The api creates an entry for the new account in the database.

Input Request body:
```
{
    "currency":"SGD",
    "balance":"1000"
}
```
Output Response:
```$xslt
{
    "accountId":"SampleAccountNumber"
}
```

##### **Account Fetch Balance:**
Account Balance can be fetched via `/balance/:accountId` api. It returns the balance of the account
in the format as `<CURRENCY> <AMOUNT>`. For instance, if the api is called with `/balance/SampleAccountNumber` , the 
response will be `SGD 1000`. The api fetches the entry from the database, or returns an error if the account is not present in database.

##### **Transfer Request Creation:**
Transfer request can be initiated via `/transfer` api. The api takes the sender account Id, receiver account id,
the amount and the currency of the transfer. It creates an entry for the transfer in the database
and put it in `PENDING` state. The api returns a transaction Id, or an error if the request is invalid.
Following validation checks are perfomed:
- The currency should be either sender's or receiver's currency.
- The currency conversion should be supported.
- The sender and receiver account numbers should be valid. 
- The amount should be non negative.
- The sender should have sufficient balance in the account.

Input Request Body:
```
{
    "sender": "ID1",
    "receiver": "ID2",
    "currency": "USD",
    "amount": "50"
}
```
Output Response Body:
```
{
    "transferId": "TransferId1",
    "status": "Pending"
}
```

##### **Transfer Status:**
Transfer status can be fetched via `/transferStatus/:transferId` api, which fetches the transfer 
entry from database, and returns the status as `PENDING`, `SUCCESS` or `FAILURE`.

##### **Transfer Histor for Account:**
Transfer history for an account can be getched via `/transferHistory/:accountId` api, which fetches 
the transfer history for an accountId. It fetches all the transactions associated with the account, 
both credit and debit, and all kinds of transactions status, `PENDING`, `SUCCESS` or `FAILURE`

##### **Transfer Request Execution:**
Transfer requests are executed via a scheduled service, which executes the request one at a time.
The scheduler runs at every specific time-period (currently hardcoded as 10 seconds), and fetches
the earliest Pending transfer requests based on a throttle limit. This throttle limit makes sure
not to have heavy dependency on database read and write, and provides reliability.

#### **Steps to Build and Run**
To build:

`mvn clean install`

To run: 

`java -jar target/money-transfer-1.0-SNAPSHOT-jar-with-dependencies.jar`

For example:
- create account 1 with SGD 1000
```$xslt
$ curl --header "Content-Type: application/json"  --request POST --data '{"currency":"SGD","balance":"1000"}' http://localhost:4567/createAccount
{
  "accountId": "42d6abc3-fc3f-41ab-81b0-f6045e050839"
}

$ curl --header "Content-Type: application/json"  --request GET http://localhost:4567/balance/42d6abc3-fc3f-41ab-81b0-f6045e050839
SGD 1000.0
```
- Create account 2 with EUR 1000
```
$ curl --header "Content-Type: application/json"  --request POST --data '{"currency":"EUR","balance":"1000"}' http://localhost:4567/createAccount
{
  "accountId": "9b97e30f-6618-4b6f-b881-f6b1a4439525"
}

$ curl --header "Content-Type: application/json"  --request GET http://localhost:4567/balance/9b97e30f-6618-4b6f-b881-f6b1a4439525
EUR 1000.0
```

- Create a transfer request of EUR 100 from Account 1 to account 2
```
$ curl --header "Content-Type: application/json"  --request POST --data '{"sender":"42d6abc3-fc3f-41ab-81b0-f6045e050839", "receiver":"9b97e30f-6618-4b6f-b881-f6b1a4439525", "currency":"EUR","amount":"100"}' http://localhost:4567/transfer
{
  "transferId": "610dd644-260b-4192-a8be-876a4b657801",
  "status": "Pending"
}

$ curl --header "Content-Type: application/json"  --request GET http://localhost:4567/transferStatus/610dd644-260b-4192-a8be-876a4b657801
{
  "senderAccount": "42d6abc3-fc3f-41ab-81b0-f6045e050839",
  "receiverAccount": "9b97e30f-6618-4b6f-b881-f6b1a4439525",
  "amount": 100.0,
  "currency": "EUR",
  "createTimestamp": "2020-01-04T13:07:59.06Z",
  "transferId": "610dd644-260b-4192-a8be-876a4b657801",
  "status": "Success"
}
```

- Final accounts status:
```
$ curl --header "Content-Type: application/json"  --request GET http://localhost:4567/balance/42d6abc3-fc3f-41ab-81b0-f6045e050839
SGD 850.0

$ curl --header "Content-Type: application/json"  --request GET http://localhost:4567/balance/9b97e30f-6618-4b6f-b881-f6b1a4439525
EUR 1100.0

```

- Account transfer history:
```
$ curl --header "Content-Type: application/json"  --request GET http://localhost:4567/transferHistory/42d6abc3-fc3f-41ab-81b0-f6045e050839
{
  "transfers": [
    {
      "senderId": "42d6abc3-fc3f-41ab-81b0-f6045e050839",
      "receiverId": "9b97e30f-6618-4b6f-b881-f6b1a4439525",
      "transferAmount": 100.0,
      "transferCurrency": "EUR",
      "dateTime": "2020-01-04T13:07:59.06Z",
      "status": "Success",
      "transferId": "610dd644-260b-4192-a8be-876a4b657801"
    }
  ]
}


```