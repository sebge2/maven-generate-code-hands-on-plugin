package be.sgerard.maven;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import javax.lang.model.element.Modifier;
import java.io.*;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GeneratorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "packageName", defaultValue = "be.sgerard.generated")
    private String packageName;

    @Parameter(property = "outputDirectory", defaultValue = "${project.build.directory}/generated-sources/business-rules")
    private String outputDirectory;

    @Override
    public void execute() {
        final JavaFile sampleClass = createSampleClass();

        writeClass(sampleClass, new File(outputDirectory));

        project.addCompileSourceRoot(outputDirectory);

        getLog().info("Classes generated in " + outputDirectory + ".");
    }

    private void writeClass(JavaFile clazz, File file) {
        try {
            clazz.writeToFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JavaFile createSampleClass() {
        final MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();

        final TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(main)
                .build();

        return JavaFile.builder(packageName, helloWorld)
                .build();
    }
}
