package vault;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import vault.exception.ResourceExceptionMapper;
import vault.health.PersistenceHealthCheck;
import vault.modules.BookResourceModule;
import vault.modules.PersistenceClientModule;
import vault.resource.BookResource;

public class ControllerApp extends Application<MainConfiguration> {

    public static void main(String[] args) throws Exception {
        new ControllerApp().run(args);
    }

    @Override
    public void run(MainConfiguration configuration, Environment environment) {

        /* Resources */
        Injector resourceInjector = Guice.createInjector(new BookResourceModule(), new PersistenceClientModule());
        BookResource resource = resourceInjector.getInstance(BookResource.class);
        environment.jersey().register(resource);

        /* Exception mappers */
        final ResourceExceptionMapper bookMapper = new ResourceExceptionMapper();
        environment.jersey().register(bookMapper);

        /* Health checks */
        PersistenceHealthCheck persistence = resourceInjector.getInstance(PersistenceHealthCheck.class);
        environment.healthChecks().register("Persistence", persistence);
    }
}
