package edu.mdsd.mil.util;

import edu.mdsd.mil.AddInstruction;
import edu.mdsd.mil.ConstantInteger;
import edu.mdsd.mil.DivInstruction;
import edu.mdsd.mil.EqInstruction;
import edu.mdsd.mil.GeqInstruction;
import edu.mdsd.mil.GtInstruction;
import edu.mdsd.mil.JmpInstruction;
import edu.mdsd.mil.JpcInstruction;
import edu.mdsd.mil.LabelInstruction;
import edu.mdsd.mil.LeqInstruction;
import edu.mdsd.mil.LoadInstruction;
import edu.mdsd.mil.LtInstruction;
import edu.mdsd.mil.MILFactory;
import edu.mdsd.mil.MILModel;
import edu.mdsd.mil.MulInstruction;
import edu.mdsd.mil.NeqInstruction;
import edu.mdsd.mil.PrtInstruction;
import edu.mdsd.mil.RegisterReference;
import edu.mdsd.mil.StoreInstruction;
import edu.mdsd.mil.SubInstruction;

public class MILCreationUtil {
	private static final MILFactory FACTORY = MILFactory.eINSTANCE;

	public static MILModel createMILModel() {
		return FACTORY.createMILModel();
	}

	public static LoadInstruction createLoadInstruction(int value) {
		LoadInstruction inst = FACTORY.createLoadInstruction();
		ConstantInteger val = FACTORY.createConstantInteger();
		val.setRawValue(value);
		inst.setValue(val);
		return inst;
	}

	public static LoadInstruction createLoadInstruction(String name) {
		LoadInstruction inst = FACTORY.createLoadInstruction();
		RegisterReference val = FACTORY.createRegisterReference();
		val.setAddress(name);
		inst.setValue(val);
		return inst;
	}

	public static StoreInstruction createStoreInstruction(String addr) {
		StoreInstruction inst = FACTORY.createStoreInstruction();
		RegisterReference ref = FACTORY.createRegisterReference();
		ref.setAddress(addr);
		inst.setRegisterReference(ref);
		return inst;
	}

	public static StoreInstruction createStoreInstruction() {
		return FACTORY.createStoreInstruction();
	}

	public static AddInstruction createAddInstruction() {
		return FACTORY.createAddInstruction();
	}

	public static SubInstruction createSubInstruction() {
		return FACTORY.createSubInstruction();
	}

	public static MulInstruction createMulInstruction() {
		return FACTORY.createMulInstruction();
	}

	public static DivInstruction createDivInstruction() {
		return FACTORY.createDivInstruction();
	}

	public static LabelInstruction createLabelInstruction(String name) {
		LabelInstruction inst = FACTORY.createLabelInstruction();
		inst.setName(name);
		return inst;
	}

	public static JmpInstruction createJmpInstruction(LabelInstruction label) {
		JmpInstruction inst = FACTORY.createJmpInstruction();
		inst.setLabel(label);
		return inst;
	}

	public static JpcInstruction createJpcInstruction(LabelInstruction label) {
		JpcInstruction inst = FACTORY.createJpcInstruction();
		inst.setLabel(label);
		return inst;
	}

	public static EqInstruction createEqInstruction() {
		return FACTORY.createEqInstruction();
	}

	public static NeqInstruction createNeqInstruction() {
		return FACTORY.createNeqInstruction();
	}

	public static LtInstruction createLtInstruction() {
		return FACTORY.createLtInstruction();
	}

	public static GtInstruction createGtInstruction() {
		return FACTORY.createGtInstruction();
	}

	public static LeqInstruction createLeqInstruction() {
		return FACTORY.createLeqInstruction();
	}

	public static GeqInstruction createGeqInstruction() {
		return FACTORY.createGeqInstruction();
	}

	public static PrtInstruction createPrint(String message) {
		PrtInstruction inst = FACTORY.createPrtInstruction();
		inst.setValue(message);
		return inst;
	}
}
