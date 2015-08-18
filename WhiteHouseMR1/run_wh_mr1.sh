rm -rf whitehousemr1_classes

rm WhiteHouseMR1.jar

mkdir whitehousemr1_classes

cd src

javac -classpath `yarn classpath` -d ../whitehousemr1_classes WhiteHouseMR1.java

cd ..

jar -cvf WhiteHouseMR1.jar -C whitehousemr1_classes/ .

hadoop fs -rm -r /WhiteHouseMR1

hadoop fs -mkdir /WhiteHouseMR1
hadoop fs -mkdir /WhiteHouseMR1/input

hadoop fs -copyFromLocal WhiteHouse-WAVESReleased-0827.csv /WhiteHouseMR1/input

yarn jar WhiteHouseMR1.jar WhiteHouseMR1 /WhiteHouseMR1/input /WhiteHouseMR1/intermediate /WhiteHouseMR1/output


echo "                         "
echo "printing Top 10 Visitors to WhiteHouse .........................."
echo "                         "

hadoop fs -cat /WhiteHouseMR1/output/part-r-00000 | sort -nrk 1

