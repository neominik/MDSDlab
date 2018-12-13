package edu.mdsd.mpl.compiler;

import static edu.mdsd.mil.util.MILCreationUtil.createAddInstruction;
import static edu.mdsd.mil.util.MILCreationUtil.createDivInstruction;
import static edu.mdsd.mil.util.MILCreationUtil.createEqInstruction;
import static edu.mdsd.mil.util.MILCreationUtil.createGeqInstruction;
import static edu.mdsd.mil.util.MILCreationUtil.createGtInstruction;
import static edu.mdsd.mil.util.MILCreationUtil.createJmpInstruction;
import static edu.mdsd.mil.util.MILCreationUtil.createJpcInstruction;
import static edu.mdsd.mil.util.MILCreationUtil.createLabelInstruction;
import static edu.mdsd.mil.util.MILCreationUtil.createLeqInstruction;
import static edu.mdsd.mil.util.MILCreationUtil.createLoadInstruction;
import static edu.mdsd.mil.util.MILCreationUtil.createLtInstruction;
import static edu.mdsd.mil.util.MILCreationUtil.createMILModel;
import static edu.mdsd.mil.util.MILCreationUtil.createMulInstruction;
import static edu.mdsd.mil.util.MILCreationUtil.createNeqInstruction;
import static edu.mdsd.mil.util.MILCreationUtil.createPrint;
import static edu.mdsd.mil.util.MILCreationUtil.createStoreInstruction;
import static edu.mdsd.mil.util.MILCreationUtil.createSubInstruction;
import static edu.mdsd.mil.util.MILCreationUtil.createYield;
import static edu.mdsd.mpl.compiler.StreamUtil.stream;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import edu.mdsd.mil.Instruction;
import edu.mdsd.mil.LabelInstruction;
import edu.mdsd.mil.MILModel;
import edu.mdsd.mpl.mpl.AddExpression;
import edu.mdsd.mpl.mpl.ArithmeticExpression;
import edu.mdsd.mpl.mpl.Assignment;
import edu.mdsd.mpl.mpl.Block;
import edu.mdsd.mpl.mpl.Comparison;
import edu.mdsd.mpl.mpl.ComparisonOperator;
import edu.mdsd.mpl.mpl.DivisionExpression;
import edu.mdsd.mpl.mpl.Expression;
import edu.mdsd.mpl.mpl.ExpressionStatement;
import edu.mdsd.mpl.mpl.For;
import edu.mdsd.mpl.mpl.Form;
import edu.mdsd.mpl.mpl.If;
import edu.mdsd.mpl.mpl.LiteralValue;
import edu.mdsd.mpl.mpl.MPLModel;
import edu.mdsd.mpl.mpl.MPLPackage;
import edu.mdsd.mpl.mpl.MultiplyExpression;
import edu.mdsd.mpl.mpl.NegateExpression;
import edu.mdsd.mpl.mpl.Operation;
import edu.mdsd.mpl.mpl.ParenExpression;
import edu.mdsd.mpl.mpl.Program;
import edu.mdsd.mpl.mpl.Statement;
import edu.mdsd.mpl.mpl.SubtractExpression;
import edu.mdsd.mpl.mpl.TraceCall;
import edu.mdsd.mpl.mpl.Variable;
import edu.mdsd.mpl.mpl.VariableDeclaration;
import edu.mdsd.mpl.mpl.VariableReference;
import edu.mdsd.mpl.mpl.While;

public class MPL2MILCompiler {
	public void compileAndSave(Resource resource) {
		long time = System.nanoTime();
		MILModel model = compile(resource);
		long elapsed = System.nanoTime() - time;
		System.out.println("Compiled: " + elapsed / 1000 + " μs");
		URI mplResourceURI = resource.getURI();
		URI milResourceURI = mplResourceURI.trimFileExtension().appendFileExtension("mil");
		ResourceSet resSet = resource.getResourceSet();
		Resource milResource = resSet.createResource(milResourceURI);
		milResource.getContents().add(model);
		try {
			milResource.save(Collections.emptyMap());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public MILModel compile(Resource resource) {
		MPLModel model = (MPLModel) resource.getContents().get(0);
		return compile(model);
	}

	public MILModel compile(MPLModel mplModel) {
		MILModel milModel = createMILModel();

		List<Instruction> instructions = compileMPLModel(mplModel).collect(Collectors.toList());

		milModel.getInstructions().addAll(instructions);
		return milModel;
	}

	private Stream<Instruction> compileMPLModel(MPLModel model) {
		Stream<Instruction> compiledProgram = compileProgram(model.getProgram());
		Stream<Instruction> compiledOperations = model.getOperations().stream().flatMap(this::compileOperation);
		return stream(compiledProgram, compiledOperations);
	}

	private Stream<Instruction> compileProgram(Program program) {
		Stream<Instruction> compiledVarDecls = program.getVariableDeclarations().stream()
				.flatMap(this::compileVariableDeclaration);
		Stream<Instruction> compiledStatements = program.getBody().getStatements().stream().flatMap(this::compile);
		return stream(compiledVarDecls, compiledStatements);
	}

	private Stream<Instruction> compileVariableDeclaration(VariableDeclaration declaration) {
		return Stream.ofNullable(declaration.getInitialValue())
				.flatMap(expression -> stream(compile(expression), compile(declaration.getVariable())));
	}

	private Stream<Instruction> compile(Expression expression) {
		switch (expression.eClass().getClassifierID()) {
		case MPLPackage.LITERAL_VALUE:
			return compile((LiteralValue) expression);
		case MPLPackage.VARIABLE_REFERENCE:
			return compile((VariableReference) expression);
		case MPLPackage.ADD_EXPRESSION:
			return compile((AddExpression) expression);
		case MPLPackage.SUBTRACT_EXPRESSION:
			return compile((SubtractExpression) expression);
		case MPLPackage.MULTIPLY_EXPRESSION:
			return compile((MultiplyExpression) expression);
		case MPLPackage.DIVISION_EXPRESSION:
			return compile((DivisionExpression) expression);
		case MPLPackage.NEGATE_EXPRESSION:
			return compile((NegateExpression) expression);
		case MPLPackage.PAREN_EXPRESSION:
			return compile((ParenExpression) expression);
		}
		return unsupported(expression);
	}

	private Stream<Instruction> compile(LiteralValue value) {
		return createLoadInstruction(value.getRawValue());
	}

	private Stream<Instruction> compile(VariableReference reference) {
		return createLoadInstruction(reference.getVariable().getName());
	}

	private Stream<Instruction> compile(AddExpression add) {
		return compileArithmeticExpression(add, createAddInstruction());
	}

	private Stream<Instruction> compile(SubtractExpression sub) {
		return compileArithmeticExpression(sub, createSubInstruction());
	}

	private Stream<Instruction> compile(MultiplyExpression mul) {
		return compileArithmeticExpression(mul, createMulInstruction());
	}

	private Stream<Instruction> compile(DivisionExpression div) {
		return compileArithmeticExpression(div, createDivInstruction());
	}

	private Stream<Instruction> compileArithmeticExpression(ArithmeticExpression exp, Stream<Instruction> inst) {
		return stream(compile(exp.getOperand1()), compile(exp.getOperand2()), inst);
	}

	private Stream<Instruction> compile(NegateExpression neg) {
		return stream(createLoadInstruction(0), compile(neg.getOperand()), createSubInstruction());
	}

	private Stream<Instruction> compile(ParenExpression par) {
		return compile(par.getOperand());
	}

	private Stream<Instruction> compile(Variable variable) {
		return createStoreInstruction(variable.getName());
	}

	private Stream<Instruction> compile(Statement statement) {
		Form form = statement.getForm();
		switch (form.eClass().getClassifierID()) {
		case MPLPackage.ASSIGNMENT:
			return compile((Assignment) form);
		case MPLPackage.EXPRESSION_STATEMENT:
			return compile((ExpressionStatement) form);
		case MPLPackage.IF:
			return compile((If) form);
		case MPLPackage.FOR:
			return compile((For) form);
		case MPLPackage.WHILE:
			return compile((While) form);
		case MPLPackage.TRACE_CALL:
			return compile((TraceCall) form);
		}
		return unsupported(form);
	}

	private Stream<Instruction> compile(Assignment assignment) {
		return stream(compile(assignment.getRightHandSide()), compile(assignment.getLeftHandSide().getVariable()));
	}

	private Stream<Instruction> compile(ExpressionStatement statement) {
		return stream(compile(statement.getExpression()), createStoreInstruction());
	}

	private Stream<Instruction> compile(If ifForm) {
		LabelInstruction endLabel = createLabelInstruction("endif_" + getSeed(ifForm));
		return stream(compile(ifForm.getCondition()), compileThenElse(ifForm, endLabel), stream(endLabel));
	}

	private Stream<Instruction> compileThenElse(If ifForm, LabelInstruction endLabel) {
		if (ifForm.getElse() != null) {
			LabelInstruction elseLabel = createLabelInstruction("else_" + getSeed(ifForm));
			return stream(createJpcInstruction(elseLabel), compile(ifForm.getThen()), createJmpInstruction(endLabel),
					stream(elseLabel), compile(ifForm.getElse()));
		} else {
			return stream(createJpcInstruction(endLabel), compile(ifForm.getThen()));
		}
	}

	private Stream<Instruction> compile(Block block) {
		return block.getStatements().stream().flatMap(this::compile);
	}

	private Stream<Instruction> compile(Comparison comparison) {
		return stream(compile(comparison.getLeftHandSide()), compile(comparison.getRightHandSide()),
				compile(comparison.getOperator()));
	}

	private Stream<Instruction> compile(ComparisonOperator operator) {
		switch (operator.eClass().getClassifierID()) {
		case MPLPackage.EQ:
			return createEqInstruction();
		case MPLPackage.NE:
			return createNeqInstruction();
		case MPLPackage.LT:
			return createLtInstruction();
		case MPLPackage.GT:
			return createGtInstruction();
		case MPLPackage.LE:
			return createLeqInstruction();
		case MPLPackage.GE:
			return createGeqInstruction();
		}
		return unsupported(operator);
	}

	private Stream<Instruction> compile(For loop) {
		LabelInstruction forLabel = createLabelInstruction("for_" + getSeed(loop));
		LabelInstruction endLabel = createLabelInstruction("endfor_" + getSeed(loop));
		return stream(compile(loop.getFrom()), stream(forLabel), getCondition(loop), createJpcInstruction(endLabel),
				compile(loop.getBody()), getIncrement(loop), createJmpInstruction(forLabel), stream(endLabel));
	}

	private Stream<Instruction> getCondition(For loop) {
		Stream<Instruction> loadInstruction = createLoadInstruction(
				loop.getFrom().getLeftHandSide().getVariable().getName());
		Stream<Instruction> comparator = loop.getDownwards() ? createGtInstruction() : createLtInstruction();
		return stream(loadInstruction, compile(loop.getTo()), comparator);
	}

	private Stream<Instruction> getIncrement(For loop) {
		String var = loop.getFrom().getLeftHandSide().getVariable().getName();
		Stream<Instruction> operation = loop.getDownwards() ? createSubInstruction() : createAddInstruction();
		return stream(createLoadInstruction(var), createLoadInstruction(1), operation, createStoreInstruction(var));
	}

	private Stream<Instruction> compile(While loop) {
		LabelInstruction whileLabel = createLabelInstruction("while_" + getSeed(loop));
		LabelInstruction endLabel = createLabelInstruction("endwhile_" + getSeed(loop));
		return stream(stream(whileLabel), compile(loop.getCondition()), createJpcInstruction(endLabel),
				compile(loop.getBody()), createJmpInstruction(whileLabel), stream(endLabel));
	}

	private Stream<Instruction> compile(TraceCall trace) {
		String var = trace.getVariable().getVariable().getName();
		return stream(createPrint("trace " + var + ": "), createLoadInstruction(var), createYield());
	}

	private Stream<Instruction> compileOperation(Operation operation) {
		return unsupported(operation);
	}

	private Stream<Instruction> unsupported(Object o) {
		return createPrint("Unsupported construct: " + o.getClass() + "\n");
	}

	private String getSeed(Object o) {
		return Integer.toHexString(System.identityHashCode(o));
	}
}
