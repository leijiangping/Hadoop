%Open the java project WhiteHouseMR1 in eclipse, which has mapreduce code in src file

%% Create a classes directory in project root directory:
mkdir whitehousemr1_classes

cd src

%% Compile the MapRecuce Java Code with YARN Classpath after going inside src folder
javac -classpath `yarn classpath` -d ../whitehousemr1_classes WhiteHouseMR1.java

cd ..

%% Create a JAR file needed to run MapReduce job
jar -cvf WhiteHouseMR1.jar -C whitehousemr1_classes/ .

Assuming we have a input directory with data in hdfs , we can run below command  (Alternatively run the shell script in the same folder)

%% Now run the job as below :
yarn jar WhiteHouseMR1.jar WhiteHouseMR1 /WhiteHouseMR1/input /WhiteHouseMR1/intermediate /WhiteHouseMR1/output
