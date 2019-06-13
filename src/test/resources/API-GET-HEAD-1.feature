Feature: Api-GET-HEAD-1

  Scenario: http request call lineaCredito
    Given the host url "lineaCredito/01025/0000207974666000/8680674"
    When call get api
    Then http response JSON
    And http response status code 200


  Scenario: http request call fido utilizzo
    Given the host url "fido/utilizzo/13250295"
    When call get api
    Then http response JSON
    And http response status code 200


  Scenario: http request call fido by id
    Given the host url "fido/13250295"
    When call get api
    Then http response JSON
    And http response status code 200

    