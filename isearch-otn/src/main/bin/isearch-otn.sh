#!/bin/sh
usage()
{
   echo "Usage: `basename $0` "
   echo ""
   echo "Options:"
   echo "   -e emsid "
   exit 0
}

while getopts "e:" OPTION
do
    case $OPTION in
    e)emsid=$OPTARG
              ;;
    *|\?)usage
              ;;
    esac
done

cd ..
isearchhome=`pwd`
echo "isearchhome is ${isearchhome}"

ProcessName=ISearch-otn
FILE_PID=${isearchhome}/${ProcessName}.pid
FILE_OUT=${isearchhome}/${ProcessName}.out
FILE_ERR=${isearchhome}/${ProcessName}.err

pid=`cat $FILE_PID | awk '{print $1}'`
if [ "${pid}" != "" ]
then
    kill -15 $pid
fi
echo "old pid is $pid"

JAVA_OPTS="-server -Xmx2048m"
JAVA_OPTS="$JAVA_OPTS -Disearch.log.dir=${isearchhome}/log"
JAVA_OPTS="$JAVA_OPTS "-Dlog4j.configuration="file:${isearchhome}/cfg/log4j.xml"
 
export ICLASSPATH=${isearchhome}/lib
 
CLASSPATH=${isearchhome}/cfg
CLASSPATH=$CLASSPATH:$ICLASSPATH
CLASSPATH=$CLASSPATH:${isearchhome}/lib/isearch-otn-0.0.1-SNAPSHOT.jar
CLASSPATH=$CLASSPATH:${isearchhome}/lib/classworlds-1.0.1.jar
 
BOOT_CONF=${isearchhome}/cfg/boot.conf
BOOT_OPTS="$BOOT_OPTS -Dclassworlds.conf=$BOOT_CONF"
BOOT_OPTS="$BOOT_OPTS -Disearch.home=${isearchhome}"

echo $CLASSPATH
echo "starting $ProcessName..."
date>>$FILE_OUT
echo "starting $ProcessName...">>$FILE_OUT
java -D"$ProcessName"  -cp $CLASSPATH $JAVA_OPTS $BOOT_OPTS  org.codehaus.classworlds.Launcher 1>>$FILE_OUT 2>>$FILE_ERR &
pid=$!
echo $pid >$FILE_PID
echo "new process id is $pid"
date>>$FILE_OUT
echo "new process id is $pid">>$FILE_OUT
