package no.jenjon13.eeexam.ejb.abstracts;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public abstract class EntityEJBIT {
    private static EJBContainer ec;
    private static Context ctx;

    @BeforeClass
    public static void initContainer() throws Exception {
        final Path testClassesMeta = new File("target/test-classes/META-INF/persistence.xml").toPath();
        final Path classesMeta = new File("target/classes/META-INF/persistence.xml").toPath();
        Files.copy(testClassesMeta, classesMeta, StandardCopyOption.REPLACE_EXISTING);

        Map<String, Object> properties = new HashMap<>();
        properties.put(EJBContainer.MODULES, new File("target/classes"));
        ec = EJBContainer.createEJBContainer(properties);
        ctx = ec.getContext();
    }

    @AfterClass
    public static void closeContainer() throws Exception {
        if (ctx != null)
            ctx.close();
        if (ec != null)
            ec.close();
    }

    public static <T> T getEJB(Class<T> clazz) {
        try {
            return (T) ctx.lookup("java:global/classes/" + clazz.getSimpleName() + "!" + clazz.getName());
        } catch (NamingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
