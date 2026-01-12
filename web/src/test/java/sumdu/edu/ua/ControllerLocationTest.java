package sumdu.edu.ua;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "sumdu.edu.ua")
public class ControllerLocationTest {

    @ArchTest
    public static final ArchRule controllersShouldResideOnlyInWeb = classes()
            .that().haveSimpleNameEndingWith("Controller")
            .should().resideInAPackage("..web..")
            .allowEmptyShould(true);
}