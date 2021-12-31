package org.cakelab.json.codec.samples;

import java.util.Objects;

public class ObjectInObject {
	Primitives m_1;
	
	public ObjectInObject() {
		 m_1 = new Primitives();
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_1);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObjectInObject other = (ObjectInObject) obj;
		return Objects.equals(m_1, other.m_1);
	}
	
	
}

