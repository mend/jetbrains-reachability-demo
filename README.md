# Reachability Demo Application
Demo application for the reachability project.  


# Usage
java -jar ./reachability-demo.jar --fromDate <date> --toDate <date> --outputPath <path> --dont-scan --enginePath <path> --sourceRootPath <path>  

Where:  
**--fromDate**: The date from where to start query the DB in the following format: yyyy-MM-dd. Mandatory.  
**--toDate**: The date from where to stop query the DB in the following format: yyyy-MM-dd. Optional. Default: fromDate + 1 day.  
**--outputPath**: The folder into which the result will be written. Mandatory.  
**--dont-scan**: Use this flag to skip the scan phase. Optional. Default: false.  
**--enginePath**: The full path to the mend executable engine. Mandatory if --dont-scan==false.  
**--sourceRootPath**: The path to the root of the source dir. Mandatory if --dont-scan==false.  
**--requestToken**: The API request token as recieved from Mend. Mandatory.  

Example: `java -jar ./reachability-demo.jar --fromDate 2018-07-01 --toDate 2018-07-02  --outputPath /home/user/reachability/output --enginePath /home/user/engine --sourceRootPath /home/user/reachability/sources --requestToken dummy-token`  

Example: `java -jar ./reachability-demo.jar --fromDate 2018-07-01 --toDate 2018-07-02  --outputPath /home/user/reachability/output --dont-scan --requestToken dummy-token`  
