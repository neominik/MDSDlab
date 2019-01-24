build:
	rm -rf target/classes/
	mkdir classes
	clj -e "(compile 'edu.mdsd.symbolic.api)"
	mv classes target/classes
	myvn package
	mv target/symbolic-execution-0.1.0.jar ../edu.mdsd.mil.resource.mil/lib/symbolic.jar
