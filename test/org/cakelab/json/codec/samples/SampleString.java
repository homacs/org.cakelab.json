package org.cakelab.json.codec.samples;

import java.util.Objects;

public class SampleString {
	private String s;

	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}

	@Override
	public int hashCode() {
		return Objects.hash(s);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SampleString other = (SampleString) obj;
		return Objects.equals(s, other.s);
	}

}
