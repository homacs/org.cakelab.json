package org.cakelab.json.codec.samples;

public class Version {
	int major;
	int minor;
	
	public Version(String s) {
		set(s);
	}

	@Override
	public String toString() {
		return "" + major + "." + minor;
	}

	public void set(String s) {
		String[] numbers = s.split("\\.");
		major = Integer.valueOf(numbers[0]);
		minor = Integer.valueOf(numbers[1]);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + major;
		result = prime * result + minor;
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
		Version other = (Version) obj;
		if (major != other.major)
			return false;
		if (minor != other.minor)
			return false;
		return true;
	}

	
	
}
