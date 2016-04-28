#!/usr/bin/env bash

THISDIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
pushd $THISDIR
java -Djava.library.path=$THISDIR/lib/soar -cp $THISDIR/bin:$THISDIR/lib/stdlib-package.jar:$THISDIR/lib/soar/java/sml.jar edu.umich.eecs.soar.tutorial.SimpleEaters
popd
