Feature: Customer - Cart, Catalog and Customer Registration

  Scenario: Adding an item to an empty cart
    Given an ADULT customer named "Alice" with credit card "1234567890" and id 1
    When Alice adds 2 "BASIC_PLAN" to her cart
    Then Alice's cart contains 2 "BASIC_PLAN"

  Scenario: Adding the same item twice cumulates the quantity
    Given an ADULT customer named "Alice" with credit card "1234567890" and id 1
    When Alice adds 2 "BASIC_PLAN" to her cart
    And Alice adds 3 "BASIC_PLAN" to her cart
    Then Alice's cart contains 5 "BASIC_PLAN"

  Scenario: Reducing an item quantity in the cart
    Given an ADULT customer named "Alice" with credit card "1234567890" and id 1
    When Alice adds 3 "BASIC_PLAN" to her cart
    And Alice adds -1 "BASIC_PLAN" to her cart
    Then Alice's cart contains 2 "BASIC_PLAN"

  Scenario: Removing all quantity of an item removes it from the cart
    Given an ADULT customer named "Alice" with credit card "1234567890" and id 1
    When Alice adds 2 "BASIC_PLAN" to her cart
    And Alice adds -2 "BASIC_PLAN" to her cart
    Then Alice's cart is empty

  Scenario: Removing more than available raises a NegativeQuantityException
    Given an ADULT customer named "Alice" with credit card "1234567890" and id 1
    When Alice adds 1 "BASIC_PLAN" to her cart
    And Alice adds -5 "BASIC_PLAN" to her cart
    Then a NegativeQuantityException is thrown

  Scenario: Cart price is the sum of quantity times plan price
    Given an ADULT customer named "Alice" with credit card "1234567890" and id 1
    When Alice adds 2 "BASIC_PLAN" to her cart
    And Alice adds 1 "SUPER_CARD" to her cart
    Then Alice's cart price is 35.0

  Scenario: Validating an empty cart raises an EmptyCartException
    Given an ADULT customer named "Alice" with credit card "1234567890" and id 1
    When Alice validates her empty cart
    Then an EmptyCartException is thrown

  Scenario: Validating a non-empty cart triggers payment and clears the cart
    Given an ADULT customer named "Alice" with credit card "1234567890" and id 1
    And the payment succeeds for Alice
    When Alice adds 1 "BASIC_PLAN" to her cart
    And Alice validates her cart
    Then the order is created
    And Alice's cart is empty

  Scenario: Validating a cart when payment is refused raises a PaymentException
    Given an ADULT customer named "Alice" with credit card "1234567890" and id 1
    And the payment is refused for Alice
    When Alice adds 1 "BASIC_PLAN" to her cart
    And Alice validates her cart
    Then a PaymentException is thrown for the customer

  Scenario: Listing plans returns only subscription plans
    When the catalog lists all plans
    Then the result contains "BASIC_PLAN"
    And the result contains "BEGINNER_PASS"
    And the result contains "SUPER_CARD"
    And the result contains "FAMILY_PLAN"
    And the result does not contain "SUPER_CARD_PASSAGE"

  Scenario: Exploring the catalog with a matching regex returns matching subscription plans
    When the catalog is explored with regexp "BASIC.*"
    Then the result contains "BASIC_PLAN"
    And the result does not contain "SUPER_CARD"
    And the result does not contain "SUPER_CARD_PASSAGE"

  Scenario: Exploring the catalog with a non-matching regex returns an empty set
    When the catalog is explored with regexp "UNKNOWN.*"
    Then the result is empty

  Scenario: Registering a new customer saves it
    Given no customer named "Bob" exists
    When "Bob" registers with credit card "0987654321" as ADULT
    Then the customer "Bob" is saved

  Scenario: Registering a customer with an existing name raises AlreadyExistingCustomerException
    Given a customer named "Bob" already exists
    When "Bob" registers with credit card "0987654321" as ADULT
    Then an AlreadyExistingCustomerException is thrown

  Scenario: Retrieving a customer with an unknown id raises CustomerIdNotFoundException
    Given no customer exists with id 99
    When retrieving customer with id 99
    Then a CustomerIdNotFoundException is thrown