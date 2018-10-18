#!/bin/bash          

#LOCI-TOOLS
#2018-09 not used any more, because of the direct maven download of ome's formats-bsd
#mvn install:install-file -Dfile=./lib/bioformats/loci_tools-5.9.1.jar -DgroupId=bioformats -DartifactId=loci_tools -Dversion=5.9.1 -Dpackaging=jar

#FLANAGAN
mvn install:install-file -Dfile=./lib/flanagan/flanagan-jdk1.8.0-version.jar -DgroupId=flanagan -DartifactId=flanagan -Dversion=jdk1.8.0 -Dpackaging=jar

#JAI-CORE & JAI-CODEC
mvn install:install-file -Dfile=./lib/JAI/jai_core-1.1.3.jar -DgroupId=javax.media.jai -DartifactId=jai_core -Dversion=1.1.3 -Dpackaging=jar
mvn install:install-file -Dfile=./lib/JAI/jai_codec-1.1.3.jar -DgroupId=javax.media.jai -DartifactId=jai_codec -Dversion=1.1.3 -Dpackaging=jar

#bUnwarpJ_ 1.1
mvn install:install-file -Dfile=./lib/ImageJ/ijPlugin/bUnwarpJ/bUnwarpJ_.jar -DgroupId=ijPlugins -DartifactId=bUnwarpJ -Dversion=1.1 -Dpackaging=jar

#TurboReg 20080619
mvn install:install-file -Dfile=./lib/ImageJ/ijPlugin/TurboReg/TurboReg_.jar -DgroupId=ijPlugins -DartifactId=TurboReg -Dversion=20080619 -Dpackaging=jar

#UnwarpJ 20060708
mvn install:install-file -Dfile=./lib/ImageJ/ijPlugin/UnwarpJ/UnwarpJ.zip -DgroupId=ijPlugins -DartifactId=UnwarpJ -Dversion=20060708 -Dpackaging=zip

#Watershed 20070322
mvn install:install-file -Dfile=./lib/ImageJ/ijPlugin/Watershed/Watershed_.jar -DgroupId=ijPlugins -DartifactId=watershed -Dversion=20070322 -Dpackaging=jar

#JAyatana 1.2.4
mvn install:install-file -Dfile=./lib/jayatana-1.2.4/jayatana-1.2.4.jar -DgroupId=org.java.ayatana -DartifactId=jayatana -Dversion=1.2.4 -Dpackaging=jar

#matlabcontrol 4.1.0 (http://code.google.com/p/matlabcontrol/) New BSD License
mvn install:install-file -Dfile=./lib/matlabcontrol/matlabcontrol-4.1.0.jar -DgroupId=matlabcontrol -DartifactId=matlabcontrol -Dversion=4.1.0 -Dpackaging=jar

#AppleJavaExtensions version 2011 11 09
mvn install:install-file -Dfile=./lib/apple/AppleJavaExtensions-20111109.jar -DgroupId=com.apple -DartifactId=AppleJavaExtensions -Dversion=20111109 -Dpackaging=jar

#tools.jar because Java 1.9 and higher does not include it in the JDK any more
#maybe we do not need tools.jar any more because ImageJ seems to work without it
#mvn install:install-file -Dfile=./lib/Tools-1.8.0_181/tools.jar -DgroupId=java.jdk.lib -DartifactId=tools -Dversion=1.8.0_181 -Dpackaging=jar
