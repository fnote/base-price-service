#Reference Price Service
[![Quality Gate Status](https://cloudpricing-sonar-dev.prcp-np.us-east-1.aws.sysco.net/api/project_badges/measure?project=com.sysco%3Aref-price-service&metric=alert_status)](https://cloudpricing-sonar-dev.prcp-np.us-east-1.aws.sysco.net/dashboard?id=com.sysco%3Aref-price-service)

Reference Price Service consumes periodically provided files by PA and EATs, processes them, loads into a database, and provides an API so that the price for a particular item for a specific customer can be retrieved with low latency.

## Design & API Spec
* [Software Analysis and Design](https://syscobt.atlassian.net/wiki/spaces/PRCP/pages/1449168081/Reference+Pricing+-+Software+Analysis+and+Design)

# How to start up 
Do the below steps to get the Reference Price API up and running entirely in a local developer workstation
* This requires a mysql database

Run the following [mysql-deploy](https://github.aws.na.sysco.net/pricing-platform/mysql-deploy/tree/reference-price-db-deploy) to create the
 databases locally or in the required environment

* Set the active profile to DEV / local
* Update the AWS Credentials

* Run the unit tests
```dtd
gradle test
```
* Running unit tests will insert test data into embedded h2 database.

##Application properties

Reference Price API reads all its profile specific properties from AWS parameter store so that all the application properties 
are managed centrally in a secured environment.All the sensitive parameters are store encrypted.

###AWS Spring Cloud
This uses Spring Cloud framework based configuration management. The defaults ```bootstrap.properties``` file 
has the default configuration required for Spring Cloud.
```dtd
cloud.aws.credentials.instanceProfile=false
cloud.aws.credentials.useDefaultAwsCredentialsChain=true
cloud.aws.stack.auto=false
cloud.aws.region.auto=false
cloud.aws.region.static=us-east-1

aws.paramstore.prefix=/CP
aws.paramstore.defaultContext=application
aws.paramstore.profileSeparator=/
aws.paramstore.failFast=true
aws.paramstore.name=REF_PRICE_SERVICE
aws.paramstore.enabled=true
```
Note that by default ```aws.paramstore.enabled``` is enabled. So that under default conditions, you need to have the required AWS access to fetch the profile specific properties.
Above configuration assumes that application will run in ```cloud.aws.region.static=us-east-1``` region.

###AWS Credentials
It requires an AWS connection to read the parameters. So store the AWS token in the file ``` ~/.aws/credentials```.
In Sysco AWS, credentials get expired every one hour so its required to refresh this file in sufficient a frequency.
```dtd
[default]
aws_access_key_id = xxxxxxxxxxxxx
aws_secret_access_key = tX4xxxxxxxxxxxxx
aws_session_token = IQxxxxxxxxxxxxxxx     
```

###Disable AWS Spring Cloud
Optionally you can disable AWS parameter store based properties and read them from the local properties file. 
This is desired only in developer workstation in order to avoid having to refresh AWS credentials.
Use the spring profile ```local``` for this mode so that ```boostrap-local.properties``` will dictate 
over the default ```boostrap.properties```
eg:
```dtd
java -jar <jarfilename> -Dspring_profiles_active=local
```

and ```application-local.properties``` will inject the other required properties.


###Recommended Profiles

* local<br>Used when running the application in developer workstation without a vpn connection Sysco AWS cloud

* DEV<br>Used when running the application in developer workstation or in cloud environment using the aws cloud based properties related to dev
 profile.

* EXE<br>Used when running the application in a cloud environment using the aws cloud based properties related to QA profile.

* STG<br>Used when running the application in staging environment

* PROD<br>Used when running the application in production

###Set active spring profile to enable the required profile based properties
```dtd
export spring_profiles_active=DEV
```

## Features Built into the Framework

## Layered Architecture

![Image description](./api-achi.png)