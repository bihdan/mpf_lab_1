package sumdu.edu.ua.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "sumdu.edu.ua")
public class WebArchitectureTest {

    @ArchTest
    public static final ArchRule controllersShouldResideOnlyInWeb = classes()
            .that().haveSimpleNameEndingWith("Controller")
            .should().resideInAPackage("..web..")
            .allowEmptyShould(true);

    @ArchTest
    public static final ArchRule webLayerDependencies = layeredArchitecture()
            .consideringOnlyDependenciesInAnyPackage("sumdu.edu.ua..")
            .layer("Web").definedBy("..web..")
            .layer("Core").definedBy("..core..")
            .layer("Persistence").definedBy("..persistence..")
            .layer("Infrastructure").definedBy("..infrastructure..")
            .whereLayer("Web").mayOnlyAccessLayers("Core", "Infrastructure")
            .whereLayer("Infrastructure").mayOnlyAccessLayers("Core", "Persistence")
            .allowEmptyShould(true);
}