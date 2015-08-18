%Open the java project WhiteHouseMR4 in eclipse, which has mapreduce code in src file

%% Create a classes directory in project root directory:
mkdir whitehousemr4_classes

cd src

%% Compile the MapRecuce Java Code with YARN Classpath after going inside src folder
javac -classpath `yarn classpath` -d ../whitehousemr4_classes WhiteHouseMR4.java

cd ..

%% Create a JAR file needed to run MapReduce job
jar -cvf WhiteHouseMR4.jar -C whitehousemr4_classes/ .

Assuming we have a input directory with data in hdfs , we can run below command  (Alternatively run the shell script in the same folder)

%% Now run the job as below :
yarn jar WhiteHouseMR4.jar WhiteHouseMR4 /WhiteHouseMR4/input /WhiteHouseMR4/output
