package edu.mdsd.mil.util;

import java.util.stream.Stream;

import edu.mdsd.mil.ConstantInteger;
import edu.mdsd.mil.Instruction;
import edu.mdsd.mil.JmpInstruction;
import edu.mdsd.mil.JpcInstruction;
import edu.mdsd.mil.LabelInstruction;
import edu.mdsd.mil.LoadInstruction;
import edu.mdsd.mil.MILFactory;
import edu.mdsd.mil.MILModel;
import edu.mdsd.mil.PrtInstruction;
import edu.mdsd.mil.RegisterReference;
import edu.mdsd.mil.StoreInstruction;

public class MILCreationUtil {
	private static final MILFactory FACTORY = MILFactory.eINSTANCE;

	public static MILModel createMILModel() {
		return FACTORY.createMILModel();
	}

	public static Stream<Instruction> createLoadInstruction(int value) {
		LoadInstruction inst = FACTORY.createLoadInstruction();
		ConstantInteger val = FACTORY.createConstantInteger();
		val.setRawValue(value);
		inst.setValue(val);
		return stream(inst);
	}

	public static Stream<Instruction> createLoadInstruction(String name) {
		LoadInstruction inst = FACTORY.createLoadInstruction();
		RegisterReference val = FACTORY.createRegisterReference();
		val.setAddress(name);
		inst.setValue(val);
		return stream(inst);
	}

	public static Stream<Instruction> createStoreInstruction(String addr) {
		StoreInstruction inst = FACTORY.createStoreInstruction();
		RegisterReference ref = FACTORY.createRegisterReference();
		ref.setAddress(addr);
		inst.setRegisterReference(ref);
		return stream(inst);
	}

	public static Stream<Instruction> createStoreInstruction() {
		return stream(FACTORY.createStoreInstruction());
	}

	public static Stream<Instruction> createAddInstruction() {
		return stream(FACTORY.createAddInstruction());
	}

	public static Stream<Instruction> createSubInstruction() {
		return stream(FACTORY.createSubInstruction());
	}

	public static Stream<Instruction> createMulInstruction() {
		return stream(FACTORY.createMulInstruction());
	}

	public static Stream<Instruction> createDivInstruction() {
		return stream(FACTORY.createDivInstruction());
	}

	public static LabelInstruction createLabelInstruction(String name) {
		LabelInstruction inst = FACTORY.createLabelInstruction();
		inst.setName(name);
		return inst;
	}

	public static Stream<Instruction> createJmpInstruction(LabelInstruction label) {
		JmpInstruction inst = FACTORY.createJmpInstruction();
		inst.setLabel(label);
		return stream(inst);
	}

	public static Stream<Instruction> createJpcInstruction(LabelInstruction label) {
		JpcInstruction inst = FACTORY.createJpcInstruction();
		inst.setLabel(label);
		return stream(inst);
	}

	public static Stream<Instruction> createEqInstruction() {
		return stream(FACTORY.createEqInstruction());
	}

	public static Stream<Instruction> createNeqInstruction() {
		return stream(FACTORY.createNeqInstruction());
	}

	public static Stream<Instruction> createLtInstruction() {
		return stream(FACTORY.createLtInstruction());
	}

	public static Stream<Instruction> createGtInstruction() {
		return stream(FACTORY.createGtInstruction());
	}

	public static Stream<Instruction> createLeqInstruction() {
		return stream(FACTORY.createLeqInstruction());
	}

	public static Stream<Instruction> createGeqInstruction() {
		return stream(FACTORY.createGeqInstruction());
	}

	public static Stream<Instruction> createPrint(String message) {
		PrtInstruction inst = FACTORY.createPrtInstruction();
		inst.setValue(message);
		return stream(inst);
	}

	public static Stream<Instruction> createYield() {
		return stream(FACTORY.createYldInstruction());
	}

	private static Stream<Instruction> stream(Instruction inst) {
		return Stream.of(inst);
	}
}
