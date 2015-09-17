package de.econaxy.shared.transform

import groovyjarjarasm.asm.Opcodes
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.messages.SyntaxErrorMessage
import org.codehaus.groovy.syntax.SyntaxException
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.TYPE])
@GroovyASTTransformationClass("ModelTransformation")
public @interface Model {
    String id()
    String type() default '' // Classname
}

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class ModelTransformation implements ASTTransformation {
    private boolean checkNode(astNodes, annotationType, nodeType) {
        if (!astNodes)
            return false
        if (!astNodes[0]) return false
        if (!astNodes[1]) return false
        if (!(astNodes[0] instanceof AnnotationNode))
            return false
        if (!astNodes[0].classNode?.name == annotationType) return false
        if (!(nodeType.isInstance(astNodes[1])))
            return false
        return true
    }

    public void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        if (!checkNode(astNodes, Model.getClass().name, ClassNode)) {
            addError("Internal error on annotation", astNodes[0], sourceUnit)
            return
        }
        ClassNode annotatedClass = astNodes[1]

        ClassNode modelBase = ClassHelper.makeWithoutCaching('de.econaxy.shared.models.ModelBase')
        annotatedClass.setSuperClass(modelBase)

        AnnotationNode annotationNode = astNodes.first()
        // public static final _ID
        annotatedClass.addField(new FieldNode('_ID', Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, ClassHelper.make(String), annotatedClass, new ConstantExpression(annotationNode.getMember('id').text)))
        // public static final _TYPE
        annotatedClass.addField(new FieldNode('_TYPE', Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, ClassHelper.make(String), annotatedClass, new ConstantExpression(annotationNode.getMember('type')?.text ?: annotatedClass.nameWithoutPackage)))

        annotatedClass.properties.each { property ->
            annotatedClass.addField(new FieldNode(constantName(property.name), Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, ClassHelper.make(String), annotatedClass, new ConstantExpression(property.name)))
        }
    }

    private String constantName(String name) {
        boolean lastWasLower = false
        StringBuilder builder = new StringBuilder()
        for (String letter : name) {
            if (letter.charAt(0).isUpperCase()) {
                if (lastWasLower)
                    builder.append('_')
                lastWasLower = false
            } else
                lastWasLower = true
            builder.append(letter.toUpperCase())
        }
        return builder.toString()
    }

    public void addError(String msg, ASTNode expr, SourceUnit source) {
        int line = expr.lineNumber
        int col = expr.columnNumber
        SyntaxException se = new SyntaxException(msg + '\n', line, col)
        SyntaxErrorMessage sem = new SyntaxErrorMessage(se, source)
        source.errorCollector.addErrorAndContinue(sem)
    }
}
