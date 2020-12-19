## Changes by Agit Oktay


### Question 1: Product Management 
   
   * I didn't change any logic and work flow of the methods in the files that are stated in the README file.
        * Moved some of them to new packages
        * Add Exception signature to methods
        * Make IyzicoPaymentService a implementation of PaymentService interface to make injection of it according to settings.
   * (Bonus) Implement SandboxIyzicoPaymentService which is implementation of the PaymentService
   * (Bonus) Add new settings to application.yml use sandbox api
        * You can enabled it like that;
            ```
          sandbox:
            enabled: true
            apiKey: <sandbox-api-key>
            secretKey: <sandbox-secret-key>
          ```
   * Add product layer as controller, service and repository..
   * Add unit and integration tests
   * Add simple Postman test collection and JMeter test profile.
          
### Question 2 : Latency Management
    
   *  @Transactional removed from IyzicoPaymentService