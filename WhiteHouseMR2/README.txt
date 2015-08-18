%Open the java project WhiteHouseMR2 in eclipse, which has mapreduce code in src file

%% Create a classes directory in project root directory:
mkdir whitehousemr2_classes

cd src

%% Compile the MapRecuce Java Code with YARN Classpath after going inside src folder
javac -classpath `yarn classpath` -d ../whitehousemr2_classes WhiteHouseMR2.java

cd ..

%% Create a JAR file needed to run MapReduce job
jar -cvf WhiteHouseMR2.jar -C whitehousemr2_classes/ .

Assuming we have a input directory with data in hdfs , we can run below command  (Alternatively run the shell script in the same folder)

%% Now run the job as below :
yarn jar WhiteHouseMR2.jar WhiteHouseMR2 /WhiteHouseMR2/input /WhiteHouseMR2/intermediate /WhiteHouseMR2/output
