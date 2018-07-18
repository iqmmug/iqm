#!/bin/bash   
echo "Preparing IQM for release..."  
sh import_custom_libs.sh
mvn clean install license:aggregate-add-third-party javadoc:jar site:site site:stage 
echo "Find the multi-module website ready for deployment in the /target/staging directory."
echo "Perform manual deployment using 'mvn site:deploy'." 
read -n1 -r -p "Press any key to continue..." key
exit 0