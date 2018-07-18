#!/bin/bash     
sh import_custom_libs.sh
mvn clean install
read -n1 -r -p "Press any key to continue..." key
exit 0