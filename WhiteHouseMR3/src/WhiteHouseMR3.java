import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WhiteHouseMR3 {
	// Mapper #1 : This Mapper gets the Visitee Count from input CSV file
	public static class Map extends
			Mapper<LongWritable, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			String[] csvFields1 = line.split(",", -1);
			List<String> csvFields2 = Arrays.asList(line.split(",", -1));
			String visitor = csvFields1[0] + " " + csvFields1[1];
			String visitee = csvFields2.get(19) + " " + csvFields2.get(20);
			String Output3 = visitor + " " + visitee;
			String Final_Output = Output3.toLowerCase();
			word.set(Final_Output);
			context.write(word, one);
		}
	}

	// Mapper #2 : This Mapper is used to get the Top Ten  
	public static class TopTenMapper extends
			Mapper<LongWritable, Text, NullWritable, Text> {
		private TreeMap<Integer, Text> localTopTen = new TreeMap<Integer, Text>();

		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			StringTokenizer tokenizer = new StringTokenizer(line, "\t");
			String visitorName = "Null";
			int visitorCount = 0;
			if (tokenizer.hasMoreTokens()) {
				visitorName = tokenizer.nextToken();
			}
			if (tokenizer.hasMoreTokens()) {
				visitorCount = Integer.parseInt(tokenizer.nextToken());
			}
			localTopTen.put(visitorCount, new Text(visitorCount + "\t"
					+ visitorName));
			if (localTopTen.size() > 10) {
				localTopTen.remove(localTopTen.firstKey());
			}
		}

		@Override
		protected void cleanup(Context context) throws IOException,
				InterruptedException {
			for (Text t : localTopTen.values()) {
				context.write(NullWritable.get(), t);
			}
		}
	}

	// Reducer #1 : This is the Reducer for Mapper #1
	public static class Reduce extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			context.write(key, new IntWritable(sum));
		}
	}

	// Reducer #2 : This is the Reducer for Mapper #2
	public static class TopTenReducer extends
			Reducer<NullWritable, Text, NullWritable, Text> {
		private TreeMap<Integer, Text> localTopTen = new TreeMap<Integer, Text>();
		private Text finalOutput = new Text();
		private int ctr = 0;

		public void reduce(NullWritable key, Iterator<Text> values,
				Context context) throws IOException, InterruptedException {
			while (values.hasNext()) {
				String[] valueStr = values.next().toString().split("\t");
				String visitorName = valueStr[1];
				int visitorCount = Integer.parseInt(valueStr[0]);
				finalOutput.set("" + visitorCount + "\t" + visitorName);
				localTopTen.put(visitorCount, finalOutput);
				if (localTopTen.size() > 10) {
					localTopTen.remove(localTopTen.firstKey());
				}
			}
			for (Text t : localTopTen.values()) {
				context.write(NullWritable.get(), t);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf1 = new Configuration();
		Job job1 = Job.getInstance(conf1,
				"Most Frequent Visitor-Visitee Combination to WhiteHouse");

		job1.setJarByClass(WhiteHouseMR3.class);
		job1.setOutputKeyClass(Text.class);
		job1.setOutputValueClass(IntWritable.class);
		job1.setMapperClass(Map.class);
		job1.setCombinerClass(Reduce.class);
		job1.setReducerClass(Reduce.class);
		FileInputFormat.addInputPath(job1, new Path(args[0]));
		FileOutputFormat.setOutputPath(job1, new Path(args[1]));

		job1.waitForCompletion(true);

		if (job1.isSuccessful()) {
			Configuration conf2 = new Configuration();
			Job job2 = Job
					.getInstance(conf2,
							"Top 10 Most Frequent Visitor-Visitee Combination to WhiteHouse");

			job2.setJarByClass(WhiteHouseMR3.class);
			job2.setOutputKeyClass(NullWritable.class);
			job2.setOutputValueClass(Text.class);

			job2.setMapperClass(TopTenMapper.class);
			job2.setCombinerClass(TopTenReducer.class);
			job2.setReducerClass(TopTenReducer.class);

			FileInputFormat.addInputPath(job2, new Path(args[1]));
			FileOutputFormat.setOutputPath(job2, new Path(args[2]));

			job2.setNumReduceTasks(1);
			job2.waitForCompletion(true);
		}
	}
}
