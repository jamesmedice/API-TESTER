Feature: Api-POST-HEAD-1

  Scenario: http request call lineaCredito
    Given the host url "lineaCredito/"
    When call post api - json file "creditLine.json"
    Then http response JSON
    And http response status code 200

  Scenario: http request call lineaCredito/customersList
    Given the host url "lineaCredito/customersList"
    When call post api - json file "customersList.json"
    Then http response JSON
    And http response status code 200
