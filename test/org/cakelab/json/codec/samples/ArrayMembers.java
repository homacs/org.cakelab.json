package org.cakelab.json.codec.samples;

import java.util.Arrays;

public class ArrayMembers {

	long[] int_array_1;
	double[] double_array_2;
	Primitives[] object_array_3;
	
	
	public ArrayMembers() {
		int_array_1 = new long[]{1,2,3,4,5};
		double_array_2 = new double[]{1,2,3,4,5};
		
		object_array_3 = new Primitives[]{new Primitives(), new Primitives()};
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(double_array_2);
		result = prime * result + Arrays.hashCode(int_array_1);
		result = prime * result + Arrays.hashCode(object_array_3);
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArrayMembers other = (ArrayMembers) obj;
		return Arrays.equals(double_array_2, other.double_array_2) && Arrays.equals(int_array_1, other.int_array_1)
				&& Arrays.equals(object_array_3, other.object_array_3);
	}
	
}
