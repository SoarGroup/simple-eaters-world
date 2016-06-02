#!/usr/bin/env bash

THISDIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
FLAG=""

unamestr=`uname`
if [[ "$unamestr" == 'Linux' ]]; then
	cp $THISDIR/lib/soar/java/swt-linux64.jar $THISDIR/lib/soar/java/swt.jar
elif [[ "$unamestr" == 'Darwin' ]]; then
	cp $THISDIR/lib/soar/java/swt-mac64.jar $THISDIR/lib/soar/java/swt.jar
	FLAG="-XstartOnFirstThread"
else
	echo 'Unsupported OS'
	exit 1
fi

pushd $THISDIR > /dev/null
java $FLAG -Djava.library.path=$THISDIR/lib/soar -jar lib/soar/SoarJavaDebugger.jar &
popd > /dev/null
