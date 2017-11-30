#!/bin/bash - 
#===============================================================================
#
#          FILE: runServer.sh
# 
#         USAGE: ./runServer.sh 
# 
#   DESCRIPTION: 
# 
#       OPTIONS: ---
#  REQUIREMENTS: ---
#          BUGS: ---
#         NOTES: ---
#        AUTHOR: Sean 
#  ORGANIZATION: 
#       CREATED: 10/05/2017 11:50
#      REVISION:  ---
#===============================================================================

set -o nounset                              # Treat unset variables as an error

export CLASSPATH=target/lis-0.0.1-SNAPSHOT.jar
rmiregistry &
#java -cp  target/lis-0.0.1-SNAPSHOT.jar:target/classes/VoterService.class -Djava.security.policy=policy.txt VoterServer
java -jar -Djava.security.policy=policy.txt target/lis-0.0.1-SNAPSHOT.jar
#java -Djava.security.policy=policy.txt -cp /src/main/resources/opennlp-tools-1.8.3.jar:target/lis-0.0.1-SNAPSHOT.jar:target/classes VoterServer
