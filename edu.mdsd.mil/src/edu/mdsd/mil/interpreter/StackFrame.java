package edu.mdsd.mil.interpreter;

import java.util.HashMap;
import java.util.Map;

public class StackFrame {
	private final int returnAddress;
	private final Map<String, Integer> register = new HashMap<>();

	public StackFrame(int returnAddress) {
		this.returnAddress = returnAddress;
	}

	public int getReturnAddress() {
		return returnAddress;
	}

	public Map<String, Integer> getRegister() {
		return register;
	}
}
