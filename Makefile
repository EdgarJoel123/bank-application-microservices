.PHONY: run submit

run:
	{ \
		mvn -f account/pom.xml -Dtest=sampleTest test || true; \
		mvn -f client/pom.xml -Dtest=sampleTest test || true; \
		REPORTS=$$(find account/target/surefire-reports client/target/surefire-reports -type f -name 'TEST-*.xml' 2>/dev/null); \
		if [ -z "$$REPORTS" ]; then \
			echo "No se encontraron reportes JUnit"; \
			exit 1; \
		fi; \
		junit-merge -o xunitreport.xml $$REPORTS; \
	}

submit:
	{ \
		mvn -f account/pom.xml test || true; \
		mvn -f client/pom.xml test || true; \
		REPORTS=$$(find account/target/surefire-reports client/target/surefire-reports -type f -name 'TEST-*.xml' 2>/dev/null); \
		if [ -z "$$REPORTS" ]; then \
			echo "No se encontraron reportes JUnit"; \
			exit 1; \
		fi; \
		junit-merge -o xunitreport.xml $$REPORTS; \
	}