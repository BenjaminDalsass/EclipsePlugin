package org.deltadore.planet.plugin.actions.creations;

import java.util.List;
import java.util.ResourceBundle;

import org.deltadore.planet.plugin.actions.projet.C_ActionProjetPlanetAbstraite;
import org.deltadore.planet.swt.C_VerificateurSaisie;
import org.deltadore.planet.swt.E_NotificationType;
import org.deltadore.planet.tools.C_ToolsSWT;
import org.deltadore.planet.tools.C_ToolsWorkbench;
import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ShellPool;

public class C_ActionCreationGabaritFonction extends C_ActionProjetPlanetAbstraite 
{
	public C_ActionCreationGabaritFonction() 
	{
		super("Création gabarit de fonction");
	}

	@Override
	public void f_LANCEMENT(IJavaProject projet, IJobChangeListener listener)
	{
		f_AFFICHE_FENETRE_SAISIE_FONCTION(projet);
	}
	
	@Override
	public ImageDescriptor f_GET_IMAGE_DESCRIPTOR() 
	{
		return C_ToolsSWT.f_GET_IMAGE_DESCRIPTOR("reference.png");
	}
	
	private boolean f_AFFICHE_FENETRE_SAISIE_FONCTION(final IJavaProject projet)
	{
		final Shell shell = new Shell(m_window.getShell(), SWT.TITLE | SWT.APPLICATION_MODAL);
		shell.setSize(400, 300) ;
		shell.setText("Création d'un gabarit de fonction");
		shell.setLayout(new FillLayout());
		
		// layout
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);
		
		// numéro fonction
		Label labelNumeroFonction = new Label(shell, SWT.NONE);
		labelNumeroFonction.setText("Numéro:");
		final Text c_fieldNumero = new Text(shell, SWT.BORDER);
		c_fieldNumero.addVerifyListener(new C_VerificateurSaisie("[0-9]{0,5}"));
		c_fieldNumero.forceFocus();
		
		// nom site
		Label labelNomSite = new Label(shell, SWT.NONE);
		labelNomSite.setText("Libellé:");
		final Text c_fieldNom = new Text(shell, SWT.BORDER);
		C_VerificateurSaisie verificateur = new C_VerificateurSaisie("[0-9a-zA-Z_]{0,35}");
		verificateur.f_SET_SAISIE_MAJUSCULE(true);
		c_fieldNom.addVerifyListener(verificateur);
		C_ToolsSWT.f_GRIDLAYOUT_DATA(c_fieldNom, 1, 1, true, false, GridData.FILL, GridData.BEGINNING);
		
		// bouton
		Button btnValider = new Button(shell, SWT.NONE);
		btnValider.setText("Valider");
		btnValider.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				shell.setVisible(false);
				try 
				{
					NullProgressMonitor monitor = new NullProgressMonitor();
					
					if(c_fieldNumero.getText().length() > 0
					&& c_fieldNom.getText().length() > 0)
					{
						AST ast = AST.newAST(AST.JLS3);
						
						IPackageFragmentRoot root = projet.getPackageFragmentRoot(projet.getProject().getFolder("/src/java"));
						IPackageFragment fragment = root.createPackageFragment("fonctions.fct_" + c_fieldNumero.getText() + "_" + c_fieldNom.getText(), true, monitor);
						
						String nomClass = "C_Module_" + "fct_" + c_fieldNumero.getText();
						
						ICompilationUnit compilUnit = fragment.createCompilationUnit(nomClass + ".java", "", true, monitor);
						compilUnit.createPackageDeclaration(fragment.getElementName(), monitor);
						
						IType type = compilUnit.createType("public class " + nomClass + "\n{\n}", null, true, monitor);
						type.createField("int i;", null, true, monitor);
//						TypeDeclaration type = ast.newTypeDeclaration();
//						type.setInterface(false);
//						type.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
//						type.setName(ast.newSimpleName(nomClass));
//						
//						 MethodDeclaration methodDeclaration = ast.newMethodDeclaration();
//						 methodDeclaration.setConstructor(true);
						  
						compilUnit.save(new NullProgressMonitor(), true);
						
						C_ToolsSWT.f_NOTIFICATION(E_NotificationType.SUCCESS, "Test", "compilation unit");
					}
				} catch (JavaModelException e) 
				{
					e.printStackTrace();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}
		});
		
		shell.setVisible(true);
		
		   while (!shell.isDisposed()) {
			      if (!m_window.getShell().getDisplay().readAndDispatch()) {
			        // If no more entries in event queue
			    	  m_window.getShell().getDisplay().sleep();
			      }
			    }
		   
		
		return true;
	}
	
	
	private void f_TEST_CREATION_CLASSE(int numFonction , String nomFonction)
	{
		// création modèle AST
		AST ast = AST.newAST(AST.JLS3);
	  
		// compilation unit
		CompilationUnit unit = ast.newCompilationUnit();
		
		// package déclaration
		PackageDeclaration packageDeclaration = ast.newPackageDeclaration();
		QualifiedName packageName = ast.newQualifiedName(ast.newSimpleName("fonctions"), ast.newSimpleName("util"));
		packageDeclaration.setName(ast.newSimpleName("example"));
		unit.setPackage(packageDeclaration);
		
		// import
		ImportDeclaration importDeclaration = ast.newImportDeclaration();
		QualifiedName name = ast.newQualifiedName(ast.newSimpleName("java"), ast.newSimpleName("util"));
		importDeclaration.setName(name);
		importDeclaration.setOnDemand(true);
		List<ImportDeclaration> importsUnit = unit.imports();
		importsUnit.add(importDeclaration);
		
		// type
		TypeDeclaration type = ast.newTypeDeclaration();
		type.setInterface(false);
		List<IExtendedModifier> modifiersType = type.modifiers();
		modifiersType.add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		type.setName(ast.newSimpleName("HelloWorld"));
		
		// méthode
		MethodDeclaration methodDeclaration = ast.newMethodDeclaration();
		methodDeclaration.setConstructor(false);
		List<IExtendedModifier> modifiersMethod = methodDeclaration.modifiers();
		modifiersMethod.add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		modifiersMethod.add(ast.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
		methodDeclaration.setName(ast.newSimpleName("main"));
		methodDeclaration.setReturnType2(ast.newPrimitiveType(PrimitiveType.VOID));
		
		// variables déclaration
		SingleVariableDeclaration variableDeclaration = ast.newSingleVariableDeclaration();
		variableDeclaration.setType(ast.newArrayType(ast.newSimpleType(ast.newSimpleName("String"))));
		variableDeclaration.setName(ast.newSimpleName("args"));
		List<SingleVariableDeclaration> methodDeclarationParameters = methodDeclaration.parameters();
		methodDeclarationParameters.add(variableDeclaration);
		
		// block
		Block block = ast.newBlock();
		
		// invocation
		MethodInvocation methodInvocation = ast.newMethodInvocation();
		name = ast.newQualifiedName(ast.newSimpleName("System"), ast.newSimpleName("out"));		
		methodInvocation.setExpression(name);
		methodInvocation.setName(ast.newSimpleName("println")); 
		
		// + opeation
		InfixExpression infixExpression = ast.newInfixExpression();
		infixExpression.setOperator(InfixExpression.Operator.PLUS);
		StringLiteral literal = ast.newStringLiteral();
		// left operand
		literal.setLiteralValue("Hello");
		infixExpression.setLeftOperand(literal);
		// right operand
		literal = ast.newStringLiteral();
		literal.setLiteralValue(" world");
		infixExpression.setRightOperand(literal);
		
		// ajout argument
		List<Expression> methodInvocationExpression = methodInvocation.arguments();
		methodInvocationExpression.add(infixExpression);
		
		ExpressionStatement expressionStatement = ast.newExpressionStatement(methodInvocation);
		List<Statement> blockStatement = block.statements();
		blockStatement.add(expressionStatement);
		methodDeclaration.setBody(block);
		
		List<BodyDeclaration> typeBodyDeclaration = type.bodyDeclarations();
		typeBodyDeclaration.add(methodDeclaration);
		
		List<AbstractTypeDeclaration> unitTypes = unit.types();
		unitTypes.add(type);
	}
}
