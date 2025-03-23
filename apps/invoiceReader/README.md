# invoiceReader
1. A file is loaded to S3
2. This event triggers this application call with the following payload :
```
```
3. A call is made to the OCR API to extract informations
4. The result is written to the topic ``` ```


mvn -B archetype:generate -DarchetypeArtifactId=maven-archetype-quickstart -DgroupId=callrpc -DartifactId=callrpc -DinteractiveMode=false
