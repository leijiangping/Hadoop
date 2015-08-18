rm -rf whitehousemr4_classes

rm WhiteHouseMR4.jar

mkdir whitehousemr4_classes

cd src

javac -classpath `yarn classpath` -d ../whitehousemr4_classes WhiteHouseMR4.java

cd ..

jar -cvf WhiteHouseMR4.jar -C whitehousemr4_classes/ .

hadoop fs -rm -r /WhiteHouseMR4

hadoop fs -mkdir /WhiteHouseMR4
hadoop fs -mkdir /WhiteHouseMR4/input

hadoop fs -copyFromLocal WhiteHouse-WAVESReleased-0827.csv /WhiteHouseMR4/input

yarn jar WhiteHouseMR4.jar WhiteHouseMR4 /WhiteHouseMR4/input /WhiteHouseMR4/output


echo "                         "
echo "Printing Average Appointments made per month (all years) to WhiteHouse .........................."
echo "                         "

hadoop fs -cat /WhiteHouseMR4/output/part-r-00000 | sort -nrk 1

