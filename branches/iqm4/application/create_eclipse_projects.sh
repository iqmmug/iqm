#!/bin/bash          
# create eclipse project files for each module
sh import_custom_libs.sh
mvn eclipse:clean eclipse:eclipse
read -n1 -r -p "Press any key to continue..."
exit 0