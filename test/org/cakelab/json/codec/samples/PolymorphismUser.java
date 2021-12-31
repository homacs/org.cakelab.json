package org.cakelab.json.codec.samples;

import java.util.Objects;

public class PolymorphismUser {
	public MySuperClass ref_on_A_or_B;

	@Override
	public int hashCode() {
		return Objects.hash(ref_on_A_or_B);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PolymorphismUser other = (PolymorphismUser) obj;
		return Objects.equals(ref_on_A_or_B, other.ref_on_A_or_B);
	}
	
	
}