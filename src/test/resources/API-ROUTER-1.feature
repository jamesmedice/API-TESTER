Feature: Api-ROUTER-1

  Scenario: http request call lineaCredito
    Given the route host url "lineaCredito/01025/0009376581283000/16162653"
    When route call get api
    Then route http response status code 200
    And route http response payload not empty

  Scenario: http request call fido utilizzo
    Given the route host url "fido/utilizzo/16162653"
    When route call get api
    Then route http response status code 200
    And route http response payload not empty

  Scenario: http request call fido by id
    Given the route host url "fido/16162653"
    When route call get api
    Then route http response status code 200
    And route http response payload not empty

  Scenario: http request call lineaCredito
    Given the route host url "lineaCredito/"
    When route call post api - json file "creditLine.json"
    Then route http response status code 200
    And route http response payload not empty

  Scenario: http request call lineaCredito/customersList
    Given the route host url "lineaCredito/customersList"
    When route call post api - json file "customersList.json"
    Then route http response status code 200
    And route http response payload not empty
