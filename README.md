# Reachability Demo Application
Demo application for the reachability project.  


# Usage
java -jar ./reachability-demo.jar --fromDate [date] --toDate [date] --outputPath [path] --dont-scan --enginePath [path] --sourceRootPath [path] --partnerToken [token]  

Where:  
**--fromDate**: The date from where to start query the DB in the following format: yyyy-MM-dd. Mandatory.  
**--toDate**: The date from where to stop query the DB in the following format: yyyy-MM-dd. Optional. Default: fromDate + 1 day.  
**--outputPath**: The folder into which the result will be written. Mandatory.  
**--dont-scan**: Use this flag to skip the scan phase. Optional. Default: false.  
**--enginePath**: The full path to the mend executable engine. Mandatory if --dont-scan==false.  
**--sourceRootPath**: The path to the root of the source dir. Mandatory if --dont-scan==false.  
**--partnerToken**: The API request token as received from Mend. Mandatory.  

Example: `java -jar ./reachability-demo.jar --fromDate 2018-07-01 --toDate 2018-07-02  --outputPath /home/user/reachability/output --enginePath /home/user/engine --sourceRootPath /home/user/reachability/sources --partnerToken dummy-token`  

Example: `java -jar ./reachability-demo.jar --fromDate 2018-07-01 --toDate 2018-07-02  --outputPath /home/user/reachability/output --dont-scan --partnerToken dummy-token`

# Build
To build the jar use the following command:
`mvn clean install`. This will create the `reachability-demo.jar` file under the `./target` folder  

Tested with `Apache Maven 3.9.5` on `Java version: 17.0.2`