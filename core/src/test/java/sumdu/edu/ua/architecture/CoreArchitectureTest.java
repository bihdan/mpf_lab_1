package sumdu.edu.ua.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class CoreArchitectureTest {

    @Test
    void coreShouldBeIndependent() {
        JavaClasses imported = new ClassFileImporter().importPackages("sumdu.edu.ua");

        noClasses()
                .that().resideInAPackage("..core..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("sumdu.edu.ua.persistence..", "sumdu.edu.ua.web..")
                .because("Core must be independent of local persistence and web implementations")
                .check(imported);

        noClasses()
                .that().resideInAPackage("..core..")
                .should().dependOnClassesThat()
                .resideInAPackage("java.sql..")
                .because("Direct JDBC usage (java.sql) is forbidden in core after switching to JPA")
                .check(imported);
    }
}