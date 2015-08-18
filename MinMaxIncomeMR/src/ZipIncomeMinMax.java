import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class ZipIncomeMinMax {
	
	public static class IncomeMapper extends
	Mapper<Object, Text, Text, MinMaxIncome> {

private Text zipcode = new Text();
private Integer minincome;
private Integer maxincome;

private MinMaxIncome outPut = new MinMaxIncome();

public enum RecordCounters {
	ValidZipCounter
}

public void map(Object key, Text value, Context context)
		throws IOException, InterruptedException {

	String[] incomeDataFields = value.toString().split(",");
	
	// sample data
	// 8600000US94028,94028,"94028 5-Digit ZCTA, 940 3-Digit ZCTA",161323,180272,91172  

	
	// checking if a zipcode is a valid integer
	if (isInteger(incomeDataFields[1]))

	{
		// initializing counter with 0 for record 0 , we are counting records with 0 as first record for a logic
		context.getCounter(RecordCounters.ValidZipCounter).increment(0);
		zipcode.set(incomeDataFields[1]);

		minincome = Integer.parseInt(incomeDataFields[4]);
		maxincome = Integer.parseInt(incomeDataFields[4]);

	} else {
		return;
	}

	if (zipcode == null || minincome == null || maxincome == null) {
		return;
	}
	try {
		outPut.setMinIncome(minincome);
		outPut.setMaxIncome(maxincome);
		

		if (context.getCounter(RecordCounters.ValidZipCounter).getValue() == 0) {
			context.write(new Text("ZipRange " + new Long(0).toString()), outPut);

		} else {
			context.write(
					new Text("ZipRange " + new Long(context.getCounter(
							RecordCounters.ValidZipCounter).getValue() / 10)
							.toString()), outPut);
		}
		
		// ValidZipCounter will have the count or records with valid zipcode 
		context.getCounter(RecordCounters.ValidZipCounter).increment(1);

	} catch (IOException e) { // TODO Auto-generated catch block
		e.printStackTrace();
	}

}
}

public static class IncomeReducer extends
	Reducer<Text, MinMaxIncome, Text, MinMaxIncome> {
private MinMaxIncome resultRow = new MinMaxIncome();

public void reduce(Text key, Iterable<MinMaxIncome> values,
		Context context) throws IOException, InterruptedException {
	Integer minincome = 0;
	Integer maxincome = 0;

	resultRow.setMinIncome(null);
	resultRow.setMaxIncome(null);

	for (MinMaxIncome val : values) {

		minincome = val.getMinIncome();
		maxincome = val.getMaxIncome();
		
		
		// get min income for a zip range of 10 records

		if (resultRow.getMinIncome() == null
				|| minincome.compareTo(resultRow.getMinIncome()) < 0) {
			resultRow.setMinIncome(minincome);
		}

		// get max income for a zip range of 10 records
		if (resultRow.getMaxIncome() == null
				|| maxincome.compareTo(resultRow.getMaxIncome()) > 0) {
			resultRow.setMaxIncome(maxincome);
		}
	} // end of for loop
	
	
	context.write(key, resultRow);
}
}

// helper method to find if a zipcode is an integer

public static boolean isInteger(String s) {
return isInteger(s, 10);
}

public static boolean isInteger(String s, int radix) {
if (s.isEmpty())
	return false;
for (int i = 0; i < s.length(); i++) {
	if (i == 0 && s.charAt(i) == '-') {
		if (s.length() == 1)
			return false;
		else
			continue;
	}
	if (Character.digit(s.charAt(i), radix) < 0)
		return false;
}
return true;
}

public static void main(String[] args) throws Exception {
Configuration conf = new Configuration();
Job job = Job.getInstance(conf, "Zipcode Range Min Max Income Aggregation");
job.setJarByClass(ZipIncomeMinMax.class);
job.setMapperClass(IncomeMapper.class);
job.setCombinerClass(IncomeReducer.class);
job.setReducerClass(IncomeReducer.class);
job.setOutputKeyClass(Text.class);
job.setOutputValueClass(MinMaxIncome.class);
FileInputFormat.addInputPath(job, new Path(args[0]));
FileOutputFormat.setOutputPath(job, new Path(args[1]));
System.exit(job.waitForCompletion(true) ? 0 : 1);
}

}
