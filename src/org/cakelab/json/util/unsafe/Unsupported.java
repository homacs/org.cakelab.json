package org.cakelab.json.util.unsafe;

public class Unsupported extends UnsafeBase {

	Unsupported() {
	}
	
	@Override
	public Object allocateInstance(Class<?> cls) throws InstantiationException {
		throw new UnsupportedOperationException();
	}

}
