#!/bin/bash
#===========================================================
#
# printUsage()
# 
# Prints out usage instructions for this script.
#
#===========================================================
function printUsage() {
	echo
	echo "USAGE:  $SCRIPTNAME"
	echo
	echo "        env           - One of:  {local|dev|qa|stage|prod}"
	echo
}

#===========================================================
# 
# toUpper()
# 
# Converts the given string to all upper case.  (dev --> DEV)
# 
#===========================================================
function toUpper() {
  echo $1 | tr "[:lower:]" "[:upper:]"
}

#
# The location of where this script was invoked.
#
INVOKEPATH=`echo $0 | awk -F'/[^/]*.sh$' '{ print $1 }'`

#
# The name of this script.
#
SCRIPTNAME=`echo $0 | awk -F'/' '{ print $NF }'`

# Make sure JAVA_HOME is set.
if [[ -z $JAVA_HOME ]]
then
	echo
	echo "*** WARNING:  JAVA_HOME not set!"
	echo 
	echo "Please set the JAVA_HOME variable and re-run this script."
	echo
	echo "e.g.  export JAVA_HOME=/usr/java/jdk1.6.0_21  (Linux)"
	echo "                - or -"
	echo "      export JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/1.6/Home  (Mac OSX)"
	echo
	exit 2
fi

# Check that environment variable is set.
if [[ -z "$ENV" ]]
then
	echo
	echo "Environment variable (ENV) not set."
	echo
	echo "e.g.:  export ENV=dev"
	echo
	printUsage
	exit 1
fi

# Construct config filename.
CONFIG_FILE="conf_`toUpper $ENV`.properties"
echo
echo "Using '$CONFIG_FILE' for configuration ..."
echo
echo "$JAVA_HOME/bin/java -jar ${project.build.finalName}.jar -configFile $CONFIG_FILE"
echo

$JAVA_HOME/bin/java -Dlogfile=$CONFIG_FILE -jar ${project.build.finalName}.jar -configFile $CONFIG_FILE

echo
echo "Tool execution complete."
echo
exit
