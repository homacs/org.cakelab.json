package org.cakelab.json.codec.samples;

public class ArrayMembers {

	long[] int_array_1;
	double[] double_array_2;
	Primitives[] object_array_3;
	
	
	public ArrayMembers() {
		int_array_1 = new long[]{1,2,3,4,5};
		double_array_2 = new double[]{1,2,3,4,5};
		
		object_array_3 = new Primitives[]{new Primitives(), new Primitives()};
	}
	
}
