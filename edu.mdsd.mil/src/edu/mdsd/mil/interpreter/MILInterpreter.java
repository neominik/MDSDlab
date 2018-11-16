package edu.mdsd.mil.interpreter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntUnaryOperator;

import edu.mdsd.mil.ConstantInteger;
import edu.mdsd.mil.Instruction;
import edu.mdsd.mil.JpcInstruction;
import edu.mdsd.mil.JumpInstruction;
import edu.mdsd.mil.LabelInstruction;
import edu.mdsd.mil.LoadInstruction;
import edu.mdsd.mil.MILModel;
import edu.mdsd.mil.MILPackage;
import edu.mdsd.mil.PrtInstruction;
import edu.mdsd.mil.RegisterReference;
import edu.mdsd.mil.StoreInstruction;
import edu.mdsd.mil.Value;

public class MILInterpreter {

	private int pc = 0;
	private Stack<Integer> operandStack = new Stack<>();
	private Map<String, Integer> register = new HashMap<>();
	private List<Instruction> instructions;

	public void interpretAndPrintResult(MILModel model) {
		Map<String, Integer> result = new TreeMap<>(interpret(model));

		System.out.println("Results:");
		result.forEach((address, rawValue) -> System.out.println(address + " = " + rawValue));
	}

	public Map<String, Integer> interpret(MILModel model) {
		instructions = model.getInstructions();
		for (; pc < instructions.size(); pc++) {
			Instruction instruction = instructions.get(pc);
			interpretSingle(instruction);
		}
		return register;
	}

	private void interpretSingle(Instruction instruction) {
		switch (instruction.eClass().getClassifierID()) {
		case MILPackage.ADD_INSTRUCTION:
			interpretBinaryOperator((a, b) -> a + b);
			break;
		case MILPackage.SUB_INSTRUCTION:
			interpretBinaryOperator((a, b) -> a - b);
			break;
		case MILPackage.MUL_INSTRUCTION:
			interpretBinaryOperator((a, b) -> a * b);
			break;
		case MILPackage.DIV_INSTRUCTION:
			interpretBinaryOperator((a, b) -> a / b);
			break;
		case MILPackage.LOAD_INSTRUCTION:
			interpret((LoadInstruction) instruction);
			break;
		case MILPackage.STORE_INSTRUCTION:
			interpret((StoreInstruction) instruction);
			break;
		case MILPackage.NEG_INSTRUCTION:
			interpretUnaryOperator(i -> i == 0 ? 1 : 0);
			break;
		case MILPackage.LABEL_INSTRUCTION:
			break;
		case MILPackage.JMP_INSTRUCTION:
			interpret((JumpInstruction) instruction);
			break;
		case MILPackage.JPC_INSTRUCTION:
			interpret((JpcInstruction) instruction);
			break;
		case MILPackage.YLD_INSTRUCTION:
			interpretConsumer(this::print);
			break;
		case MILPackage.PRT_INSTRUCTION:
			interpret((PrtInstruction) instruction);
			break;
		case MILPackage.EQ_INSTRUCTION:
			interpretBinaryOperator((a, b) -> a == b ? 1 : 0);
			break;
		case MILPackage.NEQ_INSTRUCTION:
			interpretBinaryOperator((a, b) -> a != b ? 1 : 0);
			break;
		case MILPackage.LT_INSTRUCTION:
			interpretBinaryOperator((a, b) -> a < b ? 1 : 0);
			break;
		case MILPackage.LEQ_INSTRUCTION:
			interpretBinaryOperator((a, b) -> a <= b ? 1 : 0);
			break;
		case MILPackage.GT_INSTRUCTION:
			interpretBinaryOperator((a, b) -> a > b ? 1 : 0);
			break;
		case MILPackage.GEQ_INSTRUCTION:
			interpretBinaryOperator((a, b) -> a >= b ? 1 : 0);
			break;
		default:
			unsupported(instruction);
		}
	}

	private void interpretBinaryOperator(IntBinaryOperator f) {
		int operand2 = popFromOperandStack();
		int operand1 = popFromOperandStack();
		int result = f.applyAsInt(operand1, operand2);
		pushOnOperandStack(result);
	}

	private void interpretUnaryOperator(IntUnaryOperator f) {
		int operand = popFromOperandStack();
		int result = f.applyAsInt(operand);
		pushOnOperandStack(result);
	}

	private void interpretConsumer(IntConsumer f) {
		int operand = popFromOperandStack();
		f.accept(operand);
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

	private void interpret(JumpInstruction instruction) {
		LabelInstruction label = instruction.getLabel();
		pc = instructions.indexOf(label);
	}

	private void interpret(JpcInstruction instruction) {
		interpretConsumer(operand -> {
			if (operand == 0)
				interpret((JumpInstruction) instruction);
		});
	}

	private void interpret(PrtInstruction instruction) {
		String text = instruction.getValue();
		print(text);
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
		return unsupported(value);
	}

	private int getRegisterValue(String address) {
		return register.getOrDefault(address, 0);
	}

	private void setRegisterValue(String address, int rawValue) {
		register.put(address, rawValue);
	}

	private void print(int i) {
		System.out.println(i);
	}

	private void print(String text) {
		System.out.print(text);
	}

	private int unsupported(Object o) {
		throw new UnsupportedOperationException(o.getClass().toString());
	}
}
