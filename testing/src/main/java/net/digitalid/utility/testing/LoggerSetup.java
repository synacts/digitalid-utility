package net.digitalid.utility.testing;

import java.io.File;
import java.io.FileNotFoundException;

import javax.annotation.Nonnull;

import net.digitalid.utility.annotations.method.Impure;
import net.digitalid.utility.file.Files;
import net.digitalid.utility.logging.Level;
import net.digitalid.utility.logging.exceptions.InvalidConfigurationException;
import net.digitalid.utility.logging.filter.ConfigurationBasedLoggingFilter;
import net.digitalid.utility.logging.filter.LoggingFilter;
import net.digitalid.utility.logging.filter.LoggingRule;
import net.digitalid.utility.logging.logger.FileLogger;
import net.digitalid.utility.logging.logger.Logger;
import net.digitalid.utility.validation.annotations.file.path.Absolute;
import net.digitalid.utility.validation.annotations.type.Stateless;

import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * This class sets the output file of the logger.
 */
@Stateless
public abstract class LoggerSetup extends Assert {
    
    /**
     * Initializes the output file of the logger.
     */
    @Impure
    @BeforeClass
    public static void initializeLogger() throws InvalidConfigurationException, FileNotFoundException {
        final @Nonnull @Absolute File projectDirectory = new File("").getAbsoluteFile();
        final @Nonnull String callerPrefix = "net.digitalid." + projectDirectory.getParentFile().getName() + "." + projectDirectory.getName();
        LoggingFilter.filter.set(ConfigurationBasedLoggingFilter.with(Files.relativeToWorkingDirectory("config/TestingLogging.conf"), LoggingRule.with(Level.VERBOSE, callerPrefix), LoggingRule.with(Level.INFORMATION)));
        Logger.logger.set(FileLogger.with(Files.relativeToWorkingDirectory("target/test-logs/test.log")));
    }
    
}
