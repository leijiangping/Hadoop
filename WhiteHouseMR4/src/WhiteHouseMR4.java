import java.io.IOException;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WhiteHouseMR4 {
	// Mapper #1: to get the month wise appointments in all years 
	public static class Map extends
			Mapper<LongWritable, Text, IntWritable, IntWritable> {

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			// All comma separated fields of each individual records are stored in String array
			String[] csvFields = line.split(",");
			if (csvFields.length > 1) {
				String field = csvFields[10];
				
				// Below lines uses pattern matching to get the month and year values of field 10
				Pattern pattern = Pattern.compile("(\\d+)\\/(\\d+)\\/(\\d+)");
				Matcher m = pattern.matcher(field);
				
				IntWritable month = null;
				IntWritable year = new IntWritable(2009);
				int year_int = 2009;
				if (m.find()) {
					month = new IntWritable(Integer.parseInt(m.group(1)));
					year_int = Integer.parseInt(m.group(3));
					if (year_int > 2001 && year_int < 2013) {
						year = new IntWritable(year_int);
					}
				}
				if (month != null)
					context.write(month, year);
			}
		}
	}

	// Reducer #1 for Mapper #1
	public static class Reduce extends
			Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
		@Override
		public void reduce(IntWritable key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			HashSet<Integer> hash = new HashSet<Integer>();
			int count = 0;
			for (IntWritable val : values) {
				count++;
				hash.add(val.get());
			}
			int unique = hash.size();
			int average = count / unique;
			//  This outputs the intermediate (key,value) pair
			context.write(key, new IntWritable(average));
		}
	}

	public static void main(String[] args) throws Exception {
		
		Configuration conf1 = new Configuration();
		Job job1 = Job.getInstance(conf1, "Average Appointments made per month (all years) to WhiteHouse");
		
		job1.setJarByClass(WhiteHouseMR4.class);
		job1.setOutputKeyClass(IntWritable.class);
		job1.setOutputValueClass(IntWritable.class);
		
		job1.setMapperClass(Map.class);
		job1.setReducerClass(Reduce.class);
		
		FileInputFormat.addInputPath(job1, new Path(args[0]));
		FileOutputFormat.setOutputPath(job1, new Path(args[1]));
		
		job1.waitForCompletion(true);
	}
}