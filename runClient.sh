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

java -cp target/classes VoterClient
