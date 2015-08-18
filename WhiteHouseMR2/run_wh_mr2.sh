rm -rf whitehousemr2_classes

rm WhiteHouseMR2.jar

mkdir whitehousemr2_classes

cd src

javac -classpath `yarn classpath` -d ../whitehousemr2_classes WhiteHouseMR2.java

cd ..

jar -cvf WhiteHouseMR2.jar -C whitehousemr2_classes/ .

hadoop fs -rm -r /WhiteHouseMR2

hadoop fs -mkdir /WhiteHouseMR2
hadoop fs -mkdir /WhiteHouseMR2/input

hadoop fs -copyFromLocal WhiteHouse-WAVESReleased-0827.csv /WhiteHouseMR2/input

yarn jar WhiteHouseMR2.jar WhiteHouseMR2 /WhiteHouseMR2/input /WhiteHouseMR2/intermediate /WhiteHouseMR2/output


echo "                         "
echo "printing Top 10 Visitees to WhiteHouse .........................."
echo "                         "

hadoop fs -cat /WhiteHouseMR2/output/part-r-00000 | sort -nrk 1

