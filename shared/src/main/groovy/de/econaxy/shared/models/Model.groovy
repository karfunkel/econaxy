package de.econaxy.shared.models

import de.econaxy.PM
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.builder.AstBuilder
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
@GroovyASTTransformationClass("de.econaxy.shared.models.ModelTransformation")
public @interface Model {}

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
        println 'Start visit'
        if (!checkNode(astNodes, Model.getClass().name, ClassNode)) {
            addError("Internal error on annotation", astNodes[0], sourceUnit)
            return
        }
        ClassNode annotatedClass = astNodes[1]
        ClassNode constantsClass = createInnerClass(PM.name, annotatedClass)
        annotatedClass.module.addClass(constantsClass)
    }

    ClassNode createInnerClass(String outerClassName, ClassNode annotatedClass) {
        def hiddenProperties = ['class', 'attributeNames', 'attributes', 'propertyChangeListeners']

        def innerClassFullName = outerClassName + '$' + annotatedClass.nameWithoutPackage

        AstBuilder builder = new AstBuilder()
        ClassNode innerClass = builder.buildFromSpec {
            innerClass(innerClassFullName, ClassNode.ACC_PUBLIC | ClassNode.ACC_STATIC) {
                classNode(outerClassName, ClassNode.ACC_PUBLIC | ClassNode.ACC_STATIC) {
                    classNode Object
                    interfaces { classNode GroovyObject }
                    mixins {}
                }
                classNode Object
                interfaces { classNode GroovyObject }
                mixins {}
            }
        }.first()

        String code = 'class Temp {'
        code += 'public ' + outerClassName + ' this$0'
        code += annotatedClass.properties.findAll { !hiddenProperties.contains(it.name) }.collect {
            "String ${it.name} = '${it.name}'"
        }.join('\n')
        code += '}'

        ClassNode temp = builder.buildFromString(code).last()
        temp.properties.each() { innerClass.addProperty(it) }
        temp.fields.each() { innerClass.addField(it) }
        temp.methods.each() { innerClass.addMethod(it) }

        return innerClass
    }

    public void addError(String msg, ASTNode expr, SourceUnit source) {
        int line = expr.lineNumber
        int col = expr.columnNumber
        SyntaxException se = new SyntaxException(msg + '\n', line, col)
        SyntaxErrorMessage sem = new SyntaxErrorMessage(se, source)
        source.errorCollector.addErrorAndContinue(sem)
    }
}
