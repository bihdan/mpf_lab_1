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
                .resideInAPackage("..persistence..")
                .orShould().dependOnClassesThat()
                .resideInAPackage("..web..")
                .check(imported);
    }
}