package edu.mdsd.mil.interpreter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import edu.mdsd.mil.AddInstruction;
import edu.mdsd.mil.ConstantInteger;
import edu.mdsd.mil.Instruction;
import edu.mdsd.mil.LoadInstruction;
import edu.mdsd.mil.MILModel;
import edu.mdsd.mil.MILPackage;
import edu.mdsd.mil.RegisterReference;
import edu.mdsd.mil.StoreInstruction;
import edu.mdsd.mil.Value;

public class MILInterpreter {

	private int pc = 0;
	private Stack<Integer> operandStack = new Stack<>();
	private Map<String, Integer> register = new HashMap<>();

	public void interpretAndPrintResult(MILModel model) {
		Map<String, Integer> result = interpret(model);

		System.out.println("Results:");
		result.forEach((address, rawValue) -> System.out.println(address + " = " + rawValue));
	}

	public Map<String, Integer> interpret(MILModel model) {
		List<Instruction> instructions = model.getInstructions();
		for (; pc < instructions.size(); pc++) {
			Instruction instruction = instructions.get(pc);
			interpretSingle(instruction);
		}
		return register;
	}

	private void interpretSingle(Instruction instruction) {
		switch (instruction.eClass().getClassifierID()) {
		case MILPackage.ADD_INSTRUCTION:
			interpret((AddInstruction) instruction);
			break;
		case MILPackage.LOAD_INSTRUCTION:
			interpret((LoadInstruction) instruction);
			break;
		case MILPackage.STORE_INSTRUCTION:
			interpret((StoreInstruction) instruction);
			break;
		default:
			throw new UnsupportedOperationException();
		}
	}

	private void interpret(AddInstruction instruction) {
		int operand2 = popFromOperandStack();
		int operand1 = popFromOperandStack();
		int result = operand1 + operand2;
		pushOnOperandStack(result);
	}

	private void interpret(LoadInstruction instruction) {
		Value value = instruction.getValue();
		int rawValue = getRawValue(value);
		pushOnOperandStack(rawValue);
	}

	private void interpret(StoreInstruction instruction) {
		RegisterReference reference = instruction.getRegisterReference();
		int rawValue = popFromOperandStack();

		if (reference != null) {
			String address = reference.getAddress();
			setRegisterValue(address, rawValue);
		}
	}

	private int popFromOperandStack() {
		return operandStack.pop();
	}

	private void pushOnOperandStack(int rawValue) {
		operandStack.push(rawValue);
	}

	private int getRawValue(Value value) {
		if (value instanceof ConstantInteger) {
			return ((ConstantInteger) value).getRawValue();
		} else if (value instanceof RegisterReference) {
			String address = ((RegisterReference) value).getAddress();
			return getRegisterValue(address);
		}
		throw new UnsupportedOperationException();
	}

	private int getRegisterValue(String address) {
		return register.getOrDefault(address, 0);
	}

	private void setRegisterValue(String address, int rawValue) {
		register.put(address, rawValue);
	}
}
