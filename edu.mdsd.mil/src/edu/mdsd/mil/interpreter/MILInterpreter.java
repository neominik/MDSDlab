package edu.mdsd.mil.interpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.TreeMap;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntUnaryOperator;

import edu.mdsd.mil.CalInstruction;
import edu.mdsd.mil.ConstantInteger;
import edu.mdsd.mil.ErrInstruction;
import edu.mdsd.mil.InpInstruction;
import edu.mdsd.mil.Instruction;
import edu.mdsd.mil.JpcInstruction;
import edu.mdsd.mil.JumpInstruction;
import edu.mdsd.mil.LabelInstruction;
import edu.mdsd.mil.LoadInstruction;
import edu.mdsd.mil.MILModel;
import edu.mdsd.mil.MILPackage;
import edu.mdsd.mil.PrtInstruction;
import edu.mdsd.mil.RegisterReference;
import edu.mdsd.mil.RetInstruction;
import edu.mdsd.mil.StoreInstruction;
import edu.mdsd.mil.Value;

public class MILInterpreter {

	private int pc = 0;
	private Stack<Integer> operandStack = new Stack<>();
	private Stack<StackFrame> callStack = new Stack<>();
	private List<Instruction> instructions;

	private PrintStream out;
	private InputStream in;

	public MILInterpreter(PrintStream out, InputStream in) {
		this.out = out;
		this.in = in;
		callStack.push(new StackFrame(0));
	}

	public void interpretAndPrintResult(MILModel model) {
		Map<String, Integer> result = new TreeMap<>(interpret(model));

		out.println("Results:");
		result.forEach((address, rawValue) -> out.println(address + " = " + rawValue));
	}

	public Map<String, Integer> interpret(MILModel model) {
		instructions = model.getInstructions();
		for (; pc < instructions.size(); pc++) {
			Instruction instruction = instructions.get(pc);
			interpretSingle(instruction);
		}
		return callStack.peek().getRegister();
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
		case MILPackage.INP_INSTRUCTION:
			interpret((InpInstruction) instruction);
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
		case MILPackage.CAL_INSTRUCTION:
			interpret((CalInstruction) instruction);
			break;
		case MILPackage.RET_INSTRUCTION:
			interpret((RetInstruction) instruction);
			break;
		case MILPackage.YLD_INSTRUCTION:
			interpretConsumer(this::print);
			break;
		case MILPackage.PRT_INSTRUCTION:
			interpret((PrtInstruction) instruction);
			break;
		case MILPackage.ERR_INSTRUCTION:
			interpret((ErrInstruction) instruction);
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
		interpretConsumer(v -> Optional.ofNullable(instruction.getRegisterReference())
				.map(RegisterReference::getAddress).ifPresent(address -> setRegisterValue(address, v)));
	}

	private void interpret(InpInstruction instruction) {
		while (true) {
			String range = "[" + instruction.getLowerBound().getRawValue() + ", "
					+ instruction.getUpperBound().getRawValue() + "]";
			print("Please enter a valid int in range " + range + ":\n");
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			try {
				int input = Integer.parseInt(reader.readLine());
				if (outOfRange(input, instruction))
					throw new NumberFormatException(input + " is not within range " + range);
				pushOnOperandStack(input);
				break;
			} catch (NumberFormatException e) {
				print(e + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean outOfRange(int input, InpInstruction instruction) {
		return input < instruction.getLowerBound().getRawValue() || input > instruction.getUpperBound().getRawValue();
	}

	private void interpret(JumpInstruction instruction) {
		LabelInstruction label = instruction.getLabel();
		pc = instructions.indexOf(label);
	}

	private void interpret(JpcInstruction instruction) {
		interpretConsumer(condition -> {
			if (condition == 0)
				interpret((JumpInstruction) instruction);
		});
	}

	private void interpret(CalInstruction instruction) {
		callStack.push(new StackFrame(pc));
		interpret((JumpInstruction) instruction);
	}

	private void interpret(RetInstruction instruction) {
		StackFrame oldFrame = callStack.pop();
		pc = oldFrame.getReturnAddress();
	}

	private void interpret(PrtInstruction instruction) {
		String text = instruction.getValue();
		print(text);
	}

	private void interpret(ErrInstruction instruction) {
		String text = instruction.getValue();
		print(text);
		throw new AssertionError(text);
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
		return callStack.peek().getRegister().getOrDefault(address, 0);
	}

	private void setRegisterValue(String address, int rawValue) {
		callStack.peek().getRegister().put(address, rawValue);
	}

	private void print(int i) {
		out.println(i);
	}

	private void print(String text) {
		out.print(text);
	}

	private int unsupported(Object o) {
		throw new UnsupportedOperationException(o.getClass().toString());
	}
}
