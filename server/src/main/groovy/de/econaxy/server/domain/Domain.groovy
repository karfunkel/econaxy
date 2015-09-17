package de.econaxy.server.domain

import groovy.beans.BindableASTTransformation
import groovyjarjarasm.asm.Opcodes
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.messages.SyntaxErrorMessage
import org.codehaus.groovy.syntax.SyntaxException
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.AutoCloneASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.TYPE])
@GroovyASTTransformationClass("de.econaxy.server.domain.DomainASTTransformation")
public @interface Domain {
    boolean autoInit() default true
    boolean autoClone() default true
    boolean bindable() default true
}

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class DomainASTTransformation implements ASTTransformation {
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
        if (!checkNode(astNodes, Domain.name, ClassNode)) {
            addError("Internal error on annotation", astNodes[0], sourceUnit)
            return
        }
        AnnotationNode anno = astNodes[0]
        ClassNode annotatedClass = astNodes[1]

        annotatedClass.addInterface(ClassHelper.make(DomainTrait))

        if(anno.getMember('autoInit') ?: true) {
            List<ConstructorNode> constructors = [] + annotatedClass.declaredConstructors
            // create default constructor if none do exist

            if (!constructors) {
                ConstructorNode constructor = new ConstructorNode(Opcodes.ACC_PUBLIC, new BlockStatement())
                annotatedClass.addConstructor(constructor)
                constructors << constructor
            }

            // add init() to all constructors
            for (ConstructorNode constructor : constructors) {
                Statement code = constructor.code
                if (code instanceof BlockStatement) {
                    code.statements.add(0, new ExpressionStatement(new MethodCallExpression(new VariableExpression('this'), 'init', MethodCallExpression.NO_ARGUMENTS)))
                } else {
                    constructor.code = new BlockStatement([
                            new ExpressionStatement(new MethodCallExpression(new VariableExpression('this'), 'init', MethodCallExpression.NO_ARGUMENTS)),
                            code
                    ], new VariableScope())
                }
            }
        }

        if(anno.getMember('autoClone') ?: true) {
            AutoCloneASTTransformation autoCloneASTTransformation = new AutoCloneASTTransformation()
            autoCloneASTTransformation.visit([new AnnotationNode(AutoCloneASTTransformation.MY_TYPE), annotatedClass] as ASTNode[], sourceUnit)
        }

        if(anno.getMember('bindable') ?: true) {
            BindableASTTransformation bindableASTTransformation = new BindableASTTransformation()
            bindableASTTransformation.visit([new AnnotationNode(ClassHelper.make(BindableASTTransformation)), annotatedClass] as ASTNode[], sourceUnit)
        }
    }

    public void addError(String msg, ASTNode expr, SourceUnit source) {
        int line = expr.lineNumber
        int col = expr.columnNumber
        SyntaxException se = new SyntaxException(msg + '\n', line, col)
        SyntaxErrorMessage sem = new SyntaxErrorMessage(se, source)
        source.errorCollector.addErrorAndContinue(sem)
    }
}
