#!/bin/bash
#
# wtt.sh
# 
# This script provides a command-line interface to the Web Team Tool.
# 
#===============================================================================
echo
#===========================================================
#
# printUsage()
# 
# Prints out usage instructions for this script.
#
#===========================================================
function printUsage() {

	echo
	echo "USAGE:  "
	echo
	echo "    $ $SCRIPTNAME [ option ] [ parameters ... ]"
	echo
	echo "    option - "
	echo "        -d - Convert display ID into numeric serial number."
	echo "        -e - Validate OAuth access token belonging to the given email address."
	echo "        -f - Hash the given password string used by WebObjects/FrontBase."
	echo "        -i - Install"
	echo "        -s - Convert numeric serial number into display ID."
	echo "        -o - Validate the given OAuth access token."
	echo "        -p - List system properties."
	echo "        -q - Determine a user's upload quota."
	echo "        -t - Find OAuth Access Token with the given email address."
	echo "        -u - Uninstall"
	echo "        -w - Generate a hash of the given string using the Sky/Vector algorithm.."
	echo "        -g - Generate classes for the given XML Schema."
	echo "       -tp - Package to place generated class files into."
	echo "     -fbpk - Primary key of user account in FrontBase."
	echo
	echo " parameters"
	echo
}

#===========================================================
#
# install()
# 
# 
#
#===========================================================
function install() {
	
	if [[ ! -e "$INSTALL_HOME" ]]
	then
		mkdir -p "$INSTALL_HOME"
	fi
	
	if [[ ! -e "$INSTALL_BASE/bin" ]]
	then
		mkdir -p "$INSTALL_BASE/bin"
	fi
	
	cp ${project.build.finalName}.jar "$INSTALL_HOME"
	cp wtt.sh "$INSTALL_HOME"
	
	ln -s "$INSTALL_HOME/wtt.sh" "$INSTALL_BASE/bin/wtt"
	
	svcPath=`echo $PATH | grep "/Livescribe/Services/bin"`
	if [[ -z "$svcPath" ]]
	then
		#  Append /Livescribe/Services/bin to PATH variable.
		echo >> ~/.bash_profile
		echo "#  Added by Web Team Tool $now" >> ~/.bash_profile
		echo export PATH=\$PATH:/Livescribe/Services/bin >> ~/.bash_profile
		
		#  Read in the new PATH values.
		source ~/.bash_profile
	fi
	
}

#===========================================================
#
# uninstall()
# 
# 
#
#===========================================================
function uninstall() {
	
	pushd "$INSTALL_BASE"
	rm webteamtool/*
	rm -rf webteamtool
	rm bin/wtt
	popd
	
	#sed '$s/:\/Livescribe\/Services\/bin//g' ~/.bash_profile
}

#===========================================================
#
# printMenu()
# 
# 
#
#===========================================================
function printMenu() {
	
	echo
	echo "	0.  local"
	echo "	1.  DEV"
	echo "	2.  QA"
	echo "	3.  STAGE"
	echo "	4.  PROD	(Make sure there is connectivity to this database.)"
	echo
}

#===============================================================================
#	Start of script.
#===============================================================================
echo "Web Team Tool v${project.version}"
echo "Built ${maven.build.timestamp}"

INVOKEPATH=`echo $0 | awk -F'/[^/]*.sh$' '{ print $1 }'` 
SCRIPTNAME=`echo $0 | awk -F'/' '{ print $NF }'`

#INSTALL_BASE="/usr/share/java"
INSTALL_BASE="/Livescribe/Services"
INSTALL_HOME="$INSTALL_BASE/webteamtool"

now=`date "+%Y-%m-%d %H:%M"`
svcPath=`echo $PATH | grep "/Livescribe/Services/bin"`

selected_env=-0

printMenu

read -p "Select which environment to use:  " selected_env
echo

case $selected_env in
	0)
		export ENV=local
		;;
	1)
		export ENV=DEV
		;;
	2)
		export ENV=QA
		;;
	3)
		export ENV=STAGE
		;;
	4)
		export ENV=PROD
		;;
	*)
		printUsage
		exit
		;;
esac

echo "[$SCRIPTNAME]:  Using $ENV environment ..."
echo

param1=$1

if [[ -z "$param1" ]]
then
	echo
	echo "[$SCRIPTNAME]:  No parameters found."
	printUsage
	echo
	exit 1
fi

option=${param1:0:1}

if [[ "$option" != "-" ]]
then
	echo 
	echo "[$SCRIPTNAME]:  No option found."
	printUsage
	echo
	exit 2
fi

params="$2 $3 $4 $5"

CMD="java -DENV=$ENV -jar $INSTALL_HOME/${project.build.finalName}.jar "

case $1 in
	'-d')
		$CMD -d $params
		;;
	'-e')
		$CMD -e $params
		;;
	'-i')
		install
		;;
	'-s')
		$CMD -s $params
		;;
	'-o')
		$CMD -o $params
		;;
	'-p')
		$CMD -p 
		;;
	'-q')
		echo "$CMD -q $params"
		echo
		$CMD -q $params
		;;
	'-t')
		$CMD -t $params
		;;
	'-u')
		uninstall
		;;
	'-w')
		$CMD -w $params
		;;
	'-f')
		$CMD -f $params
		;;
	'-g')
		$CMD -g $params
		;;
	'-fbpk')
		$CMD -fbpk $params
		;;
	*)
		printUsage
		exit 1
		;;
esac
