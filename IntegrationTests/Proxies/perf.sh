#!/bin/bash

runCommand() {
typeset cmnd="$*"
typeset ret_code
duredTime=$(($SECONDS-$startTime))
echo lauch $i $command jobs result at $duredTime from the start
eval $cmnd #&> /dev/null
ret_code=$?
if [ $ret_code != 0 ]; then
  echo "Error : [%d] when executing command: '$cmnd'" $ret_code
  exit $ret_code
fi
}

runInfoAndExit()    {
    echo 'first parameter is number o job needed to be execute, should be positive'
    echo 'second parameter is the maven goal (ex : compile), it is optional'
    echo 'Third parameter is racine for repo local (by default : /tmp/perf/test/maven/), it is optional'
    echo 'be aware if the third parameter is not set this program will erase /tmp/perf/test/maven/'
    exit
}

if [ -z "$1" ]; then
    runInfoAndExit
fi

if [ -z "$1" ]; then
    runInfoAndExit
fi

if [ -z "$3" ]; then
    rootRepoLocal=/tmp/perf/test/maven/job
    echo 'Erase all file in /tmp/perf/test/maven/'
    rm -rfv $rootRepoLocal
else
    rootRepoLocal=$3
fi


startTime=$SECONDS
echo $rootRepoLocal
for i in `seq 1 $1`; do
command="mvn $2 -Dmaven.repo.local=$rootRepoLocal/jobs$i&"
runCommand "$command"
done

duredTime=$(($SECONDS-$startTime))
echo Program start $duredTime seconds ago. $SECONDS
echo $(($duredTime/$1)) " seconds/job"