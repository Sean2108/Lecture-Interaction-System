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

rmiregistry &
java -jar -Djava.security.policy=policy.txt target/lis-0.0.1-SNAPSHOT.jar
#java -Djava.security.policy=policy.txt -cp target/classes:src/main/resources VoterServer
