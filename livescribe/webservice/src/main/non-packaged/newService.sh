#!/bin/bash
# 
# newService.sh
# 
# Creates a Maven project for a new Web Service.
# 
# Once invoked with the proper parameters (see 'printUsage()' below),
# the Maven archetype:generate goal will prompt the user for the 
# version (default: '1.0-SNAPSHOT') and groupId (default: the groupId value
# the user supplied when invoking the script).  Accepting the 
# defaults will work in most cases.
# 
# This script assumes a local Nexus repository is available.  To use
# a different repository, change the 'archetypeRepository' URL in the 'mvn' 
# command below.
# 
#==================================================

#===========================================================
#
# printUsage()
# 
# Prints out usage instructions for this script.
#
#===========================================================
function printUsage() {

	echo
	echo "USAGE:  $SCRIPTNAME < groupId > < artifactId >"
	echo
	echo "       groupId - The Maven group ID of the new Web Service."
	echo "    artifactId - The Maven artifact ID of the new Web Service."
	echo
}

#===========================================================
# Start of program.
#===========================================================

INVOKEPATH=`echo $0 | awk -F'/[^/]*.sh$' '{ print $1 }'` 
SCRIPTNAME=`echo $0 | awk -F'/' '{ print $NF }'`

if [[ -z "$1" || -z "$2" ]]
then
	printUsage
	exit 1
fi

groupId=$1
artifactId=$2

if [[ -e "$artifactId" ]]
then
	rm -r "$artifactId"
fi

#--------------------------------------------------
# 
# Archetype Parameters
# archetypeRepository - The Maven repository where the archetype is stored.
#    archetypeGroupId - The 'groupId' of the Maven archetype.
# archetypeArtifactId - The 'artifactId' of the Maven archetype.
#    archetypeVersion - The 'version' of the Maven archetype.
# 
# New Service Parameters
#             groupId - The Maven 'group ID' of the new Service being created.
#          artifactId - The Maven 'artifact ID' of the new Service being created.
# 
mvn -e archetype:generate -DarchetypeRepository=http://jenkins.pensoft.local:8081/nexus/content/repositories/snapshots -DarchetypeGroupId=com.livescribe.archetype -DarchetypeArtifactId=webservice -DarchetypeVersion=1.0-SNAPSHOT -DgroupId=$groupId -DartifactId=$artifactId

