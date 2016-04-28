#!/usr/bin/env bash

THISDIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

unamestr=`uname`
if [[ "$unamestr" == 'Linux' ]]; then
	cp $THISDIR/lib/soar/java/swt-linux64.jar $THISDIR/lib/soar/java/swt.jar
elif [[ "$unamestr" == 'Darwin' ]]; then
	cp $THISDIR/lib/soar/java/swt-mac64.jar $THISDIR/lib/soar/java/swt.jar
else
	echo 'Unsupported OS'
	exit 1
fi

pushd $THISDIR
java -Djava.library.path=$THISDIR/lib/soar -cp $THISDIR/bin:$THISDIR/lib/stdlib-package.jar:$THISDIR/lib/soar/java/sml.jar edu.umich.eecs.soar.tutorial.SimpleEaters
popd
