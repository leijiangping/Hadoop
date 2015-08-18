rm -rf minmaxincomemr_classes

rm MinMaxIncomeMR.jar

mkdir minmaxincomemr_classes

cd src

javac -classpath `yarn classpath` -d ../minmaxincomemr_classes ZipIncomeMinMax.java MinMaxIncome.java

cd ..

jar -cvf MinMaxIncomeMR.jar -C minmaxincomemr_classes/ .

hadoop fs -rm -r /MinMaxIncomeMR

hadoop fs -mkdir /MinMaxIncomeMR
hadoop fs -mkdir /MinMaxIncomeMR/input

hadoop fs -copyFromLocal median_income_CA.csv /MinMaxIncomeMR/input

yarn jar MinMaxIncomeMR.jar ZipIncomeMinMax /MinMaxIncomeMR/input /MinMaxIncomeMR/output

echo "                         "
echo "printing 20 zipcode ranges min and max salary .........................."
echo "                         "

hadoop fs -cat /MinMaxIncomeMR/output/part-r-00000 | sort -nrk 1 | head -20


echo "                         "
echo "Total number of zipcode ranges with 10 zipcodes that are valid "
echo "                         "

hadoop fs -cat /MinMaxIncomeMR/output/part-r-00000 | wc -l
