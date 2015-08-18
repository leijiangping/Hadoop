%Open the java project MinMaxIncomeMR in eclipse, which has mapreduce code in src file

%% Create a classes directory in project root directory:
mkdir minmaxincomemr_classes

cd src

%% Compile the MapRecuce Java Code with YARN Classpath after going inside src folder
javac -classpath `yarn classpath` -d ../minmaxincomemr_classes ZipIncomeMinMax.java MinMaxIncome.java

cd ..

%% Create a JAR file needed to run MapReduce job
jar -cvf MinMaxIncomeMR.jar -C minmaxincomemr_classes/ .

Assuming we have a input directory with data in hdfs , we can run below command  (Alternatively run the shell script in the same folder)

%% Now run the job as below :
yarn jar MinMaxIncomeMR.jar ZipIncomeMinMax /MinMaxIncomeMR/input /MinMaxIncomeMR/output
