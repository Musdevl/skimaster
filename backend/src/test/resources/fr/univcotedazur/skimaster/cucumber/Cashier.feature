Feature: Cashier - Payment and NFC Card Assignment

  Background:
    Given the bank is available


  Scenario: Successful payment creates a validated order
    Given an ADULT customer named "Alice" with credit card "1234567890"
    And the customer's cart contains 1 "BASIC_PLAN"
    When the customer pays 10.0
    Then the order is created with receipt id "receipt-001"
    And no PaymentException is thrown

  Scenario: Bank refusal raises a PaymentException and no order is created
    Given an ADULT customer named "Bob" with credit card "0000000000"
    And the customer's cart contains 1 "BASIC_PLAN"
    And the bank refuses the payment
    When the customer tries to pay 10.0
    Then a PaymentException is thrown
    And no order is created
    And no NFC card is registered


  Scenario: A non-subscription plan does not generate any NFC card
    Given an ADULT customer named "Charlie" with credit card "1234567890"
    And the customer's cart contains 1 "SUPER_CARD_PASSAGE"
    When the customer pays 1.0
    Then no NFC card is registered

  Scenario: An ADULT customer buying a subscription plan receives a HIGH_SOUND card
    Given an ADULT customer named "Diana" with credit card "1234567890"
    And the customer's cart contains 1 "BASIC_PLAN"
    When the customer pays 10.0
    Then 1 NFC card is registered
    And the NFC card has sound "HIGH_SOUND"
    And the gates are notified 1 time

  Scenario: A CHILD customer buying a subscription plan receives a LOW_SOUND card
    Given a CHILD customer named "Eve" with credit card "1234567890"
    And the customer's cart contains 1 "BASIC_PLAN"
    When the customer pays 10.0
    Then 1 NFC card is registered
    And the NFC card has sound "LOW_SOUND"
    And the gates are notified 1 time


  Scenario: A FAMILY_PLAN generates 4 NFC cards (2 HIGH_SOUND + 2 LOW_SOUND)
    Given an ADULT customer named "Frank" with credit card "1234567890"
    And the customer's cart contains 1 "FAMILY_PLAN"
    When the customer pays 30.0
    Then 4 NFC cards are registered
    And 2 NFC cards have sound "HIGH_SOUND"
    And 2 NFC cards have sound "LOW_SOUND"
    And the gates are notified 4 times


  Scenario: Buying 2 subscription plans generates 2 NFC cards
    Given an ADULT customer named "Grace" with credit card "1234567890"
    And the customer's cart contains 2 "BASIC_PLAN"
    When the customer pays 20.0
    Then 2 NFC cards are registered
    And the gates are notified 2 times