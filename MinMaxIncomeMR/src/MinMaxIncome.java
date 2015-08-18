import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class MinMaxIncome implements Writable {

	// member variables
	Integer minIncome;
	Integer maxIncome;

	// constructor
	public MinMaxIncome() {
		minIncome = 0;
		maxIncome = 0;
	}

	// setter methods
	void setMinIncome(Integer duration) {
		this.minIncome = duration;
	}

	void setMaxIncome(Integer duration) {
		this.maxIncome = duration;
	}

	// getter methods
	Integer getMinIncome() {
		return minIncome;
	}

	Integer getMaxIncome() {
		return maxIncome;
	}

	// write method
	public void write(DataOutput out) throws IOException {
		// The Order in which we want to write results
		out.writeInt(minIncome);
		out.writeInt(maxIncome);
	}

	// readFields Method
	public void readFields(DataInput in) throws IOException {
		minIncome = new Integer(in.readInt());
		maxIncome = new Integer(in.readInt());
	}

	public String toString() {
		return minIncome + "\t" + maxIncome;
	}

}