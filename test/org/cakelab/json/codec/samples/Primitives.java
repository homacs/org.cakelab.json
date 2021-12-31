package org.cakelab.json.codec.samples;

import java.util.Objects;

public class Primitives {
	int int_1;
	double double_2;
	String string_3;
	String string_null_4;
	
	byte byte_5;
	char char_6;
	short short_7;
	float float_8;
	boolean boolean_9;
	
	public Primitives () {
		int_1 = 1;
		double_2 = 2.0;
		string_3 = "drei";
		string_null_4 = null;

		byte_5 = 5;
		char_6 = 6;
		short_7 = 7;
		float_8 = 8.0f;
		boolean_9 = true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(boolean_9, byte_5, char_6, double_2, float_8, int_1, short_7, string_3, string_null_4);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Primitives other = (Primitives) obj;
		return boolean_9 == other.boolean_9 && byte_5 == other.byte_5 && char_6 == other.char_6
				&& Double.doubleToLongBits(double_2) == Double.doubleToLongBits(other.double_2)
				&& Float.floatToIntBits(float_8) == Float.floatToIntBits(other.float_8) && int_1 == other.int_1
				&& short_7 == other.short_7 && Objects.equals(string_3, other.string_3)
				&& Objects.equals(string_null_4, other.string_null_4);
	}
	
	
}
