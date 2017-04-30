package com.cityu.xy;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.xml.crypto.Data;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.DepthWalk.Commit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class test {
	private static Git git = null;

	public static void main(String[] args) throws RefAlreadyExistsException, RefNotFoundException,
			InvalidRefNameException, CheckoutConflictException, GitAPIException {
		// CompilationUnit unit =
		// JdtAstUtil.getCompilationUnit("sourceFile/f8e2a40 PreDestroy.java");
		// System.out.println(unit);
		// List<CompilationUnit> compilationUnits = unit.types();
		//
		// TypeDeclaration type = (TypeDeclaration) unit.types().get(0);
		// MethodDeclaration[] methodDeclarations = type.getMethods();
		//
		// for(MethodDeclaration methodDeclaration : methodDeclarations){
		// System.out.println(methodDeclaration.getName());
		// }
		//
		// TypeDeclaration[] types = type.getTypes();
		//
		// System.out.println(type.getName());

		// for(MethodDeclaration method : methodDeclarations){
		// System.out.println(method.getName());
		//
		// Block block = method.getBody();
		//
		// List statement = block.statements();
		//
		// Statement statement2 = (Statement) statement.get(0);
		//
		// System.out.println(block.statements());
		//
		//// System.out.println(method.getBody());
		//
		// System.out.println("--------------------------------------------");
		//
		// }

		// FieldDeclaration[] fieldDeclarations = type.getFields();
		//
		//
		// for(FieldDeclaration field : fieldDeclarations){
		// System.out.println(field.fragments().get(0));
		// }

		// System.out.println(methodDeclarations);

		try {
//			Repository existingRepo = new FileRepositoryBuilder()
//					.setGitDir(new File("/Users/xiaoyan/eclipse.platform.ui/")).readEnvironment().findGitDir().build();
//			git = new Git(existingRepo);

//			git = Git.open(new File("/Users/xiaoyan/eclipse.platform.ui/"));

//			long startTime = System.currentTimeMillis();
//			Repository repository = git.getRepository();
//			Ref ref = repository.getRef("657bd90");
//			System.out.println(ref.getName());

//			CheckoutCommand checkoutCommand = git.checkout();
//			checkoutCommand
//					.addPath("bundles/org.eclipse.ui.ide/src/org/eclipse/ui/internal/ide/IDEWorkbenchMessages.java");
//			checkoutCommand.setAllPaths(true);
//			checkoutCommand.setStartPoint("d2fca74~1");
//			checkoutCommand.call();
			
//			Iterable<RevCommit> history = git.log().addPath("bundles/org.eclipse.ui.ide/src/org/eclipse/ui/internal/ide/IDEWorkbenchMessages.java").call();
//			Iterator<RevCommit> iterator = history.iterator();
//			while (iterator.hasNext()) {
//				RevCommit commit = iterator.next();
//				System.out.println(commit.getId());
//				
//				System.out.println("----------------------------------------");
//			}
//			
//			System.out.println("time cost:" + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (git != null) {
				git.close();
			}
		}
		
		SimpleDateFormat format  = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date1;
		try {
			date1 = format.parse("2013-12-04 19:43:22");
			System.out.println(date1.getTime());

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date date = new Date(1002800000 * 1000L);
		
		System.out.println(date);
	}

}
