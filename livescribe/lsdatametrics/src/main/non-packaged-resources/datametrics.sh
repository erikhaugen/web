#!/bin/bash
# 
# Not for MacOSX!!
# 

#===============================================================================
# 
# getEnvName()
# 
# Maps the hostname to the environment
# *-test[.*] => dev
# *-qa[.*] => qa
# *-stage[.*] => staging
# *[.*] => prod
#===============================================================================
function getEnvName() {
    host=`hostname`
    
    envSuffix=`echo $host | awk -F'.' '{ print $1 }' | awk -F'-' '{ print $2 }'`
    case "$envSuffix" in
    'test' )
         echo "dev"
         ;;
    'qa' )
         echo "qa"
         ;;
    'stage' )
         echo "staging"
         ;;
    'MacBook' )
         echo "local"
         ;;
    '' )
         echo "prod"
         ;;
    esac
    return 0
}

#===========================================================
# 
# toLower()
# 
#===========================================================
function toLower() {
  echo $1 | tr "[:upper:]" "[:lower:]"
}

#===========================================================
#
# printUsage()
# 
# Prints out usage instructions for this script.
#
#===========================================================
function printUsage() {

	echo
	echo "USAGE:  $SCRIPTNAME { start | stop | status | restart | install | uninstall | version }"
	echo
}

#===========================================================
#
# startDaemon()
# 
# Starts the manager.
#
#===========================================================
function startDaemon() {

	if [[ -z "$PID" || "$PID" == "" ]]
	then
		echo
		echo "Starting ..."
		echo
		
		java -DENV=$ENV -Dlogfile=$logfileprefix -jar $INSTALL_HOME/${project.build.finalName}.jar &
		
		echo "Started."
		echo
		echo "NOTE:  You can find the log file in:  $INSTALL_HOME/Logs"
		echo
	else
		echo
		echo "Process already running."
		echo
	fi
}

#===========================================================
#
# stopDaemon()
# 
# Stops the manager.
#
#===========================================================
function stopDaemon() {

	if [[ -z "$PID" || "$PID" == "" ]]
	then
		echo
		echo "Data Metrics daemon is already stopped."
		echo
	else
		echo
		echo "Stopping ..."
		echo
		kill $PID
		echo "Stopped."
		echo
	fi
}

#===========================================================
#
# uninstallDaemon()
# 
# Uninstalls the manager.
#
#===========================================================
function uninstallDaemon() {

	# Remove symlink
	rm $INSTALL_BASE/bin/datametrics
	
	echo
	echo "Removed symlink:  $INSTALL_BASE/bin/datametrics"
	
	# Remove application from /Livescribe/Services/LSDataMetrics
	rm -rf $INSTALL_HOME
	echo
	echo "Removed $INSTALL_HOME"
	echo
}

INVOKEPATH=`echo $0 | awk -F'/[^/]*.sh$' '{ print $1 }'`
SCRIPTNAME=`echo $0 | awk -F'/' '{ print $NF }'`

INSTALL_BASE="/Livescribe/Services"
INSTALL_HOME="$INSTALL_BASE/LSDataMetrics"

# echo
# echo "INVOKEPATH:  $INVOKEPATH"
# echo "SCRIPTNAME:  $SCRIPTNAME"

ACTION=$1

# Check for existence of command.
if [[ -z "$ACTION" ]]
then
	printUsage
	exit 1
fi

# If an environment was provided, use that value.
if [[ ! -z "$2" ]]
then
	ENV=$2	
fi

# If the environment variable is not set, try to get it from hostname
if [[ -z "$ENV" ]]
then
    ENV=`getEnvName`
fi

# If we still cannot find the ENV
if [[ "$ENV" == "" || "$ENV" == "0" ]]    
then
	echo
	echo 'Environment variable (ENV) not set.'
	echo
	printUsage
	exit 1
fi

export ENV=`toLower $ENV`

echo
echo "Using '$ENV' environment."

# Set the log file prefix
logfileprefix=`hostname | awk -F. '{print $1}'`
export logfileprefix=$logfileprefix
echo
echo "Using '$logfileprefix' as log file prefix."

# Make sure JAVA_HOME is set.
if [[ -z $JAVA_HOME ]]
then
	echo
	echo "*** WARNING:  JAVA_HOME not set."
	JAVA_HOME="/usr/java/jdk1.6.0_21"
	echo "Using $JAVA_HOME"
#	echo "Please set the JAVA_HOME variable and re-run this script."
	echo
#	exit 2
fi

# Find the PID of any possible running instance of this script.
#PID=`ps -ef | grep '${project.build.finalName}' | grep -v 'grep' | awk '{print $2}'`
PID=`ps -ef | grep "$INSTALL_HOME" | grep -v 'grep' | awk '{print $2}'`
echo
echo "PID of process is:  $PID"

############################################################
#             S T A R T   O F   S C R I P T
############################################################
case "$ACTION" in

	# Launches the Data Metrics Transfer service.
	start)
		startDaemon
		;;
	# Stops the running instance of this script.
	stop)
		stopDaemon
		;;
	# Checks the running status of this script.
	status)
		echo
		echo "DataMetrics Status"
		echo

		if [[ -z "$PID" || "$PID" == "" ]]
		then
			echo "Process is NOT running."
			echo
		else 
			echo "Process IS already running."
			echo
		fi
		;;
	restart)
		stopDaemon
		
		# Wait for the process to be terminated.
		while [[ -n "$PID" && "$PID" != "" ]]
		do
			# Find the PID of any possible running instance of this script.
			PID=`ps -ef | grep '${project.build.finalName}' | grep -v 'grep' | awk '{print $2}'`
		done

		startDaemon
		;;
	install)
	
		# Check to see if user has permissions to create directories.
		if [[ ! -w "/Livescribe" ]]
		then
			echo
			echo "ERROR:  You do not have permissions to write to the '$INSTALL_BASE' directory as '$USER'."
			echo
			exit 3
		fi
		
		
		# Uninstall the previous version.
		if [[ -e "$INSTALL_HOME" ]]
		then
			stopDaemon
			uninstallDaemon
		fi
		
		if [[ ! -e "$INSTALL_BASE" ]]
		then
			# This first 'mkdir' appears necessary as some systems are not creating 
			# the entire directory path with the '-p' option.
			mkdir -p $INSTALL_BASE
			
			echo
			echo "Created $INSTALL_BASE ..."
		fi
		
		# Check if the install directory already exists.
		if [[ ! -e "$INSTALL_HOME" ]]
		then			
			# Create the installation home directory.
			mkdir -p $INSTALL_HOME
			echo
			echo "Created $INSTALL_HOME ..."
			
			# Create the Logs directory.
			mkdir -p $INSTALL_HOME/Logs
			echo
			echo "Created $INSTALL_HOME/Logs ..."
		fi
		
		if [[ ! -e "$INSTALL_BASE/bin" ]]
		then
			
			# Create the 'bin' folder.
			mkdir -p $INSTALL_BASE/bin
			
			echo
			echo "Created $INSTALL_BASE/bin ..."
		fi
		
		# Copy resources to /Livescribe/Services/LSDataMetrics.
		cp -f ${project.build.finalName}.jar $INSTALL_HOME
		cp -f $SCRIPTNAME $INSTALL_HOME
		
		# Create symlink to shell script.
		ln -sf $INSTALL_HOME/datametrics.sh $INSTALL_BASE/bin/datametrics
		
		echo
		echo "Installation complete."
		echo
		;;
	uninstall)
		stopDaemon
		uninstallDaemon
		;;
	version)
		echo
		echo "App-Version:  ${project.version}"
		echo "SVN-Version:  ${buildNumber}"
		echo "Hudson-Build-Number:  ${hudson-build-number}"
		echo "Build-Date:  ${timestamp}"
		echo
		;;
	*)
		printUsage
		exit 1
		;;
esac
exit $?
