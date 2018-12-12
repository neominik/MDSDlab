package edu.mdsd.mpl.compiler;

import static edu.mdsd.mil.util.MILCreationUtil.createAddInstruction;
import static edu.mdsd.mil.util.MILCreationUtil.createDivInstruction;
import static edu.mdsd.mil.util.MILCreationUtil.createLoadInstruction;
import static edu.mdsd.mil.util.MILCreationUtil.createMILModel;
import static edu.mdsd.mil.util.MILCreationUtil.createMulInstruction;
import static edu.mdsd.mil.util.MILCreationUtil.createPrint;
import static edu.mdsd.mil.util.MILCreationUtil.createStoreInstruction;
import static edu.mdsd.mil.util.MILCreationUtil.createSubInstruction;
import static edu.mdsd.mpl.compiler.StreamUtil.stream;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import edu.mdsd.mil.Instruction;
import edu.mdsd.mil.MILModel;
import edu.mdsd.mpl.mpl.AddExpression;
import edu.mdsd.mpl.mpl.ArithmeticExpression;
import edu.mdsd.mpl.mpl.Assignment;
import edu.mdsd.mpl.mpl.DivisionExpression;
import edu.mdsd.mpl.mpl.Expression;
import edu.mdsd.mpl.mpl.ExpressionStatement;
import edu.mdsd.mpl.mpl.Form;
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
import edu.mdsd.mpl.mpl.Variable;
import edu.mdsd.mpl.mpl.VariableDeclaration;
import edu.mdsd.mpl.mpl.VariableReference;

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
		return Optional.ofNullable(declaration.getInitialValue()).map(expression -> {
			Stream<Instruction> compiledExpression = compile(expression);
			Stream<Instruction> compiledVariable = compile(declaration.getVariable());
			return stream(compiledExpression, compiledVariable);
		}).orElseGet(Stream::empty);
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
		return stream(createLoadInstruction(value.getRawValue()));
	}

	private Stream<Instruction> compile(VariableReference reference) {
		return stream(createLoadInstruction(reference.getVariable().getName()));
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

	private Stream<Instruction> compileArithmeticExpression(ArithmeticExpression exp, Instruction inst) {
		return stream(compile(exp.getOperand1()), compile(exp.getOperand2()), inst);
	}

	private Stream<Instruction> compile(NegateExpression neg) {
		return stream(createLoadInstruction(0), compile(neg.getOperand()), createSubInstruction());
	}

	private Stream<Instruction> compile(ParenExpression par) {
		return compile(par.getOperand());
	}

	private Stream<Instruction> compile(Variable variable) {
		return stream(createStoreInstruction(variable.getName()));
	}

	private Stream<Instruction> compile(Statement statement) {
		Form form = statement.getForm();
		switch (form.eClass().getClassifierID()) {
		case MPLPackage.ASSIGNMENT:
			return compile((Assignment) form);
		case MPLPackage.EXPRESSION_STATEMENT:
			return compile((ExpressionStatement) form);
		}
		return unsupported(statement);
	}

	private Stream<Instruction> compile(Assignment assignment) {
		return stream(compile(assignment.getRightHandSide()), compile(assignment.getLeftHandSide().getVariable()));
	}

	private Stream<Instruction> compile(ExpressionStatement statement) {
		return stream(compile(statement.getExpression()), createStoreInstruction());
	}

	private Stream<Instruction> compileOperation(Operation operation) {
		return unsupported(operation);
	}

	private Stream<Instruction> unsupported(Object o) {
		return Stream.of(createPrint("Unsupported construct: " + o.getClass() + "\n"));
	}
}
