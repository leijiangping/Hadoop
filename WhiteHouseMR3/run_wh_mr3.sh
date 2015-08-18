rm -rf whitehousemr3_classes

rm WhiteHouseMR3.jar

mkdir whitehousemr3_classes

cd src

javac -classpath `yarn classpath` -d ../whitehousemr3_classes WhiteHouseMR3.java

cd ..

jar -cvf WhiteHouseMR3.jar -C whitehousemr3_classes/ .

hadoop fs -rm -r /WhiteHouseMR3

hadoop fs -mkdir /WhiteHouseMR3
hadoop fs -mkdir /WhiteHouseMR3/input

hadoop fs -copyFromLocal WhiteHouse-WAVESReleased-0827.csv /WhiteHouseMR3/input

yarn jar WhiteHouseMR3.jar WhiteHouseMR3 /WhiteHouseMR3/input /WhiteHouseMR3/intermediate /WhiteHouseMR3/output


echo "                         "
echo "printing Top 10 Visitor-Visitee Combination to WhiteHouse .........................."
echo "                         "

hadoop fs -cat /WhiteHouseMR3/output/part-r-00000 | sort -nrk 1

